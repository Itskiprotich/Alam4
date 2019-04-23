package com.alam4.each;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.alam4.frags.home;
import com.alam4.model.ratings;
import com.alam4.pref.userpreferences;
import com.alam4.share.sharealert;

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

public class activealerts extends AppCompatActivity {
    ActionBar actionBar;
    String name, groupname, poster, notes, code,level;
    ArrayList<String> arrayList;

    RecyclerView actionholder;
    RecyclerView.LayoutManager mLayoutManager;
    AlertDialog al;
    TextView A, B, C;
    private RecyclerView recyclerView;
    private userpreferences prefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activealerts);
        arrayList = new ArrayList<>();
        name = getIntent().getStringExtra("name");
        groupname = getIntent().getStringExtra("groupname");
        poster = getIntent().getStringExtra("poster");
        notes = getIntent().getStringExtra("notes");
        code = getIntent().getStringExtra("code");
        level = getIntent().getStringExtra("level");
        actionBar = getSupportActionBar();
        actionBar.setTitle("" + groupname);
        prefer = new userpreferences(activealerts.this);

        recyclerView = findViewById(R.id.seeme);
        actionBar.setDisplayHomeAsUpEnabled(true);
        loadMenu();
        A = findViewById(R.id.aa);
        B = findViewById(R.id.bb);
        C = findViewById(R.id.cc);

        A.setText("" + name);
        B.setText("" + poster);
        C.setText("" + notes);
        download();
    }

    private void download() {
        Background background = new Background(activealerts.this, recyclerView);
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
            String sign_url = new connection(context).ratings();

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
            ArrayList<ratings> arrayList = new ArrayList<>();

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

                if (bool) {
                    AdapterClass adapterClass = new AdapterClass(arrayList, context, recyclerView);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                    ratings sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new ratings();

                        sp.setId(jsonObject.getString("id"));
                        sp.setMembers(jsonObject.getString("members"));
                        sp.setSafe(jsonObject.getString("safe"));
                        sp.setShout(jsonObject.getString("shout"));
                        sp.setElevate(jsonObject.getString("elevate"));
                        sp.setIgnored(jsonObject.getString("ignored"));
                        sp.setShare(jsonObject.getString("share"));
                        sp.setNeutralize(jsonObject.getString("neutralize"));
                        sp.setAlertid(jsonObject.getString("alertid"));

                        String hii=jsonObject.getString("alertid");
                        if (hii.equalsIgnoreCase(code)) {

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
        List<ratings> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<ratings> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.ratings, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHoder myHoder, int i) {
            final ratings sm = servicemodels.get(i);
            final String id, members, alertid, safe, shout, elevate, share,ignored, neutralize;
            safe=sm.getSafe();
            shout=sm.getShout();
            elevate=sm.getElevate();
            ignored=sm.getIgnored();
            neutralize=sm.getNeutralize();
            share=sm.getShare();
            alertid=sm.getAlertid();

            myHoder.Safe.setText("Safe:\t"+safe+"%");
            myHoder.Shout.setText("Shout\t"+shout+"%");
            myHoder.Elevate.setText("Elevate\t"+elevate+"%");
            myHoder.Ignore.setText("Ignored:\t"+ignored+"%");
            myHoder.Neutralized.setText("Neutralized:\t"+neutralize+"%");
            myHoder.Share.setText("Shared:\t"+share+"%");

            myHoder.elevate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elevateAlert(alertid,level);
                }
            });

            myHoder.neutralize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    neutralizeAlert(alertid,level);
                }
            });

            myHoder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareAlert(alertid,level);
                }
            });
            myHoder.ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ignoreAlert(alertid,level);
                }
            });

            myHoder.shout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoutAlert(alertid,level);
                }
            });

            myHoder.safe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    safeAlert(alertid,level);
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

    private void safeAlert(String alertid, String level) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activealerts.this, "Updating Safe", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("user", bitmaps[1]);

                String result = rh.postRequest(connection.safe(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(activealerts.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Safe")) {
                    download();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(alertid,prefer.getUser());
    }

    private void shoutAlert(String alertid, String level) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activealerts.this, "Updating Shout", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("user", bitmaps[1]);

                String result = rh.postRequest(connection.shout(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(activealerts.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("shout")) {
                    download();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(alertid,prefer.getUser());
    }

    private void ignoreAlert(String alertid, String level) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activealerts.this, "Ignoring Alert", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("user", bitmaps[1]);

                String result = rh.postRequest(connection.ignore(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(activealerts.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("ignored")) {
                    download();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(alertid,prefer.getUser());
    }

    private void shareAlert(String alertid, String level) {
        Intent intent=new Intent(activealerts.this, sharealert.class);
        Bundle bundle=new Bundle();
        bundle.putString("code",alertid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void neutralizeAlert(final String alertid, String level) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activealerts.this);
        builder.setMessage("Are you sure you want to neutralize this alert?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                okCool(alertid);
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
    private void okCool(String code) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activealerts.this, "Sharing Alert", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("groupname", bitmaps[1]);
                data.put("user", bitmaps[2]);

                String result = rh.postRequest(connection.neutralize(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(activealerts.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("shared")) {
                    download();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(code,groupname,prefer.getUser());
    }

    private void elevateAlert(final String alertid, String level) {

        if (level.equalsIgnoreCase("Level 3")){
            Toast.makeText(activealerts.this, "You Can't Elevate this alert", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activealerts.this);
            LayoutInflater lay = LayoutInflater.from(activealerts.this);
            final View viewdata = lay.inflate(R.layout.elevator, null);

            String[] lv = {"Select", "Level 1", "Level 2", "Level 3"};
            final Spinner spinner = viewdata.findViewById(R.id.ele);
            ArrayAdapter<String> array = new ArrayAdapter<>(activealerts.this, android.R.layout.simple_spinner_dropdown_item, lv);
            spinner.setAdapter(array);
            builder.setView(viewdata);
            builder.setPositiveButton("Elevate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String select = spinner.getSelectedItem().toString().trim();

                    if (activealerts.this.level.equalsIgnoreCase(select)) {
                        Toast.makeText(activealerts.this, "Check selection", Toast.LENGTH_SHORT).show();
                    }
                    if (activealerts.this.level.equalsIgnoreCase("Level 2")){
                        if (select.equalsIgnoreCase("Level 3")){
                            accept(alertid,select);
                        }else{
                            Toast.makeText(activealerts.this, "Check Selection", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        accept(alertid,select);
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

    private void accept(String alertid, String select) {
        class acceptElevate extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activealerts.this, "Elevating Alert", "Please wait...", true, true);
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
                Toast.makeText(activealerts.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("elevated")) {
                    download();
                }
            }
        }

        acceptElevate ui = new acceptElevate();
        ui.execute(alertid,select,prefer.getUser());
    }

    private class MyHoder extends RecyclerView.ViewHolder {
        TextView Safe,Shout,Elevate,Ignore,Share,Neutralized;
        ImageView elevate,neutralize,share,ignore,shout,safe;


        public MyHoder(@NonNull View itemView) {
            super(itemView);
            Safe = itemView.findViewById(R.id.aa);
            Shout = itemView.findViewById(R.id.bb);
            Elevate = itemView.findViewById(R.id.cc);
            Ignore = itemView.findViewById(R.id.dd);
            Share = itemView.findViewById(R.id.ee);
            Neutralized = itemView.findViewById(R.id.ff);

            elevate = itemView.findViewById(R.id.elevate);
            neutralize = itemView.findViewById(R.id.neutralize);
            share = itemView.findViewById(R.id.share);
            ignore = itemView.findViewById(R.id.ignore);
            shout = itemView.findViewById(R.id.shout);
            safe = itemView.findViewById(R.id.safe);


        }
    }

    private void loadMenu() {
        arrayList.clear();
        arrayList.add("Safe");
        arrayList.add("Shout");
        arrayList.add("Elevate");
        arrayList.add("Ignore");
        arrayList.add("Share");
        arrayList.add("Nuetralize");
    }

    private class MyAdapter extends RecyclerView.Adapter<Holder> {
        Context context;
        ArrayList<String> arrayList;
        int[] images;

        public MyAdapter(Context context, ArrayList<String> arrayList, int[] img) {
            this.context = context;
            this.arrayList = arrayList;
            this.images = img;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actions, viewGroup, false);

            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
            final String name = arrayList.get(i);
            viewHolder.textView.setText(name);
            viewHolder.imageView.setImageResource(images[i]);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.aa);
            imageView = itemView.findViewById(R.id.bb);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
