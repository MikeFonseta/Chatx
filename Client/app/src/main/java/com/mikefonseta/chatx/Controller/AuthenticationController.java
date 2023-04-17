package com.mikefonseta.chatx.Controller;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Entity.User;
import com.mikefonseta.chatx.Enum.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationController {

    private static User user = null;
    private static boolean isLogged = false;
    public static void evaluate_action(Fragment fragment, String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if(action.equals(Response.LOGIN.name())) {
                actionLogin(fragment,response);
            }
            else if(action.equals(Response.REGISTER.name()))
            {
                actionRegister(fragment,response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void actionLogin(Fragment fragment, JSONObject response) throws JSONException {
        String status = response.getString("status");
        if(status.equals(Response.OK.name()))
        {
            user.setUser_id(response.getInt("user_id"));
            isLogged = true;
            fragment.getActivity().startActivity(new Intent(fragment.getActivity(), MainActivity.class));
        }
        else
        {
            user = null;
            isLogged = false;
            fragment.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(fragment.getActivity(), "Credenziali errate", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private static void actionRegister(Fragment fragment, JSONObject response) throws JSONException {
        String status = response.getString("status");
        String message = response.getString("message");
        if(status.equals(Response.OK.name()))
        {
            fragment.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(fragment.getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            fragment.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(fragment.getActivity(), message, Toast.LENGTH_SHORT).show();
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
