package com.alam4;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alam4.main.dashboard;
import com.alam4.pref.userprefer;

public class welcome extends AppCompatActivity {
    TextView textView, slG;
    Animation animation, animationslG;
    private userprefer prefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        initViews();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.our_anim);
        imgSplash.startAnimation(animation);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fullscreenContent.startAnimation(animation1);

        fullscreenContent1.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bouce));
        new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(welcome.this,MainActivity.class));
                welcome.this.finish();

            }
        }.start();

    }

    private ImageView imgSplash;
    private TextView fullscreenContent;
    private TextView fullscreenContent1;

    public void initViews() {
        imgSplash = findViewById(R.id.imgSplash);
        fullscreenContent = findViewById(R.id.fullscreen_content);
        fullscreenContent1 = findViewById(R.id.fullscreen_content1);
    }

}
