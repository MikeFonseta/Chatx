package com.example.chat2023;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chat2023.ui.LoginFragment;
import com.example.chat2023.ui.SigningViewModel;
import com.example.chat2023.util.ConnectionService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SigningViewModel model = new ViewModelProvider(this).get(SigningViewModel.class);
        model.getResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                System.out.println("da main activity: " + s);
            }
        });

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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("response");
            System.out.println("da main activity: " + message);
        }
    };
}