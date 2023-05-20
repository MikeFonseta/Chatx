package com.mikefonseta.chatx.Entity;

public class ChatRoom {

    private int chat_room_id;
    private String chat_room_name;
    private int room_owner;

    public ChatRoom(int chat_room_id, String chat_room_name, int room_owner) {
        this.chat_room_id = chat_room_id;
        this.chat_room_name = chat_room_name;
        this.room_owner = room_owner;
    }

    public int getChat_room_id() {
        return chat_room_id;
    }

    public String getChat_room_name() {
        return chat_room_name;
    }

    public void setChat_room_name(String chat_room_name) {
        this.chat_room_name = chat_room_name;
    }

    public int getRoom_owner() {
        return room_owner;
    }

}
