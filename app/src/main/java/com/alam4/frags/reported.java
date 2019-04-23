package com.alam4.frags;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.connection.connection;
import com.alam4.model.reportmodel;
import com.alam4.model.servicemodel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class reported extends Fragment {
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String FILENAME = "data.txt";
    private static final String DNAME = "myfiles";

    public reported() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reported, container, false);
        swipeRefreshLayout = view.findViewById(R.id.Swipe);
        recyclerView = view.findViewById(R.id.list);
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
            String sign_url = new connection(context).reports();

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
            ArrayList<reportmodel> arrayList = new ArrayList<>();

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
                    reportmodel sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new reportmodel();

                        sp.setId(jsonObject.getString("id"));
                        sp.setGroupname(jsonObject.getString("groupname"));
                        sp.setName(jsonObject.getString("name"));
                        sp.setLevel(jsonObject.getString("level"));
                        sp.setPoster(jsonObject.getString("poster"));
                        sp.setTime_generated(jsonObject.getString("time_generated"));
                        sp.setTime_neutralized(jsonObject.getString("time_neutralized"));
                        sp.setResponse_time(jsonObject.getString("response_time"));
                        sp.setSafe(jsonObject.getString("safe"));
                        sp.setShouts(jsonObject.getString("shouts"));
                        sp.setElevates(jsonObject.getString("elevates"));
                        sp.setIgnores(jsonObject.getString("ignores"));
                        sp.setShares(jsonObject.getString("shares"));
                        sp.setNeutalized(jsonObject.getString("neutalized"));
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
        List<reportmodel> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<reportmodel> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.reports, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHoder myHoder, int i) {
            final reportmodel sm = servicemodels.get(i);
            final String id, groupname, name, level, poster, time_generated, time_neutralized, response_time, safe, shouts, elevates, ignores, shares, neutalized;
            groupname = sm.getGroupname();
            name = sm.getName();
            poster = sm.getPoster();
            level = sm.getLevel();
            time_generated = sm.getTime_generated();
            time_neutralized = sm.getTime_neutralized();
            response_time = sm.getResponse_time();
            safe = sm.getSafe();
            shouts = sm.getShouts();
            elevates = sm.getElevates();
            ignores = sm.getIgnores();
            shares = sm.getShares();
            neutalized = sm.getNeutalized();


            myHoder.A.setText(groupname);
            myHoder.B.setText(name);
            myHoder.C.setText(poster);
            myHoder.D.setText(level);
            myHoder.E.setText(time_generated);
            myHoder.F.setText(time_neutralized);
            myHoder.G.setText(response_time);

            myHoder.H.setText(safe);
            myHoder.I.setText(shouts);
            myHoder.J.setText(elevates);
            myHoder.K.setText(ignores);
            myHoder.L.setText(shares);
            myHoder.M.setText(neutalized);
            myHoder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadPdfFile(groupname, name, poster, level, time_generated, time_neutralized, response_time, safe, shouts, elevates, ignores, shares, neutalized);
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

    private void downloadPdfFile(String groupname, String name, String poster, String level, String time_generated, String time_neutralized, String response_time, String safe, String shouts, String elevates, String ignores, String shares, String neutalized) {

        String pdfname=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

       /* File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Alam4");
        if (!f.exists()) {
            f.mkdirs();
        }*/
        File rootPath = new File(Environment.getExternalStorageDirectory(), DNAME);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File dataFile = new File(rootPath, FILENAME);
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), "Cannot use storage.", Toast.LENGTH_SHORT).show();

            return;
        }
        try {
            FileOutputStream mOutput = new FileOutputStream(dataFile, false);
            String data = "DATA";
            mOutput.write(data.getBytes());
            mOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream mInput = new FileInputStream(dataFile);
            byte[] data = new byte[128];
            mInput.read(data);
            mInput.close();

            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class MyHoder extends RecyclerView.ViewHolder {
        TextView A, B, C, D, E, F, G, H, I, J, K, L, M;
        Button button;

        public MyHoder(@NonNull View itemView) {
            super(itemView);
            A = itemView.findViewById(R.id.aa);
            button = itemView.findViewById(R.id.download);
            B = itemView.findViewById(R.id.bb);
            C = itemView.findViewById(R.id.cc);
            D = itemView.findViewById(R.id.dd);
            E = itemView.findViewById(R.id.ee);
            F = itemView.findViewById(R.id.ff);
            G = itemView.findViewById(R.id.gg);
            H = itemView.findViewById(R.id.hh);
            I = itemView.findViewById(R.id.ii);
            J = itemView.findViewById(R.id.jj);
            K = itemView.findViewById(R.id.kk);
            L = itemView.findViewById(R.id.ll);
            M = itemView.findViewById(R.id.mm);


        }
    }
}
