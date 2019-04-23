package com.alam4.reg;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.MainActivity;
import com.alam4.R;
import com.alam4.background.background;
import com.alam4.connection.connection;
import com.alam4.forgot.forgot;
import com.alam4.main.dashboard;
import com.alam4.pref.userpreferences;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

public class register extends AppCompatActivity {
    TextView log, fog;
    String[] gender = {"Select", "Male", "Female"};
    String[] country = {"Select", "Kenya", "Tanzania", "Uganda", "Rwanda", "Burundi", "South Sudan"};
    Spinner genderSpinner, countrySpinner;
    EditText A, B, C, D, E, F, G, H, I;
    userpreferences aa;

    Context context;
    ActionBar actionBar;
    TextInputLayout firtnameHolder, lastnameHolder, phoneHolder, dobHolder, locationHolder, passwordHolder, confirmpasswordHolder;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Join Alam4");
        actionBar.setDisplayHomeAsUpEnabled(true);
        context = register.this;
        aa = new userpreferences(register.this);
        countrySpinner = findViewById(R.id.country);
        genderSpinner = findViewById(R.id.gender);
        ArrayAdapter<String> array = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gender);
        genderSpinner.setAdapter(array);
        ArrayAdapter<String> arraycountry = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, country);
        countrySpinner.setAdapter(arraycountry);
        A = findViewById(R.id.aa);
        B = findViewById(R.id.bb);
        C = findViewById(R.id.cc);
        D = findViewById(R.id.dd);
        E = findViewById(R.id.ee);
        F = findViewById(R.id.ff);
        G = findViewById(R.id.gg);
        H = findViewById(R.id.hh);
        I = findViewById(R.id.ii);
        firtnameHolder = findViewById(R.id.firstnameHolder);
        lastnameHolder = findViewById(R.id.lastnameHolder);
        phoneHolder = findViewById(R.id.phoneHolder);
        dobHolder = findViewById(R.id.dobHolder);
        locationHolder = findViewById(R.id.locationHolder);
        passwordHolder = findViewById(R.id.passwordHolder);
        confirmpasswordHolder = findViewById(R.id.confirmpasswordHolder);

        log = findViewById(R.id.login);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fog = findViewById(R.id.forgot_password);
        fog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgot();
            }
        });
        Random random = new Random();
        int u = random.nextInt(10000) + 5214;
        D.setText("" + u);
        imageView = findViewById(R.id.dateSelector);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

    }

    private void handleForgot() {
        Intent intent = new Intent(register.this, forgot.class);
        Animatoo.animateSlideLeft(context);
        startActivity(intent);
        register.this.finish();
    }

    private void selectDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int day, month, year;
        day = gc.get(Calendar.DAY_OF_MONTH);
        month = gc.get(Calendar.MONTH);
        year = gc.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(register.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                F.setText("" + dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void handleRegister(View view) {
        String firstname = A.getText().toString().trim();
        String lastname = B.getText().toString().trim();
        String huduma = C.getText().toString().trim();
        String userid = D.getText().toString().trim();
        String phone = E.getText().toString().trim();
        String dob = F.getText().toString().trim();
        String location = G.getText().toString().trim();

        String gender = genderSpinner.getSelectedItem().toString().trim();
        String country = countrySpinner.getSelectedItem().toString().trim();

        String password = H.getText().toString().trim();
        String con_password = I.getText().toString().trim();
        if (firstname.isEmpty()) {
            A.requestFocus();
            firtnameHolder.setError("Enter Firstname");
            return;
        }
        if (lastname.isEmpty()) {
            B.requestFocus();
            lastnameHolder.setError("Enter Lastname");
            return;
        }
        if (huduma.isEmpty()) {
            huduma = "NA";
        }
        if (userid.isEmpty()) {
            D.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            E.requestFocus();
            phoneHolder.setError("Enter Phone number");
            return;
        }
        if (dob.isEmpty()) {
            F.requestFocus();
            dobHolder.setError("Select D.O.B");
            return;
        }
        if (location.isEmpty()) {
            G.requestFocus();
            locationHolder.setError("Enter Your Location");
            return;
        }
        if (password.isEmpty()) {
            H.requestFocus();
            passwordHolder.setError("Enter your Password");
            return;
        }
        if (con_password.isEmpty()) {
            I.requestFocus();
            confirmpasswordHolder.setError("Enter confirm Password");
            return;
        }
        if (gender.equalsIgnoreCase("Select")) {
            return;
        }
        if (country.equalsIgnoreCase("Select")) {
            return;
        } else {
            if (phone.length() < 10) {
                Toast.makeText(this, "Check Phone", Toast.LENGTH_SHORT).show();
            } else {

                if (password.equalsIgnoreCase(con_password)) {
                    createAccount(firstname, lastname, huduma, userid, country, phone, gender, dob, location, password);
                } else {
                    Toast.makeText(this, "Password does not match..!!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void createAccount(String firstname, String lastname, String huduma,
                               String userid, String country, String phone,
                               String gender, String dob, String location, String password) {
        class Proceed extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            background rh = new background();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(register.this, "Creating Account", "Please wait...", true, true);
            }

            @Override
            protected String doInBackground(String... bitmaps) {

                HashMap<String, String> data = new HashMap<>();
                data.put("firstname", bitmaps[0]);
                data.put("lastname", bitmaps[1]);
                data.put("huduma", bitmaps[2]);
                data.put("userid", bitmaps[3]);
                data.put("country", bitmaps[4]);
                data.put("phone", bitmaps[5]);
                data.put("gender", bitmaps[6]);
                data.put("dob", bitmaps[7]);
                data.put("location", bitmaps[8]);
                data.put("password", bitmaps[9]);

                String result = rh.postRequest(connection.register(), data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(register.this, "" + s, Toast.LENGTH_SHORT).show();
                if (s != null && s.equalsIgnoreCase("Account creation Successful")) {
                    saveDetails();
                }
            }
        }

        Proceed ui = new Proceed();
        ui.execute(firstname, lastname, huduma, userid, country, phone, gender, dob, location, password);
    }

    private void saveDetails() {
        String id = D.getText().toString().trim();
        String first = A.getText().toString().trim();
        String last = B.getText().toString().trim();
        aa.setUser(id, first, last);
        register.this.finish();
        startActivity(new Intent(register.this, complete.class));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(register.this, MainActivity.class);
        Animatoo.animateSlideRight(context);
        startActivity(intent);
        register.this.finish();
        super.onBackPressed();
    }
}
