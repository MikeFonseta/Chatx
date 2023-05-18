#ifndef DB_H_INCLUDED
#define DB_H_INCLUDED

#include <libpq-fe.h>
#include "server.h"

extern json_object *chat_room_list;

PGconn *getConnection();
int evaluate_action(int fd, json_object *request, json_object *response);
json_object *getAllChatRoom();

int registerUser(const char *user, const char *password, json_object *response);
int loginUser(const char *user, const char *password, json_object *response);
int logout(int fd);

int getMessage(const int chat_room_id, json_object *response);
int joinRoom(const int user_id, const int chat_room_id, json_object *response);
int sendMessage(const char *char_room_id, const char *from, const char *message, json_object *response);
int acceptRequest(const int user_id, const int chat_room_id, json_object *response);
int removeUser(const int user_id, const int chat_room_id, json_object *response);
int createRoom(const int fd, const int room_owner_id, const char *chat_room_name, json_object *response);
int updateRoom(const char *chat_room_owner, const char *chat_room_name, const char *new_name, json_object *response);
int deleteRoom(const char *chat_room_id, json_object *response);
int getRooms(const int fd, const int user_id, json_object *response);

#endif