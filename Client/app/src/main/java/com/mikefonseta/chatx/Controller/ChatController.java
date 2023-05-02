package com.mikefonseta.chatx.Controller;

import android.app.Activity;
import android.widget.Toast;

import com.mikefonseta.chatx.Activity.ChatActivity;
import com.mikefonseta.chatx.Entity.ChatRoom;
import com.mikefonseta.chatx.Entity.Message;
import com.mikefonseta.chatx.Enum.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private static final List<ChatRoom> acceptedChatRooms = new ArrayList<>();
    private static final List<ChatRoom> otherChatRooms = new ArrayList<>();
    private static final List<Message> messages = new ArrayList<>();
//    private static ChatRoom currentChatRoom = null;

    public static void evaluate_action(Activity activity, String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if (action.equals(Response.GET_ROOMS.name())) {
                actionGetRooms(response);
            } else if (action.equals(Response.OPEN_ROOM.name())) {
                actionOpenRoom(response);
            } else if (action.equals(Response.SEND_MESSAGE.name())) {
                actionSendMessage(activity, response);
            } else if (action.equals(Response.NEW_MESSAGE.name())) {
                actionNewMessage(activity, response);
            } else if (action.equals(Response.CREATE.name())) {
                actionNewRoom(response);
            } else if (action.equals(Response.JOIN_ROOM.name()))
                actionJoinRoom(activity, response);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void actionSendMessage(Activity chatActivity, JSONObject response) throws JSONException {
        if (response.getString("status").equals(Response.OK.name())) {
            Message message = new Message(-1,
                    Integer.parseInt(response.getString("sender")),
                    Integer.parseInt(response.getString("chat")),
                    response.getString("message"), null);
            ((ChatActivity) chatActivity).addNewMessage(message);
        }
    }

    private static void actionNewMessage(Activity chatActivity, JSONObject response) throws JSONException {
        if (response.getString("status").equals(Response.OK.name())) {
            Message message = new Message(-1,
                    Integer.parseInt(response.getString("sender")),
                    Integer.parseInt(response.getString("chat")),
                    response.getString("message"), null);
            ((ChatActivity) chatActivity).addNewMessage(message);
        }
    }

    private static void actionJoinRoom(Activity activity, JSONObject response) throws JSONException {
        String message = response.getString("message");
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    private static void actionGetRooms(JSONObject response) throws JSONException {
        JSONArray acceptedArray = response.getJSONArray("accepted");
        JSONArray otherArray = response.getJSONArray("other");
        getAcceptedRooms(acceptedArray);
        getOtherRooms(otherArray);
    }

    private static void getAcceptedRooms(JSONArray array) throws JSONException {
        acceptedChatRooms.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);
            int chat_room_id = data.getInt("chat_room_id");
            String chat_room_name = data.getString("chat_room_name");
            int room_owner = data.getInt("room_owner_id");
            ChatRoom chatRoom = new ChatRoom(chat_room_id, chat_room_name, room_owner);
            acceptedChatRooms.add(chatRoom);
        }
    }

    private static void getOtherRooms(JSONArray array) throws JSONException {
        otherChatRooms.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);
            int chat_room_id = data.getInt("chat_room_id");
            String chat_room_name = data.getString("chat_room_name");
            int room_owner = data.getInt("room_owner_id");
            ChatRoom chatRoom = new ChatRoom(chat_room_id, chat_room_name, room_owner);
            otherChatRooms.add(chatRoom);
        }
    }

    private static void actionOpenRoom(JSONObject response) throws JSONException {
        JSONArray array = response.getJSONArray("message_list");
        messages.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);
            int message_id = data.getInt("message_id");
            int sender = data.getInt("sender");
            int chat = data.getInt("chat");
            String message_content = data.getString("message_content");
            String sending_time = data.getString("sending_time");
            Message message = new Message(message_id, sender, chat, message_content, sending_time);
            messages.add(message);
        }
    }

    private static void actionNewRoom(JSONObject response) throws JSONException {
        int chat_room_id = response.getInt("chat_room_id");
        String chat_room_name = response.getString("chat_room_name");
        int room_owner_id = response.getInt("room_owner_id");
        ChatRoom chatRoom = new ChatRoom(chat_room_id, chat_room_name, room_owner_id);
        acceptedChatRooms.add(chatRoom);
    }

    public static String getRoomsRequest(int user_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.GET_ROOMS.name());
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

    public static String getSendMessageRequest(String message, int chat_room_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.SEND_MESSAGE.name());
            jsonObject.put("chat_room_id", chat_room_id);
            jsonObject.put("from", AuthenticationController.getUser().getUser_id());
            jsonObject.put("message", message);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getCreateRoomRequest(int room_owner_id, String roomName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.CREATE.name());
            jsonObject.put("room_owner_id", room_owner_id);
            jsonObject.put("roomName", roomName);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getAccessRequest(int user_id, int chat_room_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.JOIN_ROOM.name());
            jsonObject.put("user_id", user_id);
            jsonObject.put("chat_room_id", chat_room_id);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static List<ChatRoom> getAcceptedChatRooms() {
        return acceptedChatRooms;
    }

    public static List<ChatRoom> getOtherChatRooms() {
        return otherChatRooms;
    }

    public static List<Message> getCurrentMessageList() {
        return messages;
    }

//    public static ChatRoom getCurrentChatRoom() {
//        return currentChatRoom;
//    }
//
//    public static void setCurrentChatRoom(ChatRoom chatRoom) {
//        currentChatRoom = chatRoom;
//    }
//
//    public static void clearMessages() {
//        ChatController.messages.clear();
//    }

}
