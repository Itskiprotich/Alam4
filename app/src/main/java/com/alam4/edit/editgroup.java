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

import java.util.HashMap;

public class editgroup extends AppCompatActivity {
    ActionBar actionBar;
    String grp;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editgroup);
        grp = getIntent().getStringExtra("grp");
        actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Group");
        actionBar.setSubtitle("" + grp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.aa);
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

    public void handleCancel(View view) {
        onBackPressed();
    }

    public void handleUpdate(View view) {
        String new_name = editText.getText().toString().trim();
        if (new_name.isEmpty()) {
            Toast.makeText(this, "Enter Group Name", Toast.LENGTH_SHORT).show();
        } else {
            updateData(new_name, grp);
        }
    }

    private void updateData(String new_name, String grp) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editgroup.this, "Configuring Alert", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("new_name", bitmaps[0]);
                data.put("grp", bitmaps[1]);


                String result = rh.postRequest(connection.editgroup(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(editgroup.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    editgroup.this.finish();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(new_name, grp);
    }
}
