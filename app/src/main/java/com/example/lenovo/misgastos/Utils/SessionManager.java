package com.example.lenovo.misgastos.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.lenovo.misgastos.LoginActivity;
import com.example.lenovo.misgastos.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class SessionManager {

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE=0;

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME="username";
    public static final String KEY_MAIL="email";
    public static final String KEY_CAT="data";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(KEY_ID, PRIVATE_MODE);
        
        editor = pref.edit();
    }

    public void createLoginSession(String id, String email, String name, JSONObject cat){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_ID, id);
        editor.putString(KEY_MAIL,email);
        editor.putString(KEY_USERNAME,name);
        editor.putString(KEY_CAT,cat.toString());

        //Toast.makeText(_context, id+"\n"+email+"\n"+name+"\n"+cat.toString(), Toast.LENGTH_SHORT).show();
        // commit changes
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }else{
            Intent i = new Intent(_context, MainActivity.class);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_CAT, pref.getString(KEY_CAT, null));
        user.put(KEY_MAIL, pref.getString(KEY_MAIL, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // return user
        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
