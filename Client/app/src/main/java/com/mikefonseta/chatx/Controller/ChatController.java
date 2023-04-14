package com.mikefonseta.chatx.Controller;

import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Entity.ChatRoom;
import com.mikefonseta.chatx.Enum.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private static List<ChatRoom> chatRoomList = new ArrayList<>();

    public static void evaluate_action(Fragment fragment, String message) {
        try {
            JSONObject response = new JSONObject(message);
            String action = response.getString("action");
            if (action.equals(Response.GETROOMS.name())) {
                actionGetRooms(response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
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

    public static String getRoomsRequest(String username, int user_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("action", "GETROOMS");
            jsonObject.put("username", username);
            jsonObject.put("user_id", user_id);
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        }

        return jsonObject.toString();
    }

    public static List<ChatRoom> getChatRoomList() {
        return chatRoomList;
    }

    public static void setChatRoomList(List<ChatRoom> chatRoomList) {
        ChatController.chatRoomList = chatRoomList;
    }
}
