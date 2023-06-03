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

public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ChatRoomViewHolder> {

    private final int tab;
    private final List<ChatRoom> chatRoomList;

    public ChatRoomListAdapter(List<ChatRoom> rooms, int tab) {
        this.chatRoomList = rooms;
        this.tab = tab;
    }

    public int getTab() {
        return tab;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
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

    public void renameRoom(int position, String new_name) {
        chatRoomList.get(position).setChat_room_name(new_name);
        notifyItemChanged(position);
    }

    public void remove(int position) {
        chatRoomList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        private final TextView chat_room_name;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_room_name = itemView.findViewById(R.id.chat_room_name);
        }

        public void bind(ChatRoom chatRoom) {
            chat_room_name.setText(chatRoom.getChat_room_name());
        }
    }
}
