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
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.MainActivity;
import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.HashMap;

public class code extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    Context context;
    TextView textView, resendCode;
    EditText editText;
    TextInputLayout textInputLayout;
    String cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        cc = getIntent().getStringExtra("phone");
        context = code.this;
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOk(cc);
            }
        });
        textView = findViewById(R.id.phonesent);
        resendCode = findViewById(R.id.resend);
        editText = findViewById(R.id.phone);
        textInputLayout = findViewById(R.id.phoneHolder);
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCodeNow(cc);
            }
        });
        textView.setText("Enter the Code Sent to\t" + cc);
    }

    private void resendCodeNow(String phone) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(code.this, "Resending", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {
                HashMap<String, String> data = new HashMap<>();
                data.put("phone", bitmaps[0]);
                String result = rh.postRequest(connection.forgot(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    Toast.makeText(context, "Sent..!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(code.this, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(phone);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(code.this, MainActivity.class));
        Animatoo.animateSlideRight(context);
        code.this.finish();
        super.onBackPressed();
    }

    private void handleOk(String cc) {
        String m = editText.getText().toString().trim();
        if (m.isEmpty()) {
            textInputLayout.setError("Enter your Code");
            editText.requestFocus();
            return;
        } else {
            handleCode(m, cc);
        }

    }

    private void handleCode(String m, final String phone) {

        class Pro extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(code.this, "Confirming", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {
                HashMap<String, String> data = new HashMap<>();
                data.put("phone", bitmaps[0]);
                data.put("code", bitmaps[1]);
                String result = rh.postRequest(connection.resetcode(), data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s != null && s.equalsIgnoreCase("Success")) {
                    Intent intent = new Intent(code.this, reset.class);
                    startActivity(intent);
                    Animatoo.animateSlideLeft(context);
                    code.this.finish();
                } else {
                    Toast.makeText(code.this, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        Pro ui = new Pro();
        ui.execute(phone, m);
    }
}
