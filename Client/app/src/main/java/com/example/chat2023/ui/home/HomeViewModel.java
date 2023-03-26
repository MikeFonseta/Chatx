package com.example.chat2023.ui.home;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chat2023.controller.ChatController;
import com.example.chat2023.entity.ChatRoom;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private ChatController chatController;
    private MutableLiveData<List<ChatRoom>> acceptedChat = new MutableLiveData<>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("response");
            acceptedChat = chatController.getAcceptedRooms(message);
        }
    };

    public HomeViewModel(Application application) {
        super(application);

        LocalBroadcastManager.getInstance(application.getApplicationContext()).registerReceiver(mReceiver, new IntentFilter("chatrooms"));

        SharedPreferences sharedPref = getApplication().getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        int user_id = sharedPref.getInt("user_id", 0);

        chatController = new ChatController(username, user_id);
        chatController.getChatRooms(application);
    }

    public void getData(String response) {
        chatController.getChatRooms(getApplication());
//        acceptedChat = (MutableLiveData<List<ChatRoom>>) chatController.getAcceptedRooms(response);
    }

    public LiveData<List<ChatRoom>> getAcceptedChat() {
        return acceptedChat;
    }

}