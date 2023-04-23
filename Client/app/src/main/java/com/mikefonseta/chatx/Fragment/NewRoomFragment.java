package com.mikefonseta.chatx.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Controller.Controller;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class NewRoomFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Controller.setCurrentFragment(this);
        TextInputLayout roomInputLayout = view.findViewById(R.id.room_text_layout);
        TextInputEditText roomEditText = view.findViewById(R.id.room_edit_text);
        Button button = view.findViewById(R.id.new_room_button);
        button.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(roomEditText.getText()))
                roomInputLayout.setError("Inserisci un nome per la stanza");
            else {
                int user_id = AuthenticationController.getUser().getUser_id();
                ConnectionHandler.getInstance().doRequest(ChatController.getCreateRoomRequest(user_id, roomEditText.getText().toString()));
            }
        });
    }
}