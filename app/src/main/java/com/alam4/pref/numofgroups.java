package com.alam4.pref;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class numofgroups {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public numofgroups(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("Groups",MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public String getnumOfgroups() {
        String id=sharedPreferences.getString("gr","0");
        return id;
    }

    public void updateGroups(String b) {
        editor.putString("gr",b);
        editor.commit();
    }
}
