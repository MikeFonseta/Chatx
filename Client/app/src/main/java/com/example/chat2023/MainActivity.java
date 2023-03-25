package com.example.chat2023;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chat2023.controller.AuthenticationController;
import com.example.chat2023.ui.LoginFragment;
import com.example.chat2023.util.ConnectionService;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("response");
            AuthenticationController.evaluate_action(MainActivity.this, message);
            //System.out.println("da main activity: " + message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("signing"));

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
}