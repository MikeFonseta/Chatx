package com.mikefonseta.chatx.Entity;

public class Message {

    private int message_id;
    private String sender;
    private int chat;
    private String message_content;
    private String date;

    public Message(String sender, int chat, String message_content) {
        this.sender = sender;
        this.chat = chat;
        this.message_content = message_content;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
