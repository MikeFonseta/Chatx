#include <string.h>
#include "db.h"


PGconn *getConnection()
{

	PGconn *conn;
	conn = PQconnectdb("dbname=chatx host=localhost user=mike password=admin");

	if (PQstatus(conn) == CONNECTION_BAD)
	{
		fprintf(stderr, "Unable to connect to the database: %s\n", PQerrorMessage(conn));
		exit(0);
	}
	return conn;
}

int evaluate_action(int fd, json_object *chat_room_list, json_object *request, json_object *response)
{
	json_object *action;
	json_object *username, *password, *message, *from;
	json_object *user_id, *chat_room_id;
	json_object *owner, *roomName, *newName;
	
	json_object_object_get_ex(request, "action", &action);



	if (strcmp(json_object_get_string(action), "LOGIN") == 0)
	{
		json_object_object_get_ex(request, "username", &username);
		json_object_object_get_ex(request, "password", &password);
		return loginUser(fd, chat_room_list,json_object_get_string(username), json_object_get_string(password), response);
	
	}
	if (strcmp(json_object_get_string(action), "REGISTER") == 0)
	{
		json_object_object_get_ex(request, "username", &username);
		json_object_object_get_ex(request, "password", &password);
		return registerUser(json_object_get_string(username), json_object_get_string(password), response);
	}
	if (strcmp(json_object_get_string(action), "SEND_MESSAGE") == 0)
	{
		json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
		json_object_object_get_ex(request, "message", &message);
		json_object_object_get_ex(request, "from", &from);
		return sendMessage(fd,chat_room_list, json_object_get_string(chat_room_id), json_object_get_string(from), json_object_get_string(message), response);
	}
	if (strcmp(json_object_get_string(action), "JOIN_ROOM") == 0)
	{
		json_object_object_get_ex(request, "user_id", &user_id);
		json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
		return joinRoom(json_object_get_int(user_id), json_object_get_int(chat_room_id), response);
	}
	if (strcmp(json_object_get_string(action), "ACCEPT_REQUEST") == 0)
	{
		json_object_object_get_ex(request, "user_id", &user_id);
		json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
		return acceptRequest(json_object_get_int(user_id), json_object_get_int(chat_room_id), response);
	}
	if (strcmp(json_object_get_string(action), "REMOVE_USER") == 0)
	{
		json_object_object_get_ex(request, "user_id", &user_id);
		json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
		return removeUser(json_object_get_int(user_id), json_object_get_int(chat_room_id), response);
	}
	if (strcmp(json_object_get_string(action), "CREATE") == 0)
	{
		json_object_object_get_ex(request, "owner", &owner);
		json_object_object_get_ex(request, "roomName", &roomName);
		return createRoom(json_object_get_string(owner), json_object_get_string(roomName), response);
	}
	if (strcmp(json_object_get_string(action), "UPDATE") == 0)
	{
		json_object_object_get_ex(request, "owner", &owner);
		json_object_object_get_ex(request, "roomName", &roomName);
		json_object_object_get_ex(request, "newName", &newName);
		return updateRoom(json_object_get_string(owner), json_object_get_string(roomName), json_object_get_string(newName), response);
	}
	if (strcmp(json_object_get_string(action), "DELETE") == 0)
	{
		json_object_object_get_ex(request, "owner", &owner);
		json_object_object_get_ex(request, "roomName", &roomName);
		return deleteRoom(json_object_get_string(owner), json_object_get_string(roomName), response);
	}
	if (strcmp(json_object_get_string(action), "GETROOMS") == 0)
	{
		json_object_object_get_ex(request, "user_id", &user_id);
		return getRooms(json_object_get_int(user_id), response);
	}

	json_object_put(request);
}

json_object* getAllChatRoom()
{
	int rows = 0;
	PGconn *conn = getConnection();
	json_object *chat_room_list = json_object_new_object();
	json_object* single_chat_room;
	char sql[256];
	sprintf(sql, "SELECT chat_room_id FROM chat_room");
	PGresult *res = PQexec(conn, sql);
	if (PQresultStatus(res) != PGRES_TUPLES_OK)
	{
		rows = -1;
	}
	else
	{
		rows = PQntuples(res);

		for(int i=0; i<rows; i++) {

			single_chat_room = json_object_new_array();
			json_object_object_add(chat_room_list, PQgetvalue(res, i, 0), single_chat_room);
			// json_object_array_add(single_chat_room, json_object_new_int(atoi(PQgetvalue(res, i, 1))));
			
		}

		printf("%s\n", json_object_to_json_string_ext(chat_room_list, JSON_C_TO_STRING_PRETTY));

	}

	PQclear(res);
	PQfinish(conn);

	return chat_room_list;
}

int checkUser(const char *user)
{
	int rows = 0;
	PGconn *conn = getConnection();
	char sql[256];
	sprintf(sql, "SELECT * FROM user_account WHERE user_account.username = '%s'", user);
	PGresult *res = PQexec(conn, sql);

	if (PQresultStatus(res) != PGRES_TUPLES_OK)
	{
		rows = -1;
	}
	else
	{
		rows = PQntuples(res);
	}

	PQclear(res);
	PQfinish(conn);

	return rows == 1 ? 1 : 0;
}

int registerUser(const char *user, const char *password, json_object *response)
{
	PGconn *conn = getConnection();

	int userTaken = checkUser(user);

	if (userTaken == 0)
	{
		char sql[256];
		sprintf(sql, "INSERT INTO user_account(username,password) VALUES('%s','%s')", user, password);
		PGresult *res = PQexec(conn, sql);

		if (PQresultStatus(res) != PGRES_COMMAND_OK)
		{
			return -1;
		}
		PQclear(res);
	}
	else
	{
		json_object_object_add(response, "action", json_object_new_string("REGISTER"));
		json_object_object_add(response, "status", json_object_new_string("FAILED"));
		json_object_object_add(response, "message", json_object_new_string("Username non disponibile"));
		return 0;
	}
	PQfinish(conn);

	json_object_object_add(response, "action", json_object_new_string("REGISTER"));
	json_object_object_add(response, "status", json_object_new_string("OK"));
	json_object_object_add(response, "message", json_object_new_string("Registrazione avvenuta con successo"));

	return 1;
}

int loginUser(int fd, json_object *chat_room_list, const char *user, const char *password, json_object *response)
{
	int rows = 0;
	int char_converted;
	PGconn *conn = getConnection();
	json_object *single_chat_room;
	char sql[256];
	sprintf(sql, "SELECT j.chat_room,u.user_id FROM (SELECT * FROM user_account WHERE user_account.username = '%s') AS u INNER JOIN join_requests AS j ON u.user_id = j.user_id AND j.accepted=true AND u.password= '%s'", user, password);

	PGresult *res = PQexec(conn, sql);

	if (PQresultStatus(res) != PGRES_TUPLES_OK)
	{
		printf("%s\n", PQresultErrorMessage(res));
	}
	else
	{
		rows = PQntuples(res);
		if (rows == 0)
		{
			json_object_object_add(response, "action", json_object_new_string("LOGIN"));
			json_object_object_add(response, "status", json_object_new_string("FAILED"));
			json_object_object_add(response, "message", json_object_new_string("Credenziali errate"));
		}
		else
		{
			for (int i = 0; i < rows; i++)
			{
				if(json_object_object_get_ex(chat_room_list, PQgetvalue(res, i, 0), &single_chat_room))
				{
					json_object_array_add(single_chat_room, json_object_new_int(fd));
				}
			}
			
			char_converted = strtol(PQgetvalue(res, 0, 0), NULL, 10);
			json_object_object_add(response, "action", json_object_new_string("LOGIN"));
			json_object_object_add(response, "status", json_object_new_string("OK"));
			json_object_object_add(response, "user_id", json_object_new_int64(char_converted));
			json_object_object_add(response, "username", json_object_new_string(user));
		}
	}
	PQclear(res);
	PQfinish(conn);
	printf("%s\n", json_object_to_json_string_ext(chat_room_list, JSON_C_TO_STRING_PRETTY));
	return rows == 0 ? 0 : 1;
}

int logout(int fd, json_object *chat_room_list)
{
	int len =  json_object_object_length(chat_room_list);
	printf("QUI");
	
	json_object_object_foreach(chat_room_list, key,chat_room)
	{
		for (int i = 0; i < json_object_array_length(chat_room); i++)
		{
			if(json_object_get_int(json_object_array_get_idx(chat_room,i)))
			{
				json_object_array_del_idx(chat_room,i,1);
			}
		}
	}
}

int sendMessage(int fd, json_object *chat_room_list, const char* chat_room_id, const char *from,const char *message, json_object *response)
{
	printf("SEND MESS START");
	json_object *chat_room;
	json_object *sendMessage = json_object_new_object();

	if(json_object_object_get_ex(chat_room_list, chat_room_id, &chat_room))
	{

		json_object_object_add(sendMessage, "chat_room_id", json_object_new_string(chat_room_id));
		json_object_object_add(sendMessage, "from", json_object_new_string(from));
		json_object_object_add(sendMessage, "message", json_object_new_string(message));

		const char *response_char = json_object_to_json_string_ext(sendMessage, JSON_C_TO_STRING_PLAIN);
		char *sending = malloc(strlen(response_char) + 2);
		strcpy(sending, response_char);
		strcat(sending, "\n");
		int response_size = strlen(sending);

		for (int i = 0; i < json_object_array_length(chat_room); i++)
		{
			int sockFD = json_object_get_int(json_object_array_get_idx(chat_room,i));
			if(sockFD != fd)
			{
				//Invio al client connesso che appartiene a questa chat_room_id
				send(sockFD,response_char,response_size,0);
			}
		}

	}

	// int rows = 0;
	// int char_converted;
	// PGconn *conn = getConnection();
	// char sql[256];

	// sprintf(sql, "SELECT user_id FROM join_requests WHERE chat_room_id = %ld AND accepted=true", chat_room_id);

	// PGresult *res_select = PQexec(conn, sql);

	// if (PQresultStatus(res_select) != PGRES_TUPLES_OK)
	// {
	// 	rows = -1;
	// 	return 0;
	// }
	// else
	// {
	// 	rows = PQntuples(res_select);

	json_object_object_add(response, "action", json_object_new_string("SEND_MESSAGE"));
	json_object_object_add(response, "status", json_object_new_string("OK"));
	
	return 1;
}

int joinRoom(const int user_id, const int chat_room_id, json_object *response)
{
	int rows = 0;
	PGconn *conn = getConnection();
	char sql[256];
	sprintf(sql, "SELECT * FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d", user_id, chat_room_id);

	PGresult *res_select = PQexec(conn, sql);

	if (PQresultStatus(res_select) != PGRES_TUPLES_OK)
	{
		rows = -1;
	}
	else
	{
		rows = PQntuples(res_select);
	}

	if (rows == 0)
	{
		// Insert request
		sprintf(sql, "INSERT INTO join_requests(user_id,chat_room,accepted) VALUES(%d,%d,false)", user_id, chat_room_id);
		PGresult *res = PQexec(conn, sql);

		if (PQresultStatus(res) != PGRES_COMMAND_OK)
		{
			rows = -1;
		}
		else
		{
			printf("Pending");
			rows = 0;
		}
	}
	else if (rows == 1)
	{

		int accepted = atoi(PQgetvalue(res_select, 0, 2));

		if (accepted == 0)
		{
			printf("Pending");
			rows = 0;
		}
		else if (accepted == 1)
		{
			printf("Accepted");
			rows = 1;
		}
	}

	PQclear(res_select);
	PQfinish(conn);
	return rows;
}

int acceptRequest(const int user_id, const int chat_room_id, json_object *response)
{
	int rows = 0;
	PGconn *conn = getConnection();
	char sql[256];
	sprintf(sql, "SELECT * FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d", user_id, chat_room_id);

	PGresult *res_select = PQexec(conn, sql);

	if (PQresultStatus(res_select) != PGRES_TUPLES_OK)
	{
		rows = -1;
	}
	else
	{
		rows = PQntuples(res_select);
	}

	if (rows == 1)
	{
		// Insert request
		sprintf(sql, "UPDATE join_requests SET accepted=true WHERE join_requests.user_id = %d AND join_requests.chat_room = %d", user_id, chat_room_id);
		PGresult *res = PQexec(conn, sql);

		if (PQresultStatus(res) != PGRES_COMMAND_OK)
		{
			rows = -1;
		}
		else
		{
			rows = PQntuples(res_select);
		}
	}

	PQclear(res_select);
	PQfinish(conn);
	return rows;
}

int removeUser(const int user_id, const int chat_room_id, json_object *response)
{
	int rows = 0;
	PGconn *conn = getConnection();
	char sql[256];
	sprintf(sql, "DELETE FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d", user_id, chat_room_id);

	PGresult *res = PQexec(conn, sql);

	if (PQresultStatus(res) != PGRES_COMMAND_OK)
	{
		rows = -1;
	}
	else
	{
		rows = PQntuples(res);
		printf("Hereee %d", rows);
	}

	PQclear(res);
	PQfinish(conn);
	return rows;
}

int createRoom(const char *room_owner, const char *chat_room_name, json_object *response)
{
	int rows = 0;
	char *PGstatement = "INSERT INTO Chat_room (chat_room_name, room_owner) VALUES ($1::VARCHAR, (SELECT user_id FROM user_account WHERE user_account.username = $2::VARCHAR))";
	const char *paramValues[2] = {chat_room_name, room_owner};
	PGconn *conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 2, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_COMMAND_OK)
		printf("%s\n", PQresultErrorMessage(res));
	else
		rows = PQntuples(res);

	PQclear(res);
	PQfinish(conn);
	return rows;
}

int updateRoom(const char *room_owner, const char *chat_room_name, const char *new_name, json_object *response)
{
	int rows = 0;
	char *PGstatement = "UPDATE Chat_room SET chat_room_name = $2::VARCHAR WHERE chat_room_name = $1::VARCHAR";
	const char *paramValues[2] = {chat_room_name, new_name};
	PGconn *conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 2, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_COMMAND_OK)
		printf("%s\n", PQresultErrorMessage(res));
	else
		rows = PQntuples(res);

	PQclear(res);
	PQfinish(conn);
	return rows;
}

int deleteRoom(const char *room_owner, const char *chat_room_name, json_object *response)
{
	int rows = 0;
	char *PGstatement = "DELETE FROM Chat_room WHERE chat_room_name = $1::VARCHAR";
	const char *paramValues[1] = {chat_room_name};
	PGconn *conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 1, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_COMMAND_OK)
		printf("%s\n", PQresultErrorMessage(res));
	else
		rows = PQntuples(res);

	PQclear(res);
	PQfinish(conn);
	return rows;
}

int getRooms(const int user_id, json_object *response)
{
	json_object_object_add(response, "action", json_object_new_string("GETROOMS"));
	json_object *accepted = json_object_new_array();
	json_object_object_add(response, "accepted", accepted);
	json_object *waiting = json_object_new_array();
	json_object_object_add(response, "waiting", waiting);
	json_object *other = json_object_new_array();
	json_object_object_add(response, "other", other);

	int rows = 0;
	int char_converted;
	char char_id[10];
	sprintf(char_id, "%d", user_id);
	char *PGstatement = "SELECT C.CHAT_ROOM_ID, C.CHAT_ROOM_NAME, C.ROOM_OWNER, U.username, J.ACCEPTED FROM CHAT_ROOM C "
						"LEFT JOIN (SELECT * FROM JOIN_REQUESTS WHERE JOIN_REQUESTS.USER_ID = $1::INTEGER) J ON C.CHAT_ROOM_ID = J.CHAT_ROOM, "
						"USER_ACCOUNT U WHERE C.ROOM_OWNER = U.USER_ID";
	const char *paramValues[1] = {(const char *)char_id};
	PGconn *conn = getConnection();
	PGresult *res = PQexecParams(conn, PGstatement, 1, NULL, paramValues, NULL, NULL, 0);

	if (PQresultStatus(res) != PGRES_TUPLES_OK)
		printf("%s\n", PQresultErrorMessage(res));
	else
	{
		rows = PQntuples(res);
		for (int i = 0; i < rows; i++)
		{
			json_object *obj = json_object_new_object();

			char_converted = strtol(PQgetvalue(res, i, 0), NULL, 10);
			json_object_object_add(obj, "chat_room_id", json_object_new_int64(char_converted));

			json_object_object_add(obj, "chat_room_name", json_object_new_string(PQgetvalue(res, i, 1)));

			char_converted = strtol(PQgetvalue(res, i, 2), NULL, 10);
			json_object_object_add(obj, "room_owner_id", json_object_new_int64(char_converted));

			json_object_object_add(obj, "owner", json_object_new_string(PQgetvalue(res, i, 3)));

			if (PQgetisnull(res, i, 4) == 1)
				json_object_array_add(other, obj);
			else
			{
				if (strcmp(PQgetvalue(res, i, 4), "t") == 0)
					json_object_array_add(accepted, obj);
				else
					json_object_array_add(waiting, obj);
			}
		}
	}
	PQclear(res);
	PQfinish(conn);
	return rows;
}

// void test_removeUser()
// {
// 	json_object *root = json_object_from_file("test/test.json");
// 	if (!root)
// 		printf("errore apertura json\n");
// 	json_object *response = json_object_new_object();

// 	evaluate_action(root, response);
// 	printf("Result json:\n\n%s\n\n", json_object_to_json_string_ext(response, JSON_C_TO_STRING_PRETTY));

// 	json_object_put(root);
// 	json_object_put(response);
// }

// void test_createRoom()
// {

// 	json_object *root = json_object_from_file("test/newChatRoom.json");
// 	if (!root)
// 		printf("errore apertura json\n");
// 	json_object *response = json_object_new_object();

// 	// json_object_object_del(root, "owner");
// 	json_object_object_add(root, "owner", json_object_new_string("gaetano"));

// 	// json_object_object_del(root, "roomName");
// 	json_object_object_add(root, "roomName", json_object_new_string("testRoom"));

// 	evaluate_action(root, response);
// 	printf("Result json:\n\n%s\n\n", json_object_to_json_string_ext(response, JSON_C_TO_STRING_PRETTY));

// 	json_object_put(root);
// 	json_object_put(response);
// }

// void test_updateRoom()
// {

// 	json_object *root = json_object_from_file("test/updateChatRoom.json");
// 	if (!root)
// 		printf("errore apertura json\n");
// 	json_object *response = json_object_new_object();

// 	// json_object_object_del(root, "owner");
// 	json_object_object_add(root, "owner", json_object_new_string("gaetano"));

// 	// json_object_object_del(root, "roomName");
// 	json_object_object_add(root, "roomName", json_object_new_string("testRoom"));

// 	// json_object_object_del(root, "newName");
// 	json_object_object_add(root, "newName", json_object_new_string("room1"));

// 	evaluate_action(root, response);
// 	printf("Result json:\n\n%s\n\n", json_object_to_json_string_ext(response, JSON_C_TO_STRING_PRETTY));

// 	json_object_put(root);
// 	json_object_put(response);
// }

// void test_deleteRoom()
// {

// 	json_object *root = json_object_from_file("test/deleteChatRoom.json");
// 	if (!root)
// 		printf("errore apertura json\n");
// 	json_object *response = json_object_new_object();

// 	// json_object_object_del(root, "owner");
// 	json_object_object_add(root, "owner", json_object_new_string("gaetano"));

// 	// json_object_object_del(root, "roomName");
// 	json_object_object_add(root, "roomName", json_object_new_string("room1"));

// 	evaluate_action(root, response);
// 	printf("Result json:\n\n%s\n\n", json_object_to_json_string_ext(response, JSON_C_TO_STRING_PRETTY));

// 	json_object_put(root);
// 	json_object_put(response);
// }

// void test_getChats()
// {
// 	json_object *root = json_object_from_file("test/getChats.json");
// 	if (!root)
// 		printf("errore apertura json\n");
// 	json_object *response = json_object_new_object();

// 	json_object_object_add(root, "user_id", json_object_new_int(1));

// 	evaluate_action(root, response);
// 	printf("Result json:\n\n%s\n\n", json_object_to_json_string_ext(response, JSON_C_TO_STRING_PRETTY));

// 	json_object_put(root);
// 	json_object_put(response);
// }