package com.example.chat2023.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat2023.R;
import com.example.chat2023.entity.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder> {

    private List<ChatRoom> chatRooms = new ArrayList<>();

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new UserChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        ChatRoom room = chatRooms.get(position);
        holder.textViewName.setText(room.getChat_room_name());
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void setChatRooms(List<ChatRoom> rooms) {
        this.chatRooms = rooms;
        notifyDataSetChanged();
    }

    public static class UserChatViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;

        public UserChatViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textViewName;
        }
    }
}
