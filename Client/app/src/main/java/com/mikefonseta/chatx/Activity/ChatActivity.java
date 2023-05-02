package com.mikefonseta.chatx.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikefonseta.chatx.Adapter.MessageListAdapter;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Controller.Controller;
import com.mikefonseta.chatx.Entity.Message;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class ChatActivity extends AppCompatActivity {
    private MessageListAdapter messageListAdapter;
    private EditText messageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String room_name = bundle.getString("ROOM_NAME");
        int room_id = bundle.getInt("ROOM_ID");
        getSupportActionBar().setTitle(room_name);
        Controller.setCurrentActivity(this);

        messageContent = findViewById(R.id.message_content_send);
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(view -> {
            if (!messageContent.getText().toString().equals("") && messageContent.getText().toString().length() > 0) {
                ConnectionHandler.getInstance().doRequest(ChatController.getSendMessageRequest(messageContent.getText().toString(), room_id));
            }
        });

        RecyclerView messageRecycler = findViewById(R.id.message_recycler_view);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        System.out.println(ChatController.getCurrentMessageList());
        messageListAdapter = new MessageListAdapter(ChatController.getCurrentMessageList());
        messageRecycler.setAdapter(messageListAdapter);
    }

    public void addNewMessage(Message message) {
        runOnUiThread(() -> {
            messageContent.getText().clear();
            messageListAdapter.addMessage(message);
        });
    }


}
