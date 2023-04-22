package com.mikefonseta.chatx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikefonseta.chatx.Adapter.MessageListAdapter;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.Network.responseCallBack;
import com.mikefonseta.chatx.R;


public class ChatActivity extends AppCompatActivity {
    private MessageListAdapter messageListAdapter;
    private Button sendButton;
    private EditText messageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton = findViewById(R.id.send_button);
        messageContent = findViewById(R.id.message_content_send);
        RecyclerView messageRecycler = (RecyclerView) findViewById(R.id.message_recycler_view);
        messageListAdapter = new MessageListAdapter(ChatController.getCurrentMessageList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(messageListAdapter);
        messageListAdapter.notifyItemRangeChanged(0, ChatController.getCurrentMessageList().size());


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(messageContent.getText().toString().equals("") && messageContent.getText().toString().length() > 0)
//                {
//                    ConnectionHandler.getInstance().doRequest(ChatController.getSendMessageRequest(messageContent.getText().toString());
//                }

            }
        });

        Thread listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionHandler.getInstance().listen(new responseCallBack() {
                        @Override
                        public void onResponse(String message) {
                            ConnectionHandler.getInstance().listen(new responseCallBack() {
                                @Override
                                public void onResponse(String message) {
                                    ChatController.evaluate_action(message);
                                    messageListAdapter.notifyItemRangeChanged(0, ChatController.getCurrentMessageList().size());
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        listenThread.start();

        ConnectionHandler.getInstance().doRequest(ChatController.getMessageRequest(ChatController.getCurrentChatRoom().getChat_room_id()));

        if (ChatController.getCurrentChatRoom() != null) {
            getSupportActionBar().setTitle(ChatController.getCurrentChatRoom().getChat_room_name());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectionHandler.getInstance().stopListen();
    }

}
