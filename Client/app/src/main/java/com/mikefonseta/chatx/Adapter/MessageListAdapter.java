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

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> chatMessages;

    public MessageListAdapter() {

    }

    public void setChatMessages(List<Message> chatMessages) {
        this.chatMessages = chatMessages;
        notifyItemRangeInserted(0, chatMessages.size() - 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new SentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false));
        else
            return new ReceivedMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = chatMessages.get(position);
        if (holder.getItemViewType() == 0) {
            SentMessageViewHolder messageViewHolder = (SentMessageViewHolder) holder;
            messageViewHolder.message.setText(message.getMessage_content());
        } else {
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            receivedMessageViewHolder.sender.setText(message.getSender());
            receivedMessageViewHolder.message.setText(message.getMessage_content());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = chatMessages.get(position);
        if (AuthenticationController.getUser().getUsername().equals(message.getSender())) {
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

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        public final TextView message;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_content);
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        public final TextView sender;
        public final TextView message;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            message = itemView.findViewById(R.id.message_content);
        }
    }
}
