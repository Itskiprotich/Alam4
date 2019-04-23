package com.alam4.forgot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alam4.MainActivity;
import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.pref.userpreferences;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.HashMap;

public class reset extends AppCompatActivity {
    private userpreferences prefer;
    FloatingActionButton floatingActionButton;
    Context context;
    TextInputLayout passwordHolder, conpassHolder;
    EditText password, conpass;
    String cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        cc = getIntent().getStringExtra("phone");
        context = reset.this;
        prefer = new userpreferences(this);
        passwordHolder = findViewById(R.id.passwordHolder);
        conpassHolder = findViewById(R.id.conpassHolder);
        password = findViewById(R.id.password);
        conpass = findViewById(R.id.conpass);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOk();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(reset.this, MainActivity.class));
        Animatoo.animateSlideRight(context);
        reset.this.finish();
        super.onBackPressed();

    }

    private void handleOk() {
        String newpass = password.getText().toString().trim();
        String cpass = conpass.getText().toString().trim();

        if (newpass.isEmpty()) {
            passwordHolder.setError("Enter password");
            password.requestFocus();
            return;
        }
        if (cpass.isEmpty()) {
            conpassHolder.setError("Enter Confirm password");
            conpass.requestFocus();
            return;
        } else {
            if (newpass.equalsIgnoreCase(cpass)) {
                successFullyUpdate(newpass);
            } else {
                Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void successFullyUpdate(String newpass) {



        class successFully extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(reset.this, "Updating", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {
                HashMap<String, String> data = new HashMap<>();
                data.put("user", bitmaps[0]);
                data.put("pass", bitmaps[1]);
                String result = rh.postRequest(connection.updatepass(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    Intent intent = new Intent(reset.this, MainActivity.class);
                    startActivity(intent);
                    Animatoo.animateSlideRight(context);
                    reset.this.finish();
                } else {
                    Toast.makeText(reset.this, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        successFully ui = new successFully();
        ui.execute(prefer.getUser(), newpass);
    }
}
