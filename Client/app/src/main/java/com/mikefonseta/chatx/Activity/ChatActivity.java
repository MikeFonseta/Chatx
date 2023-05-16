package com.mikefonseta.chatx.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.mikefonseta.chatx.Adapter.MessageListAdapter;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Controller.Controller;
import com.mikefonseta.chatx.Entity.Message;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class ChatActivity extends AppCompatActivity {
    private MessageListAdapter messageListAdapter;
    private EditText messageContent;
    private RecyclerView messageRecycler;
    private View dialogView;
    private TextInputLayout textInputLayout;

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
        ConnectionHandler.getInstance().doRequest(ChatController.getMessageRequest(room_id));

        dialogView = getLayoutInflater().inflate(R.layout.edit_room, null);
        textInputLayout = dialogView.findViewById(R.id.room_input_layout);

        messageRecycler = findViewById(R.id.message_recycler_view);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageListAdapter = new MessageListAdapter();
        messageContent = findViewById(R.id.message_content_send);
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(view -> {
            if (!messageContent.getText().toString().equals("") && messageContent.getText().toString().length() > 0) {
                ConnectionHandler.getInstance().doRequest(ChatController.getSendMessageRequest(messageContent.getText().toString(), room_id));
            }
        });

        ImageButton menuButton = findViewById(R.id.menu);
        menuButton.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            menu.inflate(R.menu.owner_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.rename) {
                    renameRoom(room_id);
                    return true;
                }
                else
                    return true;
            });
            menu.show();
        });
    }

    @Override
    public void onBackPressed() {
        messageListAdapter.clearAdapter();
        super.onBackPressed();
    }

    public void setUI() {
        runOnUiThread(() -> {
            messageListAdapter.setChatMessages(ChatController.getCurrentMessageList());
            messageRecycler.setAdapter(messageListAdapter);
        });
    }

    public void addNewMessage(Message message) {
        runOnUiThread(() -> {
            messageContent.getText().clear();
            messageListAdapter.addMessage(message);
            messageRecycler.scrollToPosition(messageListAdapter.getItemCount() - 1);
        });
    }

    public void renameRoom(int room_id) {
        new MaterialAlertDialogBuilder(this).setView(dialogView)
                .setPositiveButton("Salva", (dialogInterface, i) -> {
                    if (TextUtils.isEmpty(textInputLayout.getEditText().getText()))
                        textInputLayout.setError("Inserisci un nome");
                    else
                        ConnectionHandler.getInstance().doRequest(ChatController.getUpdateRoomRequest(room_id, textInputLayout.getEditText().getText().toString()));
                })
                .setNegativeButton("Annulla", null)
                .show();
    }
}
