package com.mikefonseta.chatx.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mikefonseta.chatx.Fragment.LoginFragment;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectionHandler.getInstance().stopListen();
    }
}