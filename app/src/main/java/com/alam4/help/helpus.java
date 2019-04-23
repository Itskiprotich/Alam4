package com.alam4.help;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.alam4.R;
import com.alam4.main.dashboard;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class helpus extends AppCompatActivity {
    ActionBar actionBar;
    Context context;
    ChatArrayAdapter chatArrayAdapter;
    ListView recyclerView;
    String Authorization = "";
    String Recieved = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpus);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Inbox");
        actionBar.setDisplayHomeAsUpEnabled(true);
        context = helpus.this;
        /*recyclerView = findViewById(R.id.recyclerChat);
        chatArrayAdapter = new ChatArrayAdapter(context, new ArrayList<ChatMessage>(), recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setAdapter(chatArrayAdapter);
        chatArrayAdapter.add(new ChatMessage(1, "typing..."));
        new Welcome(context).execute();*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(helpus.this, dashboard.class));
        Animatoo.animateSlideRight(context);
        helpus.this.finish();
        super.onBackPressed();
    }


    public void ServerWelcome(JSONObject result) {
        /*try {
            Recieved = (result.getString("message"));
            Authorization = result.getString("uuid");
            chatArrayAdapter.remove((ChatMessage) chatArrayAdapter.chatList.getItemAtPosition(chatArrayAdapter.getCount() - 1));
            chatArrayAdapter.notifyDataSetChanged();

            ChatMessage chatMessage = new ChatMessage(1, Recieved);
            chatArrayAdapter.add(chatMessage);
            recyclerView.setSelection(chatArrayAdapter.getCount() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public void ServerChat(JSONObject result) {

    }
}
