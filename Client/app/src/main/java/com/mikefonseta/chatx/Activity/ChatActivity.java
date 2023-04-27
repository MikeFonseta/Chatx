package com.mikefonseta.chatx.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikefonseta.chatx.Adapter.MessageListAdapter;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Controller.Controller;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class ChatActivity extends AppCompatActivity {
    private MessageListAdapter messageListAdapter;
    private EditText messageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Controller.setCurrentActivity(this);

        Button sendButton = findViewById(R.id.send_button);
        messageContent = findViewById(R.id.message_content_send);
        RecyclerView messageRecycler = findViewById(R.id.message_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageListAdapter = new MessageListAdapter(ChatController.getCurrentMessageList());
        messageRecycler.setAdapter(messageListAdapter);

        sendButton.setOnClickListener(view -> {
            if (!messageContent.getText().toString().equals("") && messageContent.getText().toString().length() > 0) {
                ConnectionHandler.getInstance().doRequest(ChatController.getSendMessageRequest(messageContent.getText().toString()));
            }
        });

        ConnectionHandler.getInstance().doRequest(ChatController.getMessageRequest(ChatController.getCurrentChatRoom().getChat_room_id()));

        if (ChatController.getCurrentChatRoom() != null) {
            getSupportActionBar().setTitle(ChatController.getCurrentChatRoom().getChat_room_name());
        }
    }

    public void updateUI() {
        runOnUiThread(() -> {
            messageContent.getText().clear();
            messageListAdapter.notifyDataSetChanged();
        });
    }
}
