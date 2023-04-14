package com.mikefonseta.chatx.Controller;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.Entity.User;
import com.mikefonseta.chatx.Enum.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationController {

    private static User user = null;
    private static boolean isLogged = false;
    public static void evaluate_action(FragmentActivity activity, String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if(action.equals(Response.LOGIN.name())) {
                actionLogin(activity,response);
            }
            else if(action.equals(Response.REGISTER.name()))
            {
                actionLogin(activity,response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void actionLogin(FragmentActivity activity, JSONObject response) throws JSONException {
        String status = response.getString("status");
        if(status.equals(Response.OK.name()))
        {
            user.setUser_id(response.getInt("user_id"));
            isLogged = true;
            ConnectionHandler.getInstance().stopListen();
            activity.startActivity(new Intent(activity, MainActivity.class));
        }
        else
        {
            user = null;
            isLogged = false;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Credenziali errate", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static boolean isLogged()
    {
        return isLogged;
    }

    public static User getUser()
    {
        return user;
    }
    public static String getLoginRequest(String username, String password)
    {
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("action", "LOGIN");
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            user = new User(-1, username, password);
        }catch (JSONException e) {
            System.err.println(e.getMessage());
            //throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }

    public static String getRegisterRequest(String username, String password)
    {
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("action", "REGISTER");
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            user = new User(-1, username, password);
        }catch (JSONException e) {
            System.err.println(e.getMessage());
            //throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }
}
