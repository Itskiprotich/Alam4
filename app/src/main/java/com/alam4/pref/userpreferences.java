package com.alam4.pref;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class userpreferences {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public userpreferences(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("User",MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void setUser(String id, String first, String last) {
        editor.putString("alam4id",id);
        editor.putString("first",first);
        editor.putString("last",last);
        editor.putString("status",last);
        editor.commit();
    }

    public String getUser() {
        String id=sharedPreferences.getString("alam4id","12345");
        return id;
    }

    public String getLast() {
        String id=sharedPreferences.getString("last","12345");
        return id;
    }

    public String getFirst() {
        String id=sharedPreferences.getString("first","12345");
        return id;
    }

    public void logout(String s) {
        editor.putString("status",s);
        editor.commit();
    }

    public void updateId(String userid) {
        editor.putString("status",userid);
        editor.commit();
    }

    public String getStatus() {
        String id=sharedPreferences.getString("status","12345");
        return id;
    }
}
