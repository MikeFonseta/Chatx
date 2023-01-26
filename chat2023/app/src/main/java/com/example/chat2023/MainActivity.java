package com.example.chat2023;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickRegistrati(View view) {
        Intent i=new Intent(this, RegistratiActivity.class);
        startActivity(i);
    }

    public void onClickAccedi(View view) {
        Intent i=new Intent(this, AccediActivity.class);
        startActivity(i);
    }
}