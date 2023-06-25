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
    private static final List<ChatRoom> waitingUserChatRooms = new ArrayList<>();
    private static final List<Message> messages = new ArrayList<>();

    public static void evaluate_action(Activity activity, String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if (action.equals(Response.GET_ROOMS.name())) {
                actionGetRooms(response);
            } else if (action.equals(Response.OPEN_ROOM.name())) {
                actionOpenRoom(activity, response);
            } else if (action.equals(Response.NEW_MESSAGE.name())) {
                actionNewMessage(activity, response);
            } else if (action.equals(Response.CREATE.name())) {
                actionNewRoom(response);
            } else if (action.equals(Response.JOIN_ROOM.name())) {
                actionJoinRoom(activity, response);
            } else if (action.equals(Response.GET_WAITING_USERS.name())) {
                actionWaitingUsers(response);
            } else if (action.equals(Response.ACCEPT_REQUEST.name())) {
                actionAcceptRequest(activity, response);
            } else if (action.equals(Response.UPDATE.name())) {
                actionUpdateRoom(activity, response);
            } else if (action.equals(Response.DELETE.name())) {
                actionDeleteRoom(activity);
            }
        } catch (JSONException e) {
            System.out.println("Errore lettura json: " + message);
        }
    }

    private static void actionNewMessage(Activity activity, JSONObject response) throws JSONException {
        if (response.getString("status").equals(Response.OK.name())) {
            Message message = new Message();
            message.setChat(Integer.parseInt(response.getString("chat_room_id")));
            message.setMessage_content(response.getString("message"));
            message.setSender(response.getString("sender"));
            ChatActivity chatActivity = (ChatActivity) activity;
            chatActivity.runOnUiThread(() -> chatActivity.addNewMessage(message));
        }
    }

    private static void actionUpdateRoom(Activity activity, JSONObject response) throws JSONException {
        String chat_room_name = response.getString("chat_room_name");
        if (activity instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) activity;
            chatActivity.runOnUiThread(() -> chatActivity.updateTitle(chat_room_name));
        }
    }

    private static void actionDeleteRoom(Activity activity) {
        if (activity instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) activity;
            chatActivity.runOnUiThread(() -> chatActivity.showError("Non fai più parte di questa stanza"));
        }
    }

    private static void actionJoinRoom(Activity activity, JSONObject response) throws JSONException {
        String message = response.getString("message");
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }

    private static void actionWaitingUsers(JSONObject response) throws JSONException {
        JSONArray waitingArray = response.getJSONArray("waiting");
        getWaitingUsersChatRooms(waitingArray);
    }

    private static void actionAcceptRequest(Activity activity, JSONObject response) throws JSONException {
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

    private static void getWaitingUsersChatRooms(JSONArray array) throws JSONException {
        waitingUserChatRooms.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);
            int user_id = data.getInt("user_id");
            int chat_room_id = data.getInt("chat_room_id");
            String chat_room_name = data.getString("chat_room_name");
            String username = data.getString("username");
            ChatRoom chatRoom = new ChatRoom(user_id, chat_room_id, chat_room_name, username);
            waitingUserChatRooms.add(chatRoom);
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

    private static void actionOpenRoom(Activity activity, JSONObject response) throws JSONException {
        ChatActivity chatActivity = (ChatActivity) activity;
        if (response.getString("status").equals(Response.OK.name())) {
            JSONArray array = response.getJSONArray("message_list");
            messages.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject data = array.getJSONObject(i);
                String sender = data.getString("sender");
                int chat = data.getInt("chat");
                String message_content = data.getString("message_content");
                Message message = new Message(sender, chat, message_content);
                messages.add(message);
            }
            chatActivity.runOnUiThread(chatActivity::setUI);
        } else {
            String message = response.getString("message");
            chatActivity.runOnUiThread(() -> chatActivity.showError(message));
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

    public static String getWaitingUsersRequest(int user_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.GET_WAITING_USERS.name());
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

    public static String getAcceptRequest(int user_id, int chat_room_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.ACCEPT_REQUEST.name());
            jsonObject.put("user_id", user_id);
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

    public static String getUpdateRoomRequest(int chat_room_id, String newName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.UPDATE.name());
            jsonObject.put("user_id", AuthenticationController.getUser().getUser_id());
            jsonObject.put("chat_room_id", chat_room_id);
            jsonObject.put("newName", newName);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getDeleteRoomRequest(int chat_room_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.DELETE.name());
            jsonObject.put("chat_room_id", chat_room_id);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getLeaveRoomRequest(int chat_room_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", Response.REMOVE_USER.name());
            jsonObject.put("chat_room_id", chat_room_id);
            jsonObject.put("user_id", AuthenticationController.getUser().getUser_id());
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

    public static List<ChatRoom> getWaitingUserChatRooms() {
        return waitingUserChatRooms;
    }

    public static List<Message> getCurrentMessageList() {
        return messages;
    }

}
