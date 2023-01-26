package com.example.chat2023;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AccediActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accedi);
    }

    public void onClickIndietro(View view) {
        Intent i=new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onClickAccedi(View view) {
        Intent i=new Intent(this, HomeActivity.class);
        startActivity(i);
    }
}