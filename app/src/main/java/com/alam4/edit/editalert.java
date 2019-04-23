package com.alam4.edit;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.pref.userpreferences;

import java.util.HashMap;

public class editalert extends AppCompatActivity {
    EditText editText;
    String code;
    private userpreferences prefer;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editalert);
        code = getIntent().getStringExtra("code");
        editText = findViewById(R.id.aa);
        prefer = new userpreferences(editalert.this);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Alert");
        actionBar.setSubtitle("" + code);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    public void handleUpdate(View view) {
        String notes = editText.getText().toString().trim();
        if (notes.isEmpty()) {
            return;
        } else {
            updateNotes(notes, code);

        }
    }

    private void updateNotes(String notes, String code) {
        class acceptElevate extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editalert.this, "Updating", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("code", bitmaps[0]);
                data.put("notes", bitmaps[1]);
                data.put("user", bitmaps[2]);

                String result = rh.postRequest(connection.update(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Alert Updated")) {
                    editalert.this.finish();
                }
            }
        }

        acceptElevate ui = new acceptElevate();
        ui.execute(code, notes, prefer.getUser());
    }
}
