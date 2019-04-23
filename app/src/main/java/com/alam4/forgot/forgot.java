package com.alam4.forgot;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.MainActivity;
import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.HashMap;

public class forgot extends AppCompatActivity {
    TextView textView;
    Context context;
    ActionBar actionBar;
    TextInputLayout textInputLayout;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Forgot Password");
        actionBar.setDisplayHomeAsUpEnabled(true);
        context=forgot.this;
        textView=findViewById(R.id.forgot_password);

        editText=findViewById(R.id.phone);
        textInputLayout=findViewById(R.id.phoneHolder);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void handleForgot(View view) {
        String phone=editText.getText().toString();
        if (phone.isEmpty()){
            textInputLayout.setError("Enter phone number");
            editText.requestFocus();
            return;
        }else{
            codeSent(phone);

        }
    }

    private void codeSent(final String phone) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(forgot.this, "Connecting", "Please wait...", true, true);
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
                    Intent intent=new Intent(forgot.this,code.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("phone",phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Animatoo.animateSlideLeft(context);
                    forgot.this.finish();
                }
                else {
                    Toast.makeText(forgot.this, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(phone);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(forgot.this, MainActivity.class));
        Animatoo.animateSlideRight(context);
        forgot.this.finish();
        super.onBackPressed();
    }
}
