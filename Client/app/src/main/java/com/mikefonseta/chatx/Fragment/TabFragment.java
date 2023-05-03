package com.mikefonseta.chatx.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikefonseta.chatx.Activity.ChatActivity;
import com.mikefonseta.chatx.Adapter.ChatRoomListAdapter;
import com.mikefonseta.chatx.Adapter.ItemClickSupport;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Entity.ChatRoom;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class TabFragment extends Fragment {

    private final String ARG_POSITION = "position";
    private int position;
    private RecyclerView recyclerView;

    public static TabFragment newInstance(int position) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(fragment.ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setUi();

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            ChatRoomListAdapter adapter = (ChatRoomListAdapter) recyclerView.getAdapter();
            if (adapter.getTab() == 0) {
                openRoom(adapter.getChatRoom(position));
            }
            if (adapter.getTab() == 1) {
                new MaterialAlertDialogBuilder(requireActivity())
                        .setView(R.layout.dialog_request)
                        .setPositiveButton("Si", (dialogInterface, i) -> {
                            int user_id = AuthenticationController.getUser().getUser_id();
                            int chat_room_id = adapter.getChatRoom(position).getChat_room_id();
                            ConnectionHandler.getInstance().doRequest(ChatController.getAccessRequest(user_id, chat_room_id));
                            adapter.remove(position);
                        })
                        .setNegativeButton("Annulla", null).show();
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            int user_id = AuthenticationController.getUser().getUser_id();
            ConnectionHandler.getInstance().doRequest(ChatController.getRoomsRequest(user_id));
            setUi();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void setUi() {
        switch (position) {
            case 0:
                recyclerView.setAdapter(new ChatRoomListAdapter(ChatController.getAcceptedChatRooms(), position));
                break;
            case 1:
                recyclerView.setAdapter(new ChatRoomListAdapter(ChatController.getOtherChatRooms(), position));
                break;
        }
    }

    public void openRoom(ChatRoom chatRoom) {
        int chat_room_id = chatRoom.getChat_room_id();
        String chat_room_name = chatRoom.getChat_room_name();

        Bundle bundle = new Bundle();
        bundle.putInt("ROOM_ID", chat_room_id);
        bundle.putString("ROOM_NAME", chat_room_name);

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
