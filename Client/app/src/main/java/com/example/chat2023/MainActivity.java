package com.example.chat2023;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat2023.ui.LoginFragment;
import com.example.chat2023.util.ConnectionService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent startIntent = new Intent(this, ConnectionService.class);
        startIntent.setAction(ConnectionService.CONNECT);
        startService(startIntent);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

//    public void onClickRegistrati(View view) {
//        Intent i = new Intent(this, RegistratiActivity.class);
//        startActivity(i);
//    }
//
//    public void onClickLogin(View view) {
//        Intent i = new Intent(this, LoginFragment.class);
//        startActivity(i);
//    }

}