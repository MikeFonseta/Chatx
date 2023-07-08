package com.mikefonseta.chatx.Entity;

public class ChatRoom {

    private final int chat_room_id;
    private String chat_room_name;
    private int room_owner;
    private int user_id_waiting;
    private String username_waiting;

    public ChatRoom(int chat_room_id, String chat_room_name, int room_owner) {
        this.chat_room_id = chat_room_id;
        this.chat_room_name = chat_room_name;
        this.room_owner = room_owner;
    }

    public ChatRoom(int user_id_waiting, int chat_room_id, String chat_room_name, String username_waiting) {
        this.user_id_waiting = user_id_waiting;
        this.chat_room_id = chat_room_id;
        this.chat_room_name = chat_room_name;
        this.username_waiting = username_waiting;
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

    public int getUser_id_waiting() {
        return user_id_waiting;
    }

    public String getUsername_waiting() {
        return username_waiting;
    }

}
