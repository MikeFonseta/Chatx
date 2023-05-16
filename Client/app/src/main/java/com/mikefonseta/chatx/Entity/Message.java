package com.mikefonseta.chatx.Entity;

public class Message {

    private String sender;
    private int chat;
    private String message_content;

    public Message(String sender, int chat, String message_content) {
        this.sender = sender;
        this.chat = chat;
        this.message_content = message_content;
    }

    public Message() {
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

}
