package com.alam4.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alam4.MainActivity;
import com.alam4.R;
import com.alam4.background.background;

import com.alam4.connection.connection;
import com.alam4.firebase.app.Config;
import com.alam4.firebase.util.NotificationUtils;
import com.alam4.frags.home;
import com.alam4.frags.reported;
import com.alam4.frags.serviceproviders;
import com.alam4.groups.mygroups;
import com.alam4.help.helpus;
import com.alam4.notification.allnotifications;
import com.alam4.pref.numofgroups;
import com.alam4.pref.userpreferences;
import com.alam4.profile.profile;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    TextView ID, NAME;
    ImageView imageView;
    private userpreferences prefer;
    numofgroups nmg;
    private static final String TAG = dashboard.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    NotificationUtils notificationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = dashboard.this;
        notificationUtils=new NotificationUtils(dashboard.this);
        prefer = new userpreferences(this);
        nmg = new numofgroups(dashboard.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        ID = view.findViewById(R.id.bb);
        NAME = view.findViewById(R.id.cc);
        imageView = view.findViewById(R.id.aa);
        ID.setText(prefer.getUser());
        NAME.setText(prefer.getFirst() + "  " + prefer.getLast());

        home a = new home();
        loadFrag(a, "ALAM4");
        BottomNavigationView navigation = findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast

                        Log.d(TAG, token);
                        Proceed proceed=new Proceed();
                        proceed.execute(prefer.getUser(),token);
                    }
                });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    String title = intent.getStringExtra("title");
                    String timestamp = intent.getStringExtra("timestamp");

                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    class Proceed extends AsyncTask<String, Void, String> {

        background rh = new background();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... bitmaps) {
            HashMap<String, String> data = new HashMap<>();
            data.put("id", bitmaps[0]);
            data.put("token", bitmaps[1]);
            String jeff = rh.postRequest(connection.background(), data);
            return jeff;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

         /*   if (s != null) {
                String no = nmg.getnumOfgroups();
                int a = Integer.parseInt(no);
                int b = Integer.parseInt(s);
                if (b > a) {
                    createNotification(s);
                }
            }*/

        }
    }

    private void createNotification(String b) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        Notification noti = new Notification.Builder(context)
                .setContentTitle("New Group")
                .setContentText("You have a new group click to view").setSmallIcon(R.mipmap.ic_launcher).build();

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(0, noti);

        nmg.updateGroups(b);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    home a = new home();
                    loadFrag(a, "ALAM4");
                    return true;
                case R.id.navigation_group:
                    serviceproviders b = new serviceproviders();
                    loadFrag(b, "Rescue Teams");
                    return true;

                case R.id.navigation_profile:
                    startActivity(new Intent(dashboard.this, profile.class));
                    Animatoo.animateSlideLeft(context);
                    dashboard.this.finish();
                    return true;
            }
            return false;
        }
    };

    private void loadFrag(Fragment a, String s) {
        setTitle("" + s);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, a, "Home");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(dashboard.this, allnotifications.class));
            Animatoo.animateSlideLeft(context);
            dashboard.this.finish();
            return true;
        }
        if (id == R.id.action_help) {

            startActivity(new Intent(dashboard.this, helpus.class));
            Animatoo.animateSlideLeft(context);
            dashboard.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            home a = new home();
            loadFrag(a, "ALAM4");
        } else if (id == R.id.nav_gallery) {

            reported r = new reported();
            loadFrag(r, "Reports");

        } else if (id == R.id.nav_slideshow) {

            startActivity(new Intent(dashboard.this, mygroups.class));
            Animatoo.animateSlideLeft(context);
            dashboard.this.finish();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent jeff = new Intent(Intent.ACTION_SEND);
            jeff.setType("text/plain");
            String url = "https://play.google.com/store/apps/details?id=com.alam4";
            jeff.putExtra(Intent.EXTRA_TEXT, url);
            context.startActivity(jeff);

        } else if (id == R.id.nav_send) {


        } else if (id == R.id.nav_logout) {
            prefer.logout("12345");
            dashboard.this.finish();
            startActivity(new Intent(dashboard.this, MainActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
