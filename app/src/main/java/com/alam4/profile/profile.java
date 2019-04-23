package com.alam4.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.main.dashboard;
import com.alam4.model.profilemodel;
import com.alam4.pref.userpreferences;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {
    ActionBar actionBar;
    Context context;
    private RecyclerView recyclerView;
    private userpreferences prefer;
    String user;
    TextView Name, alerts, groups;
    CircleImageView imageView;
    FloatingActionButton floatingActionButton;
    String myimage;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    CircleImageView camera,gallary,delete;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        context = profile.this;
        recyclerView = findViewById(R.id.list);
        prefer = new userpreferences(this);
        user = prefer.getUser();
        download();
        Name = findViewById(R.id.Name);
        imageView = findViewById(R.id.profile_image);
        alerts = findViewById(R.id.alerts);
        groups = findViewById(R.id.groups);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.this.finish();

            }
        });
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boom();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editImage();
            }
        });

        camera=findViewById(R.id.profile_camera);
        gallary=findViewById(R.id.profile_gallary);
        delete=findViewById(R.id.profile_delete);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleCamera();
            }
        });

        gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleGallary();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleDelete();
            }
        });
    }

    private void download() {
        Background background = new Background(profile.this, recyclerView);
        background.execute();
    }

    @Override
    protected void onResume() {
        download();
        super.onResume();
    }

    private void handleCamera() {

        Toast.makeText(context, "camera", Toast.LENGTH_SHORT).show();
    }

    private void handleDelete() {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(profile.this, "Removing Image", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {
                HashMap<String, String> data = new HashMap<>();
                data.put("id", bitmaps[0]);
                String result = rh.postRequest(connection.deleteprofile(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                    download();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(prefer.getUser());
    }

    private void handleGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(profile.this, "Uploading Image", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                    download();
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put("image", uploadImage);
                data.put("name", getFileName(filePath));
                data.put("user", prefer.getUser());

                String result = rh.postRequest(connection.uploadprofile(), data);
                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

    }

    String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void boom() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }

    private void editImage() {
        Intent intent = new Intent(profile.this, viewprofile.class);
        Bundle bundle = new Bundle();
        bundle.putString("myimage", myimage);
        intent.putExtras(bundle);
        startActivity(intent);
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
            String sign_url = new connection(context).profile();

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
            ArrayList<profilemodel> arrayList = new ArrayList<>();

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
                    profilemodel sp;
                    while (i < jsonArray.length()) {

                        jsonObject = jsonArray.getJSONObject(i);
                        sp = new profilemodel();
                        sp.setGroups(jsonObject.getString("groups"));
                        sp.setAlerts(jsonObject.getString("alerts"));
                        sp.setProfile(jsonObject.getString("profile"));
                        sp.setLocation(jsonObject.getString("location"));
                        sp.setGender(jsonObject.getString("gender"));
                        sp.setPhone(jsonObject.getString("phone"));
                        sp.setCountry(jsonObject.getString("country"));
                        sp.setUserid(jsonObject.getString("userid"));
                        sp.setHuduma(jsonObject.getString("huduma"));
                        sp.setLastname(jsonObject.getString("lastname"));
                        sp.setFirstname(jsonObject.getString("firstname"));


                        String member = jsonObject.getString("userid");

                        if (member.equalsIgnoreCase(user)) {
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
        List<profilemodel> servicemodels;
        Context context;
        RecyclerView recyclerView;

        public AdapterClass(List<profilemodel> servicemodels, Context context, RecyclerView recyclerView) {
            this.servicemodels = servicemodels;
            this.context = context;
            this.recyclerView = recyclerView;
        }

        @NonNull
        @Override
        public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHoder(LayoutInflater.from(this.context).inflate(R.layout.profile, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHoder myHoder, int i) {
            final profilemodel sm = servicemodels.get(i);
            String firstname, lastname, huduma, userid, country, phone, gender, dob, location, password, profile, alerts, groups;

            firstname = sm.getFirstname();
            lastname = sm.getLastname();
            huduma = sm.getHuduma();
            userid = sm.getUserid();
            country = sm.getCountry();
            phone = sm.getPhone();
            gender = sm.getGender();
            location = sm.getLocation();
            profile = sm.getProfile();
            alerts = sm.getAlerts();
            groups = sm.getGroups();
            myHoder.huduma.setText(huduma);
            myHoder.userid.setText(userid);
            myHoder.country.setText(country);
            myHoder.phone.setText(phone);
            myHoder.gender.setText(gender);
            myHoder.location.setText(location);


            String sign_url = new connection(context).getimage();
            if (sign_url != null) {
                myimage = sign_url + profile;

            }
            loadNames(firstname, lastname, myimage, alerts, groups,userid);


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

    private void loadNames(String firstname, String lastname, String img, String alertsn, String mgroups, String userid) {

        Name.setText(firstname + " " + lastname);
        Picasso.get().load(img).into(imageView);
        alerts.setText(alertsn);
        groups.setText(mgroups);
        prefer.updateId(userid);

    }


    private class MyHoder extends RecyclerView.ViewHolder {
        TextView huduma, userid, country, phone, gender, location;

        public MyHoder(@NonNull View itemView) {
            super(itemView);
            huduma = itemView.findViewById(R.id.huduma);
            userid = itemView.findViewById(R.id.userid);
            country = itemView.findViewById(R.id.country);
            phone = itemView.findViewById(R.id.phone);
            gender = itemView.findViewById(R.id.gender);
            location = itemView.findViewById(R.id.location);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(profile.this, dashboard.class));
        Animatoo.animateSlideRight(context);
        profile.this.finish();
        super.onBackPressed();
    }
}
