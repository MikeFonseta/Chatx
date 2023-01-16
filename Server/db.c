#include <stdio.h>
#include <libpq-fe.h>
#include <stdlib.h>
#include <string.h>
#include <json-c/json.h>


PGconn* getConnection();
int evaluate_action(json_object*);

int checkUser(const char* user);
int registerUser(const char* user,const char* password);
int loginUser(const char* user,const char* password);
int joinRoom(const int user_id,const int chat_room_id);
int acceptRequest(const int user_id,const int chat_room_id);
int removeUser(const int user_id,const int chat_room_id);
int createRoom(const char*, const char*);
int updateRoom(const char*, const char*, const char*);
int deleteRoom(const char*, const char*);

void test_removeUser();
void test_createRoom();
void test_updateRoom();
void test_deleteRoom();

int main() {

	test_removeUser();
	test_createRoom();
	test_updateRoom();
	test_deleteRoom();

	return 0;
}

PGconn* getConnection() {

	PGconn *conn;
	conn = PQconnectdb("dbname=chatx host=localhost user=mike password=admin");

	if (PQstatus(conn) == CONNECTION_BAD) {
			fprintf(stderr, "Unable to connect to the database: %s\n", PQerrorMessage(conn));
			exit(0);
	}
	return conn;
}

int evaluate_Action(json_object *json_file) {
	json_object *action;
	json_object *username, *password;
	json_object *user_id, *chat_room_id;
	json_object *owner, *roomName, *newName;

	json_object_object_get_ex(json_file, "action", &action);

	if(strcmp(json_object_get_string(action), "LOGIN") == 0){
		json_object_object_get_ex(json_file, "username", &username);
		json_object_object_get_ex(json_file, "password", &password);
		printf("Login user: %d\n", loginUser(json_object_get_string(username),json_object_get_string(password)));
	}
	if(strcmp(json_object_get_string(action), "REGISTER") == 0){
		json_object_object_get_ex(json_file, "username", &username);
		json_object_object_get_ex(json_file, "password", &password);
		printf("Register user: %d\n", registerUser(json_object_get_string(username),json_object_get_string(password)));
	}
	if(strcmp(json_object_get_string(action), "JOIN_ROOM") == 0){
		json_object_object_get_ex(json_file, "user_id", &user_id);
		json_object_object_get_ex(json_file, "chat_room_id", &chat_room_id);
		printf("Join room: %d\n", joinRoom(json_object_get_int(user_id),json_object_get_int(chat_room_id)));
	}
	if(strcmp(json_object_get_string(action), "ACCEPT_REQUEST") == 0){
		json_object_object_get_ex(json_file, "user_id", &user_id);
		json_object_object_get_ex(json_file, "chat_room_id", &chat_room_id);
		printf("Accept user: %d\n", acceptRequest(json_object_get_int(user_id),json_object_get_int(chat_room_id)));
	}
	if(strcmp(json_object_get_string(action), "REMOVE_USER") == 0){
		json_object_object_get_ex(json_file, "user_id", &user_id);
		json_object_object_get_ex(json_file, "chat_room_id", &chat_room_id);
		printf("Remove user: %d\n", removeUser(json_object_get_int(user_id),json_object_get_int(chat_room_id)));
	}
	if(strcmp(json_object_get_string(action), "CREATE") == 0) {
		json_object_object_get_ex(json_file, "owner", &owner);
		json_object_object_get_ex(json_file, "roomName", &roomName);
		return createRoom(json_object_get_string(owner), json_object_get_string(roomName));
	}
	if(strcmp(json_object_get_string(action), "UPDATE") == 0) {
		json_object_object_get_ex(json_file, "owner", &owner);
		json_object_object_get_ex(json_file, "roomName", &roomName);
		json_object_object_get_ex(json_file, "newName", &newName);
		return updateRoom(json_object_get_string(owner), json_object_get_string(roomName), json_object_get_string(newName));
	}
	if(strcmp(json_object_get_string(action), "DELETE") == 0) {
		json_object_object_get_ex(json_file, "owner", &owner);
		json_object_object_get_ex(json_file, "roomName", &roomName);
		return deleteRoom(json_object_get_string(owner), json_object_get_string(roomName));
	}

    json_object_put(json_file);
}

int checkUser(const char* user)
{
	int rows = 0;
	PGconn* conn = getConnection();
	char sql[256];
	sprintf(sql,"SELECT * FROM user_account WHERE user_account.username = '%s'",user);
	PGresult *res = PQexec(conn, sql);    
    

	if (PQresultStatus(res) != PGRES_TUPLES_OK) {   
		rows = -1;
    }else{
		rows = PQntuples(res);	
	}

	PQclear(res);
    PQfinish(conn);

    return rows==1 ? 1: 0;
}

int registerUser(const char* user,const char* password)
{
	PGconn* conn = getConnection();

	int userTaken = checkUser(user);

	if(userTaken == 0)
	{
		char sql[256];
		sprintf(sql,"INSERT INTO user_account(username,password) VALUES('%s','%s')",user,password);
		PGresult *res = PQexec(conn, sql);    

		if (PQresultStatus(res) != PGRES_COMMAND_OK) {    
			return -1;
		}
		PQclear(res);
	} 
	else{
		return 0;
	}
	PQfinish(conn);

	return 1;
}

int loginUser(const char* user,const char* password)
{
	int rows = 0;
	PGconn* conn = getConnection();
	char sql[256];
	sprintf(sql,"SELECT * FROM user_account WHERE user_account.username = '%s' AND user_account.password = '%s'",user,password);
	
	PGresult *res = PQexec(conn, sql);    
    
	if (PQresultStatus(res) != PGRES_TUPLES_OK) {   
		rows = -1;
    }else{
		rows = PQntuples(res);	
	}

    for(int i=0; i<rows; i++) {
        
        printf("%s %s %s\n", PQgetvalue(res, i, 0), 
            PQgetvalue(res, i, 1), PQgetvalue(res, i, 2));
    }    

	PQclear(res);
    PQfinish(conn);
    return rows==1 ? 1 : 0;
}

int joinRoom(const int user_id,const int chat_room_id) {
	int rows = 0;
	PGconn* conn = getConnection();
	char sql[256];
	sprintf(sql,"SELECT * FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d",user_id,chat_room_id);
	
	PGresult *res_select = PQexec(conn, sql);    
    
	if (PQresultStatus(res_select) != PGRES_TUPLES_OK) {   
		rows = -1;
    }else{
		rows = PQntuples(res_select);	
	}

	if (rows == 0){
		//Insert request
		sprintf(sql,"INSERT INTO join_requests(user_id,chat_room,accepted) VALUES(%d,%d,false)",user_id,chat_room_id);
		PGresult *res = PQexec(conn, sql);    

		if (PQresultStatus(res) != PGRES_COMMAND_OK) {
			rows = -1;
		}else{
			printf("Pending");
			rows = 0;
		}
		

	}else if(rows == 1){

		int accepted = atoi(PQgetvalue(res_select, 0, 2));

		if (accepted == 0){
			printf("Pending");
			rows = 0;
		}else if (accepted == 1){
			printf("Accepted");
			rows = 1;
		}
	}

	PQclear(res_select);
    PQfinish(conn);
    return rows;
}

int acceptRequest(const int user_id,const int chat_room_id) {
	int rows = 0;
	PGconn* conn = getConnection();
	char sql[256];
	sprintf(sql,"SELECT * FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d",user_id,chat_room_id);
	
	PGresult *res_select = PQexec(conn, sql);    
    
	if (PQresultStatus(res_select) != PGRES_TUPLES_OK) {   
		rows = -1;
    }else{
		rows = PQntuples(res_select);	
	}

	if (rows == 1){
		//Insert request
		sprintf(sql,"UPDATE join_requests SET accepted=true WHERE join_requests.user_id = %d AND join_requests.chat_room = %d",user_id,chat_room_id);
		PGresult *res = PQexec(conn, sql);    

		if (PQresultStatus(res) != PGRES_COMMAND_OK) {
			rows = -1;
		}else{
			rows = PQntuples(res_select);
		}

	}

	PQclear(res_select);
    PQfinish(conn);
    return rows;
}

int removeUser(const int user_id,const int chat_room_id) {
	int rows = 0;
	PGconn* conn = getConnection();
	char sql[256];
	sprintf(sql,"DELETE FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d",user_id,chat_room_id);
	
	PGresult *res = PQexec(conn, sql);    
    
	if (PQresultStatus(res) != PGRES_COMMAND_OK) {
		rows = -1;
    }else{
		rows = PQntuples(res);
		printf("Hereee %d", rows);
	}

	PQclear(res);
    PQfinish(conn);
    return rows;
}

int createRoom(const char* room_owner, const char* chat_room_name) {
	int rows = 0;
	char *PGstatement = "INSERT INTO Chat_room (chat_room_name, room_owner) VALUES ($1::VARCHAR, (SELECT user_id FROM user_account WHERE user_account.username = $2::VARCHAR))";
	const char* paramValues[2] = {chat_room_name, room_owner};
	PGconn* conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 2, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_COMMAND_OK) {
		printf("%s\n", PQresultErrorMessage(res));
	} else
		rows = PQntuples(res);

	PQclear(res);
	PQfinish(conn);
	return rows;
}

int updateRoom(const char* room_owner, const char* chat_room_name, const char* new_name) {
	int rows = 0;
	char *PGstatement = "UPDATE Chat_room SET chat_room_name = $2::VARCHAR WHERE chat_room_name = $1::VARCHAR";
	const char* paramValues[2] = {chat_room_name, new_name};
	PGconn* conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 2, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_COMMAND_OK) {
		printf("%s\n", PQresultErrorMessage(res));
	} else
		rows = PQntuples(res);

	PQclear(res);
	PQfinish(conn);
	return rows;
}

int deleteRoom(const char* room_owner, const char* chat_room_name) {
	int rows = 0;
	char *PGstatement = "DELETE FROM Chat_room WHERE chat_room_name = $1::VARCHAR";
	const char* paramValues[1] = {chat_room_name};
	PGconn* conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 1, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_COMMAND_OK) {
		printf("%s\n", PQresultErrorMessage(res));
	} else
		rows = PQntuples(res);

	PQclear(res);
	PQfinish(conn);
	return rows;
}

void test_removeUser() {
	json_object *root = json_object_from_file("test/test.json");
	if (!root)
		printf("errore apertura json\n");
	
	evaluate_Action(root);
}

void test_createRoom() {

	json_object *root = json_object_from_file("test/newChatRoom.json");
	if (!root)
    	printf("errore apertura json\n");

	json_object_object_del(root,"owner");
    json_object_object_add(root,"owner",json_object_new_string("gaetano"));

	json_object_object_del(root,"roomName");
	json_object_object_add(root,"roomName",json_object_new_string("testRoom"));

	evaluate_Action(root);
	json_object_put(root);
}

void test_updateRoom() {

	json_object *root = json_object_from_file("test/updateChatRoom.json");
	if (!root)
    	printf("errore apertura json\n");

	json_object_object_del(root,"owner");
    json_object_object_add(root,"owner",json_object_new_string("gaetano"));

	json_object_object_del(root,"roomName");
	json_object_object_add(root,"roomName",json_object_new_string("testRoom"));

	json_object_object_del(root,"newName");
	json_object_object_add(root,"newName",json_object_new_string("room1"));

	evaluate_Action(root);
	json_object_put(root);
}

void test_deleteRoom() {

	json_object *root = json_object_from_file("test/deleteChatRoom.json");
	if (!root)
    	printf("errore apertura json\n");

	json_object_object_del(root,"owner");
    json_object_object_add(root,"owner",json_object_new_string("gaetano"));

	json_object_object_del(root,"roomName");
	json_object_object_add(root,"roomName",json_object_new_string("room1"));

	evaluate_Action(root);
	json_object_put(root);
}