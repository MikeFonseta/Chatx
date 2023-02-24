package com.example.chat2023.controller;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.chat2023.HomeActivity;
import com.example.chat2023.entity.User;
import com.example.chat2023.util.ConnectionService;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationController {

    public static void evaluate_action(Activity activity, String response_string)
    {
        try {
            JSONObject response = new JSONObject(response_string);
            String action = response.getString("action");

            if(action.equals("LOGIN"))
            {
                if(response.getString("status").equals("OK"))
                {
                    activity.startActivity(new Intent(activity, HomeActivity.class));
                }
                else if(response.getString("status").equals("FAILED"))
                {
                    Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
                }
            }
            else if(action.equals("REGISTER"))
            {
                Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void login(Activity activity, String username, String password){
        User user = new User(username, password);
        JSONObject login = user.login();
        Intent loginIntent = new Intent(activity, ConnectionService.class);
        loginIntent.setAction(ConnectionService.SEND);
        loginIntent.putExtra("json", login.toString());
        activity.startService(loginIntent);
    }

    public static void register(Activity activity, String username, String password)
    {
        User user = new User(username, password);
        JSONObject login = user.register();
        Intent registerIntent = new Intent(activity, ConnectionService.class);
        registerIntent.setAction(ConnectionService.SEND);
        registerIntent.putExtra("json", login.toString());
        activity.startService(registerIntent);
    }

}
