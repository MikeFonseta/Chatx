package com.mikefonseta.chatx.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Adapter.ChatRoomListAdapter;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.Network.responseCallBack;
import com.mikefonseta.chatx.R;

public class HomeFragment extends Fragment {

    private ChatRoomListAdapter chatRoomListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        ListView listView = view.findViewById(R.id.listView);
        chatRoomListAdapter = new ChatRoomListAdapter(getContext(), R.layout.chat_item, ChatController.getChatRoomList());
        listView.setAdapter(chatRoomListAdapter);
        chatRoomListAdapter.notifyDataSetChanged();

        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionHandler.getInstance().listen(new responseCallBack() {
                        @Override
                        public void onResponse(String message) {
                            ChatController.evaluate_action(HomeFragment.this, message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        connectionThread.start();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthenticationController.isLogged()) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(AuthenticationController.getUser().getUsername());

            String username = AuthenticationController.getUser().getUsername();
            int user_id = AuthenticationController.getUser().getUser_id();
            ConnectionHandler.getInstance().doRequest(ChatController.getRoomsRequest(username, user_id));

        }
    }
}
