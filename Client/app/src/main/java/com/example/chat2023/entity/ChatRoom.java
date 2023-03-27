package com.example.chat2023.entity;

public class ChatRoom {

    private int chat_room_id;
    private String chat_room_name;
    private User owner;

    public ChatRoom(int chat_room_id, String chat_room_name, int user_id, String username) {
        this.chat_room_id = chat_room_id;
        this.chat_room_name = chat_room_name;
        this.owner = new User(username, user_id);
    }

    public int getChat_room_id() {
        return chat_room_id;
    }

    public void setChat_room_id(int chat_room_id) {
        this.chat_room_id = chat_room_id;
    }

    public String getChat_room_name() {
        return chat_room_name;
    }

    public void setChat_room_name(String chat_room_name) {
        this.chat_room_name = chat_room_name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
