package com.mikefonseta.chatx.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Entity.Message;
import com.mikefonseta.chatx.R;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    private List<Message> chatMessages;

    public MessageListAdapter() {

    }

    public void setChatMessages(List<Message> chatMessages) {
        this.chatMessages = chatMessages;
        notifyItemRangeInserted(0, chatMessages.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = chatMessages.get(position);
        holder.message.setText(message.getMessage_content());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = chatMessages.get(position);
        if (message.getSender() == AuthenticationController.getUser().getUser_id()) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void clearAdapter() {
        chatMessages.clear();
        notifyItemRangeRemoved(0, chatMessages.size() - 1);
    }

    public void addMessage(Message message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_content);
        }
    }
}
