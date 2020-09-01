package com.example.uparking3;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String USERNAME = "USERNAME";
    //public static final String EMAIL = "EMAIL";
   // public static final String ID = "ID";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String username /*, String email , String id */){

        editor.putBoolean(LOGIN, true);
        editor.putString(USERNAME, username);
        //editor.putString(EMAIL, email);
       // editor.putString(ID, id);
        editor.apply();

    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){

        if (!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((SearchPage) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(USERNAME, sharedPreferences.getString(USERNAME,null));

        return user;
    }

    public String getUsername(){

        return sharedPreferences.getString(USERNAME,null);
    }
    public void logout(){

        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((SearchPage) context).finish();

    }


}