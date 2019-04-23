
package com.alam4.frags;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.connection.connection;
import com.alam4.model.servicemodel;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class serviceproviders extends Fragment {
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    public serviceproviders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serviceproviders, container, false);
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
            String sign_url = new connection(context).services();

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
            ArrayList<servicemodel> arrayList = new ArrayList<>();

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
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
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
                    servicemodel sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new servicemodel();
                        sp.setName(jsonObject.getString("name"));
                        sp.setInnitial(jsonObject.getString("initial"));
                        sp.setImage(jsonObject.getString("image"));
                        sp.setContact(jsonObject.getString("contact"));
                        sp.setAlam(jsonObject.getString("alam"));
                        sp.setLocation(jsonObject.getString("location"));
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
        List<servicemodel> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<servicemodel> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.service, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHoder myHoder, int i) {
            final servicemodel sm = servicemodels.get(i);
            String name, initial, image,contact,alam,location;
            name = sm.getName();
            initial = sm.getInnitial();
            image = sm.getImage();
            contact=sm.getContact();
            alam=sm.getAlam();
            location=sm.getLocation();
            myHoder.Initial.setText(initial);
            myHoder.Name.setText(name);
            myHoder.Contact.setText(""+contact);
            myHoder.Alam.setText(""+alam);
            myHoder.Location.setText(location);
            String sign_url = new connection(context).getimage();
            if (sign_url != null) {
                String img = sign_url + image;
                Picasso.get().load(img).into(myHoder.Image);
            }

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

    private class MyHoder extends RecyclerView.ViewHolder {
        TextView Name, Initial,Contact,Alam,Location;
        ImageView Image;

        public MyHoder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.bb);
            Initial = itemView.findViewById(R.id.cc);
            Image = itemView.findViewById(R.id.aa);
            Contact = itemView.findViewById(R.id.dd);
            Alam = itemView.findViewById(R.id.ee);
            Location = itemView.findViewById(R.id.ff);
        }
    }
}
