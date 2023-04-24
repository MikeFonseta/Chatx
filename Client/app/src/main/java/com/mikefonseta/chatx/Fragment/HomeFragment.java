package com.mikefonseta.chatx.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Adapter.ChatRoomListAdapter;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Controller.Controller;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class HomeFragment extends Fragment {

    private ChatRoomListAdapter chatRoomListAdapter;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = view.findViewById(R.id.listView);
        Controller.setCurrentFragment(this);

        if (AuthenticationController.isLogged()) {
            ((MainActivity) requireActivity()).getSupportActionBar().setTitle(AuthenticationController.getUser().getUsername());
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatRoomListAdapter = new ChatRoomListAdapter(getContext(), R.layout.chat_item, ChatController.getChatRoomList());
                listView.setAdapter(chatRoomListAdapter);
                chatRoomListAdapter.notifyDataSetChanged();
            }
        });
    }
}
