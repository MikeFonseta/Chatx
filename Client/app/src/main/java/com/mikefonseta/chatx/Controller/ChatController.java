package com.mikefonseta.chatx.Controller;

import com.mikefonseta.chatx.Entity.ChatRoom;
import com.mikefonseta.chatx.Entity.Message;
import com.mikefonseta.chatx.Enum.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private static List<ChatRoom> chatRoomList = new ArrayList<>();
    private static ChatRoom currentChatRoom = null;
    private static List<Message> messages = new ArrayList<>();

    public static void evaluate_action(String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if (action.equals(Response.GET_ROOMS.name())) {
                actionGetRooms(response);
            } else if (action.equals(Response.OPEN_ROOM.name())) {
                actionOpenRoom(response);
            } else if (action.equals(Response.SEND_MESSAGE.name())) {
                actionSendMessage(response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void actionSendMessage(JSONObject response) throws JSONException {
        if (response.getString("status").equals(Response.OK.name())) {

        }
    }

    private static void actionGetRooms(JSONObject response) throws JSONException {

        JSONArray array = response.getJSONArray("accepted");
        ChatController.chatRoomList.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);
            int chat_room_id = data.getInt("chat_room_id");
            String chat_room_name = data.getString("chat_room_name");
            int room_owner = data.getInt("room_owner_id");
            ChatRoom chatRoom = new ChatRoom(chat_room_id, chat_room_name, room_owner);
            ChatController.chatRoomList.add(chatRoom);
        }
    }

    private static void actionOpenRoom(JSONObject response) throws JSONException {

        JSONArray array = response.getJSONArray("message_list");
        ChatController.messages.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);
            int message_id = data.getInt("message_id");
            int sender = data.getInt("sender");
            int chat = data.getInt("chat");
            String message_content = data.getString("message_content");
            String sending_time = data.getString("sending_time");
            Message message = new Message(message_id, sender, chat, message_content, sending_time);
            ChatController.messages.add(message);
        }
        System.out.println(response);
    }

    public static String getRoomsRequest(String username, int user_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("action", Response.GET_ROOMS.name());
            jsonObject.put("username", username);
            jsonObject.put("user_id", user_id);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }

        return jsonObject.toString();
    }

    public static String getMessageRequest(int chat_room_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("action", Response.OPEN_ROOM.name());
            jsonObject.put("chat_room_id", chat_room_id);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }

        return jsonObject.toString();
    }

    public static String getSendMessageRequest(String message) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("action", Response.SEND_MESSAGE.name());
            jsonObject.put("chat_room_id", currentChatRoom.getChat_room_id());
            jsonObject.put("user_id", AuthenticationController.getUser().getUser_id());
            jsonObject.put("message", message);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }

        return jsonObject.toString();
    }

    public static List<ChatRoom> getChatRoomList() {
        return chatRoomList;
    }

    public static ChatRoom getCurrentChatRoom() {
        return currentChatRoom;
    }

    public static void setCurrentChatRoom(ChatRoom chatRoom) {
        currentChatRoom = chatRoom;
    }

    public static List<Message> getCurrentMessageList() {
        return messages;
    }

    public static void clearMessages() {
        ChatController.messages.clear();
    }

}
