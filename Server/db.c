#include <stdio.h>
#include <postgresql/libpq-fe.h>
#include <stdlib.h>
#include <string.h>
#include <json-c/json.h>


PGconn* getConnection();
int checkUser(const char* user);
int registerUser(const char* user,const char* password);
int loginUser(const char* user,const char* password);
int joinRoom(const int user_id,const int chat_room_id);
int acceptRequest(const int user_id,const int chat_room_id);
int removeUser(const int user_id,const int chat_room_id);

int main()
{

	FILE *fp;

	char buffer[1024];

	struct json_object *parsed_json;
	
	struct json_object *action;
	struct json_object *username;
	struct json_object *password;

	struct json_object *user_id;
	struct json_object *chat_room_id;

	fp = fopen("test.json", "r");
	fread(buffer, 1024, 1, fp);
	fclose(fp);

	parsed_json = json_tokener_parse(buffer);

	json_object_object_get_ex(parsed_json, "action", &action);

	if(strcmp(json_object_get_string(action), "LOGIN") == 0){
		json_object_object_get_ex(parsed_json, "username", &username);
		json_object_object_get_ex(parsed_json, "password", &password);
		printf("Login user: %d\n", loginUser(json_object_get_string(username),json_object_get_string(password)));
	}
	if(strcmp(json_object_get_string(action), "REGISTER") == 0){
		json_object_object_get_ex(parsed_json, "username", &username);
		json_object_object_get_ex(parsed_json, "password", &password);
		printf("Register user: %d\n", registerUser(json_object_get_string(username),json_object_get_string(password)));
	}
	if(strcmp(json_object_get_string(action), "JOIN_ROOM") == 0){
		json_object_object_get_ex(parsed_json, "user_id", &user_id);
		json_object_object_get_ex(parsed_json, "chat_room_id", &chat_room_id);
		printf("Join room: %d\n", joinRoom(json_object_get_int(user_id),json_object_get_int(chat_room_id)));
	}
	if(strcmp(json_object_get_string(action), "ACCEPT_REQUEST") == 0){
		json_object_object_get_ex(parsed_json, "user_id", &user_id);
		json_object_object_get_ex(parsed_json, "chat_room_id", &chat_room_id);
		printf("Accept user: %d\n", acceptRequest(json_object_get_int(user_id),json_object_get_int(chat_room_id)));
	}
	if(strcmp(json_object_get_string(action), "REMOVE_USER") == 0){
		json_object_object_get_ex(parsed_json, "user_id", &user_id);
		json_object_object_get_ex(parsed_json, "chat_room_id", &chat_room_id);
		printf("Remove user: %d\n", removeUser(json_object_get_int(user_id),json_object_get_int(chat_room_id)));
	}

	return 0;
}

PGconn* getConnection() {

	PGconn *conn;
	conn = PQconnectdb("dbname=chatx host=localhost user=mike password=admin");

	if (PQstatus(conn) == CONNECTION_BAD) {
			puts("We were unable to connect to the database");
			exit(0);
	}
	return conn;
};

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
};

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
};

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
};

int joinRoom(const int user_id,const int chat_room_id)
{
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

int acceptRequest(const int user_id,const int chat_room_id)
{
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

int removeUser(const int user_id,const int chat_room_id)
{
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

