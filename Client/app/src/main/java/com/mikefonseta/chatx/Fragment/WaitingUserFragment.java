package com.mikefonseta.chatx.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Adapter.ChatRoomListAdapter;
import com.mikefonseta.chatx.Adapter.ItemClickSupport;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class WaitingUserFragment extends Fragment {

    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waiting_users, container, false);

        if (AuthenticationController.isLogged()) {
            ((MainActivity) requireActivity()).getSupportActionBar().setTitle(AuthenticationController.getUser().getUsername());
            int user_id = AuthenticationController.getUser().getUser_id();
            ConnectionHandler.getInstance().doRequest(ChatController.getWaitingUsersRequest(user_id));
        }
        return view;

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ChatRoomListAdapter(ChatController.getWaitingUserChatRooms(), 2));

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            ChatRoomListAdapter adapter = (ChatRoomListAdapter) recyclerView.getAdapter();
            new MaterialAlertDialogBuilder(requireActivity())
                    .setView(R.layout.dialog_request_waiting_user)
                    .setPositiveButton("Si", (dialogInterface, i) -> {

                    })
                    .setNegativeButton("NO", null).show();

        });

    }
}