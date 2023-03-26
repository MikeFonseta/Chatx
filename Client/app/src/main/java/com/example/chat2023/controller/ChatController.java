package com.example.chat2023.controller;

import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.chat2023.entity.ChatRoom;
import com.example.chat2023.util.ConnectionService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private String username;
    private int user_id;

    public ChatController(String username, int user_id) {
        this.username = username;
        this.user_id = user_id;
    }

    public void getChatRooms(Application application) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "GETROOMS");
            object.put("username", username);
            object.put("user_id", user_id);
            Intent intent = new Intent(application.getApplicationContext(), ConnectionService.class);
            intent.setAction(ConnectionService.SEND);
            intent.putExtra("json", object.toString());
            application.startService(intent);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public MutableLiveData<List<ChatRoom>> getAcceptedRooms(String response) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        MutableLiveData<List<ChatRoom>> mutableLiveData = new MutableLiveData<>();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("accepted");
            for (int i = 0; i < array.length(); i++) {
                JSONObject data = array.getJSONObject(i);
                int chat_room_id = data.getInt("chat_room_id");
                String chat_room_name = data.getString("chat_room_name");
                int room_owner = data.getInt("room_owner");
                String owner = data.getString("owner");
                ChatRoom chatRoom = new ChatRoom(chat_room_id, chat_room_name, room_owner, owner);
                chatRooms.add(chatRoom);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        mutableLiveData.setValue(chatRooms);
        return mutableLiveData;
    }
}
