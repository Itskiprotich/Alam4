package com.alam4.manage;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alam4.R;
import com.alam4.alerts.addalert;
import com.alam4.frags.manage.active;
import com.alam4.frags.manage.evelated;
import com.alam4.frags.manage.neutralized;
import com.alam4.frags.manage.shared;
import com.alam4.main.dashboard;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.ArrayList;
import java.util.List;

public class managealerts extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    String name;
    ActionBar actionBar;
    Context context;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managealerts);
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        context=managealerts.this;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Manage Alerts");
        actionBar.setDisplayHomeAsUpEnabled(true);
        floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatData();
            }
        });
    }

    private void floatData() {

        startActivity(new Intent(managealerts.this, addalert.class));
        Animatoo.animateSlideUp(context);
        managealerts.this.finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(managealerts.this, dashboard.class));
        Animatoo.animateSlideRight(context);
        managealerts.this.finish();
        super.onBackPressed();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new active(), "Active ");
        adapter.addFragment(new neutralized(), "Neutralized");
        adapter.addFragment(new shared(), "Shared");
        adapter.addFragment(new evelated(), "Elevated");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {

            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {

            mFragmentList.add(fragment);

            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }
}
