package com.alam4.alerts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.manage.managealerts;
import com.alam4.model.alertgroup;
import com.alam4.pref.userpreferences;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

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

import static java.security.AccessController.getContext;

public class addalert extends AppCompatActivity {
    ActionBar actionBar;
    Spinner G, N, L;
    String[] lv = {"Select", "Level 1", "Level 2", "Level 3"};
    private Bitmap bitmap;
    private Uri filePath;
    private int PICK_IMAGE_REQUEST = 1;
    ImageView imageView;
    EditText Notes, Location,Name;
    private userpreferences prefer;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addalert);

        actionBar = getSupportActionBar();
        actionBar.setTitle("New Alert");
        actionBar.setDisplayHomeAsUpEnabled(true);
        G = findViewById(R.id.response);
        N = findViewById(R.id.nature);
        L = findViewById(R.id.level);
        context=addalert.this;
        prefer = new userpreferences(this);
        imageView = findViewById(R.id.image);
        Notes = findViewById(R.id.notes);
        Name = findViewById(R.id.aa);
        Location = findViewById(R.id.location);
        loadData();

    }

    private void loadData() {
        Background background = new Background(addalert.this);
        background.execute();
        Nature nature = new Nature(addalert.this);
        nature.execute();
        ArrayAdapter<String> array = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lv);
        L.setAdapter(array);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            uploadAlert();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadAlert() {
        String notes = Notes.getText().toString().trim();
        String location = Location.getText().toString().trim();
        String group = G.getSelectedItem().toString().trim();
        String nature = N.getSelectedItem().toString().trim();
        String level = L.getSelectedItem().toString().trim();
        String name=Name.getText().toString().trim();

        if (location.isEmpty()) {
            return;
        }
        if (level.equalsIgnoreCase("Select")){
            return;
        }else {
            if (filePath!=null){

                if (!level.equalsIgnoreCase("Level 1")){
                    showAlert(notes, location, group, nature, level, name);
                }else {
                    uploadFinalAlert(notes, location, group, nature, level, name);
                }
            }else {
                Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showAlert(final String notes, final String location, final String group,
                           final String nature, final String level, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addalert.this);
        builder.setMessage("Are you sure you want to mobilize "+level+" rescue teams?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadFinalAlert(notes, location, group, nature, level, name);
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

    private void uploadFinalAlert(final String notes, final String location, final String group,
                                  final String nature, final String level, final String name) {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(addalert.this, "Sending Alert", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    clearData();
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                HashMap<String, String> data = new HashMap<>();
                data.put("image", uploadImage);
                data.put("name", getFileName(filePath));
                data.put("notes", notes);
                data.put("location", location);
                data.put("group", group);
                data.put("nature", nature);
                data.put("level", level);
                data.put("alertname", name);
                data.put("user", prefer.getFirst());

                String result = rh.postRequest(connection.createalert(), data);
                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }


    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
    private void clearData() {
        Name.setText("");
        Location.setText("");
        Notes.setText("");
        imageView.setImageResource(android.R.color.transparent);
        loadData();


    }


    public void handleSelectImage(View view) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class Background extends AsyncTask<String, String, String> {
        Context context;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;

        public Background(Context context) {
            this.context = context;
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
                ParserData parserData = new ParserData(context, s);
                parserData.execute(s);

            } else {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
            }
        }

        private class ParserData extends AsyncTask<String, Void, Boolean> {
            Context context;
            String jsondata;
            ArrayList<String> arrayList = new ArrayList<>();

            public ParserData(Context context, String jsondata) {
                this.context = context;
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
                    ArrayAdapter<String> arraycountry = new ArrayAdapter<String>(addalert.this, android.R.layout.simple_spinner_dropdown_item, arrayList);
                    G.setAdapter(arraycountry);
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

                    while (i < jsonArray.length()) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String sp = jsonObject.getString("groupname");
                        String member = jsonObject.getString("member");
                        if (member.equalsIgnoreCase(prefer.getUser())){
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

    private class Nature extends AsyncTask<String, String, String> {
        Context context;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;

        public Nature(Context context) {
            this.context = context;
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
            String sign_url = new connection(context).natures();

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
                ParserData parserData = new ParserData(context, s);
                parserData.execute(s);

            } else {
                Toast.makeText(context, "Error Encountered\nCan't reach the Server", Toast.LENGTH_SHORT).show();
            }
        }

        private class ParserData extends AsyncTask<String, Void, Boolean> {
            Context context;
            String jsondata;
            ArrayList<String> list = new ArrayList<>();

            public ParserData(Context context, String jsondata) {
                this.context = context;
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
                    ArrayAdapter<String> arraycountry = new ArrayAdapter<String>(addalert.this, android.R.layout.simple_spinner_dropdown_item, list);
                    N.setAdapter(arraycountry);
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

                    while (i < jsonArray.length()) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String sp = jsonObject.getString("name");
                        list.add(sp);
                        i++;

                    }

                    vall = true;

                } catch (JSONException e) {

                }
                return vall;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(addalert.this, managealerts.class));
        Animatoo.animateSlideDown(context);
        addalert.this.finish();
        super.onBackPressed();
    }

}
