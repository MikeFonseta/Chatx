#ifndef DB_H_INCLUDED
#define DB_H_INCLUDED

#include <libpq-fe.h>
#include <json-c/json.h>

PGconn *getConnection();
int evaluate_action(json_object *request, json_object *response);

int checkUser(const char *user);
int registerUser(const char *user, const char *password, json_object *response);
int loginUser(const char *user, const char *password, json_object *response);
int joinRoom(const int user_id, const int chat_room_id, json_object *response);
int acceptRequest(const int user_id, const int chat_room_id, json_object *response);
int removeUser(const int user_id, const int chat_room_id, json_object *response);
int createRoom(const char *room_owner, const char *chat_room_name, json_object *response);
int updateRoom(const char *room_owner, const char *chat_room_name, const char *new_name, json_object *response);
int deleteRoom(const char *room_owner, const char *chat_room_name, json_object *response);
int getRooms(const int user_id, json_object *response);

void test_removeUser();
void test_createRoom();
void test_updateRoom();
void test_deleteRoom();
void test_getChats();

#endif // DB_H_INCLUDED