package com.mikefonseta.chatx.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikefonseta.chatx.Entity.ChatRoom;
import com.mikefonseta.chatx.R;

import java.util.List;

public class WaitingUserListAdapter extends RecyclerView.Adapter<WaitingUserListAdapter.WaitingUserViewHolder> {

    private final List<ChatRoom> chatRoomList;

    public WaitingUserListAdapter(List<ChatRoom> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    @NonNull
    @Override
    public WaitingUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.waiting_item, parent, false);
        return new WaitingUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingUserViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomList.get(position);
        holder.bind(chatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public ChatRoom getChatRoom(int position) {
        return chatRoomList.get(position);
    }

    public void remove(int position) {
        chatRoomList.remove(position);
        notifyItemRemoved(position);
    }

    public static class WaitingUserViewHolder extends RecyclerView.ViewHolder {

        private final TextView chat_room_name;
        private final TextView username;

        public WaitingUserViewHolder(View itemView) {
            super(itemView);
            chat_room_name = itemView.findViewById(R.id.chat_room_name_waiting);
            username = itemView.findViewById(R.id.waiting_user);
        }

        public void bind(ChatRoom chatRoom) {
            chat_room_name.setText(chatRoom.getChat_room_name());
            username.setText(chatRoom.getUsername_waiting());
        }

    }
}
