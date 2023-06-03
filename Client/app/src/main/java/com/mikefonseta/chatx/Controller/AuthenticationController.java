package com.mikefonseta.chatx.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Entity.User;
import com.mikefonseta.chatx.Enum.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationController {

    private static User user;
    private static boolean isLogged = false;

    public static void evaluate_action(Activity activity, String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if (action.equals(Response.LOGIN.name())) {
                actionLogin(activity, response);
            } else if (action.equals(Response.REGISTER.name())) {
                actionRegister(activity, response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void actionLogin(Activity activity, JSONObject response) throws JSONException {
        String status = response.getString("status");
        if (status.equals(Response.OK.name())) {
            user = new User();
            user.setUser_id(response.getInt("user_id"));
            user.setUsername(response.getString("username"));
            isLogged = true;

            SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences("credentials", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", response.getString("username"));
            editor.putString("password", response.getString("password"));
            editor.apply();

            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } else {
            String message = response.getString("message");
            activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
        }
    }

    private static void actionRegister(Activity activity, JSONObject response) throws JSONException {
        String message = response.getString("message");
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    public static boolean isLogged() {
        return isLogged;
    }

    public static User getUser() {
        return user;
    }

    public static void logout() {
        user = null;
    }

    public static String getLoginRequest(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "LOGIN");
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getRegisterRequest(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "REGISTER");
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getLogoutRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "LOGOUT");
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }
}
