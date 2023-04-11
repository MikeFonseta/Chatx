package com.mikefonseta.chatx.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mikefonseta.chatx.Entity.ChatRoom;
import com.mikefonseta.chatx.R;

import java.util.List;

public class ChatRoomListAdapter extends ArrayAdapter<ChatRoom> {

    private Context context;
    private List<ChatRoom> chatRoomList;

    public ChatRoomListAdapter(Context context, int resource, List<ChatRoom> chatRoomList) {
        super(context, resource, chatRoomList);
        this.context = context;
        this.chatRoomList = chatRoomList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item, null);

        TextView chat_room_name = view.findViewById(R.id.chat_room_name);
        chat_room_name.setText(chatRoomList.get(position).getChat_room_name());

        System.out.println(chatRoomList.get(position).getChat_room_name());

        return view;
    }

    private void removeItem(int position) {
        chatRoomList.remove(position);
        notifyDataSetChanged();
    }

}