package com.mikefonseta.chatx.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mikefonseta.chatx.Controller.AuthenticationController;
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
        Thread connectionThread = new Thread(ConnectionHandler::getInstance);
        connectionThread.start();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials", MODE_PRIVATE);
        if (preferences.contains("username") && preferences.contains("password")) {
            String username = preferences.getString("username", null);
            String password = preferences.getString("password", null);
            Thread thread = new Thread(() -> ConnectionHandler.getInstance().doRequest(AuthenticationController.getLoginRequest(username, password)));
            thread.start();
        } else {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        }
    }

}
