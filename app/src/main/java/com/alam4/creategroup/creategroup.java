package com.alam4.creategroup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.groups.responsegroup;
import com.alam4.main.dashboard;
import com.alam4.model.membermodel;
import com.alam4.model.notes;
import com.alam4.pref.userprefer;
import com.alam4.pref.userpreferences;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class creategroup extends AppCompatActivity {
    ActionBar actionBar;
    String[] groups = {"Select", "Open Group", "Security Providers", "Emergency Service Providers", "Public Facility Group", "Company Group", "Family Group", "Private Group"};
    Spinner groupsSpinner;
    private RecyclerView recyclerView;

    AdapterClass adapterClass;
    ArrayList<String> groupmembers;
    EditText editText, location;
    private userpreferences prefer;
    String user;

    ImageView imageView;
    String grp;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Create Response Group");
        actionBar.setDisplayHomeAsUpEnabled(true);
        groupsSpinner = findViewById(R.id.spinmembers);
        recyclerView = findViewById(R.id.members);
        imageView = findViewById(R.id.cog);
        imageView.setVisibility(View.GONE);
        prefer = new userpreferences(this);
        ArrayAdapter<String> array = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groups);
        groupsSpinner.setAdapter(array);
        groupmembers = new ArrayList<>();
        editText = findViewById(R.id.name);
        location = findViewById(R.id.location);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        download();
        user = prefer.getUser();
        groupmembers.add(user);
        context = creategroup.this;
        Random random = new Random();
        int kk = random.nextInt(10000) + 1000;
        grp = String.valueOf(kk);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con();
            }
        });

    }

    private void download() {
        Background background = new Background(creategroup.this, recyclerView);
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
            String sign_url = new connection(context).getusers();

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
            ArrayList<membermodel> arrayList = new ArrayList<>();

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
                    adapterClass = new AdapterClass(arrayList, context, recyclerView);
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
                    membermodel sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new membermodel();
                        sp.setName(jsonObject.getString("firstname"));
                        sp.setId(jsonObject.getString("userid"));
                        sp.setImage(jsonObject.getString("profile"));

                        arrayList.add(sp);

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
        List<membermodel> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<membermodel> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.members, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHoder myHoder, final int i) {
            final membermodel sm = servicemodels.get(i);
            final String name, no, image;
            name = sm.getName();
            no = sm.getId();
            image = sm.getImage();

                String sign_url = new connection(context).getimage();
                if (sign_url != null) {
                    String img = sign_url + image;
                    Picasso.get().load(img).into(myHoder.imageView);
                }
                myHoder.textView.setText("" + no);
                myHoder.Name.setText("" + name);
                myHoder.Name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myHoder.Name.toggle();
                        if (myHoder.Name.isChecked()) {

                            if (!no.equalsIgnoreCase(user)){
                                addMember(no);
                            }

                        } else {
                            removemember(no);
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

    private class MyHoder extends RecyclerView.ViewHolder {
        CheckedTextView Name;
        TextView textView;
        CircleImageView imageView;
        CardView cardView;

        public MyHoder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.aa);
            textView = itemView.findViewById(R.id.cc);
            imageView = itemView.findViewById(R.id.bb);
            cardView = itemView.findViewById(R.id.card);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            if (groupmembers.size() < 3) {
                Toast.makeText(creategroup.this, "Add at lease two people to create group", Toast.LENGTH_SHORT).show();
            } else {
                creategg();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void creategg() {
        String name = editText.getText().toString().trim();
        String type = groupsSpinner.getSelectedItem().toString().trim();
        String lo = location.getText().toString().trim();
        if (type.equalsIgnoreCase("Select")) {
            Toast.makeText(this, "Select Group Type", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.isEmpty()) {
            Toast.makeText(this, "Enter Group Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (lo.isEmpty()) {
            Toast.makeText(this, "enter Location", Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (String id : groupmembers) {
                createUserGroup(name, id, type, user, lo, grp);
            }
        }
    }

    private void createUserGroup(String name, String id, String type, String user, String lo, String grp) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(creategroup.this, "Creating Group", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("name", bitmaps[0]);
                data.put("userid", bitmaps[1]);
                data.put("type", bitmaps[2]);
                data.put("user", bitmaps[3]);
                data.put("lo", bitmaps[4]);
                data.put("grp", bitmaps[5]);

                String result = rh.postRequest(connection.creategroup(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(creategroup.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Group creation Successful")) {

                    imageView.setVisibility(View.VISIBLE);

                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(name, id, type, user, lo, grp);
    }

    private void con() {
        Intent intent = new Intent(creategroup.this, responsegroup.class);
        Bundle bundle = new Bundle();
        bundle.putString("grp", grp);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(creategroup.this, dashboard.class));
        Animatoo.animateSlideRight(context);
        creategroup.this.finish();
        super.onBackPressed();
    }
}
