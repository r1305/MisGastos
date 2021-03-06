package com.example.lenovo.misgastos;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lenovo.misgastos.Utils.SessionManager;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    SessionManager session;
    String idU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        session = new SessionManager(this);
        HashMap<String, String> datos = session.getUserDetails();
        idU = datos.get(SessionManager.KEY_ID);
        //Toast.makeText(this, datos.get(SessionManager.KEY_CAT), Toast.LENGTH_LONG).show();

        //Toast.makeText(this, idU, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                session.checkLogin();
                // close this activity
                finish();
            }
        }, 3000);
    }
}
