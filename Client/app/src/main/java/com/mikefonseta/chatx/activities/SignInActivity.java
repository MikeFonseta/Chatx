package com.mikefonseta.chatx.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mikefonseta.chatx.R;
import com.mikefonseta.chatx.ServerAction;
import com.mikefonseta.chatx.ServerConnection;
import com.mikefonseta.chatx.databinding.ActivitySignInBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private ServerConnection clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        clientThread = new ServerConnection();
        Thread serverThread = new Thread(clientThread);
        serverThread.start();
        setListeners();
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.inputUsername.getText() != null && binding.inputPassword.getText() != null) {
                    if(clientThread != null){
                        clientThread.sendMessage(ServerAction.LOGIN.name()+"|"
                        +binding.inputUsername.getText().toString()+"|"
                        +binding.inputPassword.getText().toString());
                    }
                }
            }
        });
    }
}