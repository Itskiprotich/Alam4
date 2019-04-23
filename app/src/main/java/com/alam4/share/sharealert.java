package com.alam4.share;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.model.alertgroup;
import com.alam4.pref.userpreferences;

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

public class sharealert extends AppCompatActivity {
    EditText editText;
    String code;
    private userpreferences prefer;
    ActionBar actionBar;

    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> groupmembers;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharealert);
        code = getIntent().getStringExtra("code");
        editText = findViewById(R.id.aa);
        prefer = new userpreferences(sharealert.this);
        groupmembers = new ArrayList<>();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Select Group to Share");
        actionBar.setSubtitle("" + code);
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = findViewById(R.id.Swipe);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupmembers.size() < 1) {
                    Toast.makeText(sharealert.this, "Select a group to Share", Toast.LENGTH_SHORT).show();
                } else {
                    shareToMembers();
                }
            }
        });
        recyclerView = findViewById(R.id.list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                download();
            }
        });
        download();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareToMembers() {
        for (String id : groupmembers) {
            sharetoGroup(code, id);
//            Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();
        }

    }

    private void sharetoGroup(String code, String name) {

        class acceptElevate extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(sharealert.this, "Updating", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("groupname", bitmaps[1]);
                data.put("user", bitmaps[2]);

                String result = rh.postRequest(connection.sharemore(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("shared")) {
                    sharealert.this.finish();
                }
            }
        }

        acceptElevate ui = new acceptElevate();
        ui.execute(code, name,prefer.getUser());
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

    private void download() {
        Background background = new Background(sharealert.this, recyclerView);
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
            String sign_url = new connection(context).alerts();

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
            ArrayList<alertgroup> arrayList = new ArrayList<>();

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
                    alertgroup sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new alertgroup();
//                        sp.setId(jsonObject.getString("groupid"));
                        sp.setGroupname(jsonObject.getString("groupname"));

                        String me = (jsonObject.getString("member"));
                        if (me.equalsIgnoreCase(prefer.getUser())) {
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

    private class MyHoder extends RecyclerView.ViewHolder {
        CheckedTextView Name;


        public MyHoder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.aa);
        }
    }

    private class AdapterClass extends RecyclerView.Adapter<MyHoder> {
        List<alertgroup> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<alertgroup> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.shareoptions, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHoder myHoder, int i) {
            final alertgroup sm = servicemodels.get(i);
            final String name, no;
            name = sm.getGroupname();
            no = sm.getId();
            myHoder.Name.setText(name);
            myHoder.Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myHoder.Name.toggle();
                    if (myHoder.Name.isChecked()) {
                        addMember(name);
                    } else {
                        removemember(name);
                    }
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

    private void removemember(String no) {
        groupmembers.remove(no);
    }

    private void addMember(String no) {
        groupmembers.add(no);
    }
}
