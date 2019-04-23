package com.alam4.frags.manage;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.edit.editalert;
import com.alam4.model.eachgroupalert;
import com.alam4.pref.userpreferences;
import com.alam4.share.sharealert;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class active extends Fragment {


    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String name;
    private userpreferences prefer;
    AlertDialog al;

    public active() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active, container, false);
        swipeRefreshLayout = view.findViewById(R.id.Swipe);
        recyclerView = view.findViewById(R.id.list);
        prefer = new userpreferences(getContext());
        name = prefer.getFirst();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                download();
            }
        });
        download();
        return view;
    }


    private void download() {
        Background background = new Background(getContext(), recyclerView);
        background.execute();

    }

    private class Background extends AsyncTask<String, String, String> {
        Context context;
        RecyclerView listView;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;

        public Background(Context context, RecyclerView listView) {
            this.context = context;
            this.listView = listView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return DownloadData();
        }

        private String DownloadData() {
            stringBuilder = new StringBuilder();
            String line;
            httpURLConnection = new GetConnected().GetConnected();
            try {
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(120000);
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(httpURLConnection.getInputStream())));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
            } catch (ProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

            return stringBuilder.toString();
        }

        protected class GetConnected {
            URL url;
            HttpURLConnection httpU;
            String sign_url = new connection(context).active();

            public HttpURLConnection GetConnected() {
                if (sign_url != null) {
                    try {
                        url = new URL(sign_url);
                        httpU = (HttpURLConnection) url.openConnection();
                    } catch (MalformedURLException e) {
                        return null;
                    } catch (IOException e) {
                        return null;
                    }
                    return httpU;
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                ParserData parserData = new ParserData(context, listView, s);
                parserData.execute(s);

            } else {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
            }
        }

        private class ParserData extends AsyncTask<String, Void, Boolean> {
            Context context;
            RecyclerView recyclerView;
            String jsondata;
            ArrayList<eachgroupalert> arrayList = new ArrayList<>();

            public ParserData(Context context, RecyclerView recyclerView, String jsondata) {
                this.context = context;
                this.recyclerView = recyclerView;
                this.jsondata = jsondata;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                jsondata = params[0];
                return ParseJsonDt();
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
                swipeRefreshLayout.setRefreshing(false);
                if (bool) {
                    AdapterClass adapterClass = new AdapterClass(arrayList, context, recyclerView);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapterClass);
                } else {
                    Toast.makeText(context, "Encountered Error During processing", Toast.LENGTH_LONG).show();
                }
            }


            private Boolean ParseJsonDt() {
                boolean vall = false;
                try {
                    JSONArray jsonArray = new JSONArray(jsondata);
                    JSONObject jsonObject = null;
                    int i = 0;
                    eachgroupalert sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new eachgroupalert();

                        sp.setId(jsonObject.getString("id"));
                        sp.setGroupname(jsonObject.getString("groupname"));
                        sp.setNature(jsonObject.getString("nature"));
                        sp.setNotes(jsonObject.getString("notes"));
                        sp.setAttachment(jsonObject.getString("attachment"));
                        sp.setCode(jsonObject.getString("code"));
                        sp.setLocation(jsonObject.getString("location"));
                        sp.setLevel(jsonObject.getString("level"));
                        sp.setStatus(jsonObject.getString("status"));
                        sp.setPoster(jsonObject.getString("poster"));
                        sp.setName(jsonObject.getString("name"));
                        String lo = jsonObject.getString("poster");
                        if (lo.equalsIgnoreCase(name)) {
                            arrayList.add(sp);
                        }

                        i++;

                    }

                    vall = true;

                } catch (JSONException e) {

                }

                return vall;
            }
        }


    }

    private class AdapterClass extends RecyclerView.Adapter<MyHoder> {
        List<eachgroupalert> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<eachgroupalert> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.active, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHoder myHoder, int i) {
            final eachgroupalert sm = servicemodels.get(i);
            final String id, groupname, nature, notes, attachment, code, location, level, status, poster, name;
            code = sm.getCode();
            nature = sm.getNature();
            notes = sm.getNotes();
            location = sm.getLocation();
            level = sm.getLevel();
            attachment = sm.getAttachment();
            status = sm.getStatus();
            myHoder.Code.setText(code);
            myHoder.Nature.setText(nature);
            myHoder.Notes.setText(notes);
            myHoder.Location.setText(location);
            myHoder.Level.setText(level);
            groupname = sm.getGroupname();
            poster = sm.getPoster();
            name = sm.getName();
            myHoder.Group.setText(groupname);

            String sign_url = new connection(context).getimage();
            if (sign_url != null) {
                String img = sign_url + attachment;
                Picasso.get().load(img).into(myHoder.Attach);
            }
            if (level.equalsIgnoreCase("Level 3")) {
                myHoder.cardView.setCardBackgroundColor(Color.RED);

            }
            if (level.equalsIgnoreCase("Level 2")) {
                myHoder.cardView.setCardBackgroundColor(Color.BLUE);

            }
            if (level.equalsIgnoreCase("Level 1")) {
                myHoder.cardView.setCardBackgroundColor(Color.YELLOW);

            }
            myHoder.neutralize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    neutralizeAlert(code,groupname);
                }
            });
            myHoder.withdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAlert(code);
                }
            });
            myHoder.elevate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elevateAlert(code,level);
                }
            });
            myHoder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editAlert(code);
                }
            });
            myHoder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareAlert(code);
                }
            });
    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try {
            if (servicemodels.size() == 0) {
                arr = 0;
            } else {
                arr = servicemodels.size();
            }
        } catch (Exception e) {
        }
        return arr;
    }
}

    private void shareAlert(String code) {
        Intent  intent=new Intent(getContext(), sharealert.class);
        Bundle bundle=new Bundle();
        bundle.putString("code",code);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void editAlert(String code) {
        Intent  intent=new Intent(getContext(), editalert.class);
        Bundle bundle=new Bundle();
        bundle.putString("code",code);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void elevateAlert(final String code, final String level) {
        if (level.equalsIgnoreCase("Level 3")){
            Toast.makeText(getContext(), "You Can't Elevate this alert", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater lay = LayoutInflater.from(getContext());
            final View viewdata = lay.inflate(R.layout.elevator, null);

            String[] lv = {"Select", "Level 1", "Level 2", "Level 3"};
            final Spinner spinner = viewdata.findViewById(R.id.ele);
            ArrayAdapter<String> array = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, lv);
            spinner.setAdapter(array);
            builder.setView(viewdata);
            builder.setPositiveButton("Elevate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String select = spinner.getSelectedItem().toString().trim();

                    if (level.equalsIgnoreCase(select)) {
                        Toast.makeText(getContext(), "Check selection", Toast.LENGTH_SHORT).show();
                    }
                    if (level.equalsIgnoreCase("Level 2")){
                        if (select.equalsIgnoreCase("Level 3")){
                            accept(code,select);
                        }else{
                            Toast.makeText(getContext(), "Check Selection", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        accept(code,select);
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
               al.dismiss();
                }
            });


            al = builder.create();
            al.show();
        }
    }

    private void accept(String code, String level) {
        class acceptElevate extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Withdrawing", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("level", bitmaps[1]);
                data.put("user", bitmaps[2]);

                String result = rh.postRequest(connection.elevatealert(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Alert Elevated")) {
                    download();
                }
            }
        }

        acceptElevate ui = new acceptElevate();
        ui.execute(code,level,prefer.getUser());
    }

    private void deleteAlert(final String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to withdraw this alert?\nThis is a irreversible activity");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                okWithdraw(code);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void okWithdraw(String code) {
        class Withdraw extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Withdrawing", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("user", bitmaps[1]);

                String result = rh.postRequest(connection.withdraw(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Alert Withdrawn")) {
                    download();
                }
            }
        }

        Withdraw ui = new Withdraw();
        ui.execute(code,prefer.getUser());
    }

    private void neutralizeAlert(final String code, final String groupname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to neutralize this alert?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                okCool(code,groupname);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void okCool(String code, String groupname) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Neutralizing", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("user", bitmaps[1]);
                data.put("groupname", bitmaps[2]);
                String result = rh.postRequest(connection.neutralize(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Neutralized")) {
                    download();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(code,prefer.getUser(),groupname);
    }

    private void activeAlert(String groupname, String poster, String name, String notes, String code) {
        /*Intent  intent=new Intent(eachresponsegroup.this, activealerts.class);
        Bundle bundle=new Bundle();
        bundle.putString("groupname",groupname);
        bundle.putString("poster",poster);
        bundle.putString("name",name);
        bundle.putString("notes",notes);
        bundle.putString("code",code);
        intent.putExtras(bundle);
        startActivity(intent);*/
    }

private class MyHoder extends RecyclerView.ViewHolder {
    TextView Code, Nature, Notes, Location, Level,Group;
    ImageView Attach,neutralize,withdraw,elevate,edit,share;
    CardView cardView;


    public MyHoder(@NonNull View itemView) {
        super(itemView);
        Code = itemView.findViewById(R.id.bb);
        Nature = itemView.findViewById(R.id.cc);
        Notes = itemView.findViewById(R.id.dd);
        Location = itemView.findViewById(R.id.ee);
        Level = itemView.findViewById(R.id.ff);
        Attach = itemView.findViewById(R.id.aa);
        neutralize = itemView.findViewById(R.id.neutralize);
        withdraw = itemView.findViewById(R.id.withdraw);
        elevate = itemView.findViewById(R.id.elevate);
        edit = itemView.findViewById(R.id.edit);
        share = itemView.findViewById(R.id.share);
        Group = itemView.findViewById(R.id.gg);
        cardView = itemView.findViewById(R.id.card);

    }
}
}
