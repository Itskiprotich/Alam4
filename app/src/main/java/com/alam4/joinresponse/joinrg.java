package com.alam4.joinresponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.alam4.main.dashboard;
import com.alam4.pref.userpreferences;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.HashMap;

public class joinrg extends AppCompatActivity {
    ActionBar actionBar;
    String groupId,user;
    EditText editText;
    private userpreferences prefer;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinrg);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Join Response Group");
        actionBar.setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.groupid);
        prefer = new userpreferences(this);
        user=prefer.getUser();
        context=joinrg.this;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(joinrg.this, dashboard.class));
        Animatoo.animateSlideRight(context);
        joinrg.this.finish();
        super.onBackPressed();
    }

    public void handleJoining(View view) {
        groupId=editText.getText().toString().trim();
        if (groupId.isEmpty()){
            return;
        }else {
            joinNow(user,groupId);

        }
    }

    private void joinNow(String user, String groupId) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(joinrg.this, "Joining", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {
                HashMap<String, String> data = new HashMap<>();
                data.put("user", bitmaps[0]);
                data.put("groupId", bitmaps[1]);
                String result = rh.postRequest(connection.joinrg(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    Toast.makeText(joinrg.this, "Success", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(joinrg.this, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(user, groupId);
    }
}
