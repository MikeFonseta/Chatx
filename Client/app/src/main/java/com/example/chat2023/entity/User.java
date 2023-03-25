package com.example.chat2023.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private int user_id;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, int user_id) {
        this.username = username;
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject login() {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "LOGIN");
            object.put("username", username);
            object.put("password", password);
            return object;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject register() {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "REGISTER");
            object.put("username", username);
            object.put("password", password);
            return object;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
