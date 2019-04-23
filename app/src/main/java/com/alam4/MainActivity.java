package com.alam4;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.forgot.forgot;
import com.alam4.main.dashboard;
import com.alam4.pref.userprefer;
import com.alam4.pref.userpreferences;
import com.alam4.reg.register;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView reg, fog;
    String[] country = {"Select", "Kenya", "Tanzania", "Uganda", "Rwanda", "Burundi", "South Sudan"};
    Spinner countrySpinner;
    EditText A, B;
    private userpreferences prefer;
    TextInputLayout emailHolder,passwordHolder;
    Context context;

    @Override
    protected void onStart() {
        super.onStart();
        String user=prefer.getStatus();
        if (!user.equalsIgnoreCase("12345")){

            startActivity(new Intent(MainActivity.this, dashboard.class));
            MainActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        context=MainActivity.this;
        prefer = new userpreferences(this);
        fog = findViewById(R.id.forgot_password);
        A = findViewById(R.id.aa);
        B = findViewById(R.id.bb);
        emailHolder=findViewById(R.id.emailHolder);
        passwordHolder=findViewById(R.id.passwordHolder);
        countrySpinner = findViewById(R.id.country);
        ArrayAdapter<String> arraycountry = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, country);
        countrySpinner.setAdapter(arraycountry);
        fog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgot();
            }
        });
        reg = findViewById(R.id.register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReg();
            }
        });
    }

    private void handleForgot() {
        startActivity(new Intent(MainActivity.this, forgot.class));
        Animatoo.animateSlideLeft(context);
        MainActivity.this.finish();
    }

    private void handleReg() {
        startActivity(new Intent(MainActivity.this, register.class));
        Animatoo.animateSlideLeft(context);
        MainActivity.this.finish();
    }

    private void loginNow(String id, String pass, String cc) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Authenticating", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {
                HashMap<String, String> data = new HashMap<>();
                data.put("id", bitmaps[0]);
                data.put("pass", bitmaps[1]);
                data.put("cc", bitmaps[2]);
                String result = rh.postRequest(connection.login(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s != null && s.equalsIgnoreCase("Success")) {

                    startActivity(new Intent(MainActivity.this, dashboard.class));
                    MainActivity.this.finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(id, pass, cc);
    }

    public void handleLogin(View view) {
        String id = A.getText().toString().trim();
        String pass = B.getText().toString().trim();
        String cc = countrySpinner.getSelectedItem().toString().trim();
        if (id.isEmpty()) {
            A.requestFocus();
            emailHolder.setError("Enter User ID ");
            return;
        }
        if (pass.isEmpty()) {
            B.requestFocus();
            passwordHolder.setError("Enter your password");
            return;
        } else {
            if (cc.equalsIgnoreCase("Select")) {
                Toast.makeText(this, "Select your country", Toast.LENGTH_SHORT).show();
                return;
            } else {
                loginNow(id, pass, cc);
            }
        }
    }
}
