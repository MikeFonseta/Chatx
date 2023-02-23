#ifndef DB_H_INCLUDED
#define DB_H_INCLUDED

#include <libpq-fe.h>
#include <json-c/json.h>

PGconn *getConnection();
int evaluate_action(json_object *request, json_object *response);

int checkUser(const char *user);
int registerUser(const char *user, const char *password);
int loginUser(const char *user, const char *password);
int joinRoom(const int user_id, const int chat_room_id);
int acceptRequest(const int user_id, const int chat_room_id);
int removeUser(const int user_id, const int chat_room_id);
int createRoom(const char *room_owner, const char *chat_room_name);
int updateRoom(const char *room_owner, const char *chat_room_name, const char *new_name);
int deleteRoom(const char *room_owner, const char *chat_room_name);
int getRooms(const int user_id, json_object *response);

void test_removeUser();
void test_createRoom();
void test_updateRoom();
void test_deleteRoom();
void test_getChats();

#endif // DB_H_INCLUDED