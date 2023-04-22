package com.mikefonseta.chatx.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mikefonseta.chatx.Controller.Controller;
import com.mikefonseta.chatx.Fragment.LoginFragment;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Controller.setCurrentActivity(this);
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionHandler.getInstance();
            }
        });

        connectionThread.start();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }


}