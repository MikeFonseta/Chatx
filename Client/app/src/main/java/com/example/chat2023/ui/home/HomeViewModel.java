package com.example.chat2023.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.chat2023.controller.ChatController;
import com.example.chat2023.entity.ChatRoom;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private ChatController chatController;
    private String username;
    private int user_id;
    private MutableLiveData<List<ChatRoom>> acceptedChat;

    public HomeViewModel(Application application) {
        super(application);

        SharedPreferences sharedPref = getApplication().getSharedPreferences("User", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", null);
        user_id = sharedPref.getInt("user_id", 0);

        chatController = new ChatController(username, user_id);
    }

    public void getData(String response) {
        chatController.getChatRooms(getApplication());
        acceptedChat = (MutableLiveData<List<ChatRoom>>) chatController.getAcceptedRooms(response);
    }

}