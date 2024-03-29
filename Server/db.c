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

int evaluate_action(int fd, json_object *request, json_object *response)
{
        json_object *action;
        json_object *username_id, *room_id, *room_name, *usernameWaiting;
        json_object *username, *password, *message, *from;
        json_object *user_id, *chat_room_id, *room_owner_id;
        json_object *owner, *roomName, *newName;

        json_object_object_get_ex(request, "action", &action);

        if (strcmp(json_object_get_string(action), "LOGIN") == 0)
        {
                json_object_object_get_ex(request, "username", &username);
                json_object_object_get_ex(request, "password", &password);
                return loginUser(json_object_get_string(username), json_object_get_string(password), response);
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
                return sendMessage(json_object_get_string(chat_room_id), json_object_get_string(from), json_object_get_string(message), response);
        }
        if (strcmp(json_object_get_string(action), "OPEN_ROOM") == 0)
        {
                json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
                return getMessage(json_object_get_int(chat_room_id), response);
        }
        if (strcmp(json_object_get_string(action), "JOIN_ROOM") == 0)
        {
                json_object_object_get_ex(request, "user_id", &user_id);
                json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
                return joinRoom(json_object_get_int(user_id), json_object_get_int(chat_room_id), response);
        }
        if (strcmp(json_object_get_string(action), "GET_WAITING_USERS") == 0)
        {
                json_object_object_get_ex(request, "user_id", &user_id);
                return waitingUsers(json_object_get_int(user_id), response);
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
                json_object_object_get_ex(request, "room_owner_id", &room_owner_id);
                json_object_object_get_ex(request, "roomName", &roomName);
                return createRoom(fd, json_object_get_int(room_owner_id), json_object_get_string(roomName), response);
        }
        if (strcmp(json_object_get_string(action), "UPDATE") == 0)
        {
                json_object_object_get_ex(request, "user_id", &user_id);
                json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
                json_object_object_get_ex(request, "newName", &newName);
                return updateRoom(json_object_get_string(user_id), json_object_get_string(chat_room_id), json_object_get_string(newName), response);
        }
        if (strcmp(json_object_get_string(action), "DELETE") == 0)
        {
                json_object_object_get_ex(request, "chat_room_id", &chat_room_id);
                return deleteRoom(json_object_get_string(chat_room_id), response);
        }
        if (strcmp(json_object_get_string(action), "GET_ROOMS") == 0)
        {
                json_object_object_get_ex(request, "user_id", &user_id);
                return getRooms(fd, json_object_get_int(user_id), response);
        }
        if (strcmp(json_object_get_string(action), "LOGOUT") == 0)
        {
                logout(fd);
        }
        json_object_put(request);
}

json_object *getAllChatRoom()
{
        int rows = 0;
        PGconn *conn = getConnection();
        json_object *chat_room_list = json_object_new_object();
        json_object *single_chat_room;
        char sql[256];
        sprintf(sql, "SELECT chat_room_id FROM chat_room");
        PGresult *res = PQexec(conn, sql);
        if (PQresultStatus(res) != PGRES_TUPLES_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                rows = PQntuples(res);
                for (int i = 0; i < rows; i++)
                {
                        single_chat_room = json_object_new_array();
                        json_object_object_add(chat_room_list, PQgetvalue(res, i, 0), single_chat_room);
                }
                printf("%s\n", json_object_to_json_string_ext(chat_room_list, JSON_C_TO_STRING_PRETTY));
        }
        PQclear(res);
        PQfinish(conn);
        return chat_room_list;
}

int registerUser(const char *user, const char *password, json_object *response)
{
        int rows = 0;
        PGconn *conn = getConnection();
        char sql[256];
        sprintf(sql, "INSERT INTO user_account(username,password) VALUES('%s','%s')", user, password);
        PGresult *res = PQexec(conn, sql);

        json_object_object_add(response, "action", json_object_new_string("REGISTER"));
        if (PQresultStatus(res) != PGRES_COMMAND_OK)
        {
                printf("%s\n", PQresultErrorMessage(res));
                json_object_object_add(response, "status", json_object_new_string("FAILED"));
                json_object_object_add(response, "message", json_object_new_string("Username non disponibile"));
        }
        else
        {
                rows = PQntuples(res);
                json_object_object_add(response, "status", json_object_new_string("OK"));
                json_object_object_add(response, "message", json_object_new_string("Registrazione avvenuta con successo"));
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int loginUser(const char *user, const char *password, json_object *response)
{
        int rows = 0;
        int char_converted;
        PGconn *conn = getConnection();
        char sql[256];
        sprintf(sql, "SELECT * FROM user_account WHERE user_account.username = '%s' AND user_account.password = '%s'", user, password);

        PGresult *res = PQexec(conn, sql);

        if (PQresultStatus(res) != PGRES_TUPLES_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                json_object_object_add(response, "action", json_object_new_string("LOGIN"));
                rows = PQntuples(res);
                if (rows == 0)
                {
                        json_object_object_add(response, "status", json_object_new_string("FAILED"));
                        json_object_object_add(response, "message", json_object_new_string("Credenziali errate"));
                }
                else
                {
                        char_converted = strtol(PQgetvalue(res, 0, 0), NULL, 10);
                        json_object_object_add(response, "status", json_object_new_string("OK"));
                        json_object_object_add(response, "user_id", json_object_new_int64(char_converted));
                        json_object_object_add(response, "username", json_object_new_string(user));
                        json_object_object_add(response, "password", json_object_new_string(password));
                }
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int logout(int fd)
{
        int len = json_object_object_length(chat_room_list);
        json_object_object_foreach(chat_room_list, key, chat_room)
        {
                for (int i = 0; i < json_object_array_length(chat_room); i++)
                {
                        if (json_object_get_int(json_object_array_get_idx(chat_room, i)))
                        {
                                json_object_array_del_idx(chat_room, i, 1);
                        }
                }
        }
}

int sendMessage(const char *chat_room_id, const char *from, const char *message, json_object *response)
{
        json_object *chat_room;
        json_object *sendMessage = json_object_new_object();
        char *PGstatement = "INSERT INTO Message (sender, chat, message_content) VALUES ($1::INTEGER, $2::INTEGER, $3::VARCHAR) RETURNING (SELECT username FROM user_account WHERE user_id = $1::INTEGER)";
        const char *paramValues[3] = {from, chat_room_id, message};
        PGconn *conn = getConnection();
        PGresult *res = PQexecParams(conn, PGstatement, 3, NULL, paramValues, NULL, NULL, 0);

        if (PQresultStatus(res) != PGRES_TUPLES_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                json_object_object_add(response, "action", json_object_new_string("NEW_MESSAGE"));
                json_object_object_add(response, "status", json_object_new_string("OK"));
                json_object_object_add(response, "message", json_object_new_string(message));
                json_object_object_add(response, "sender", json_object_new_string(PQgetvalue(res, 0, 0)));
                json_object_object_add(response, "chat_room_id", json_object_new_string(chat_room_id));
        }
        PQclear(res);
        PQfinish(conn);
        return ALL;
}

int getMessage(const int chat_room_id, json_object *response)
{
        int rows = 0;
        PGconn *conn = getConnection();
        char sql[256];
        sprintf(sql, "SELECT m.message_id, u.username, m.chat, m.message_content FROM message m, user_account u WHERE chat = %d AND m.sender = u.user_id ORDER BY m.message_id", chat_room_id);
        PGresult *res = PQexec(conn, sql);

        json_object_object_add(response, "action", json_object_new_string("OPEN_ROOM"));
        if (PQresultStatus(res) != PGRES_TUPLES_OK)
        {
                json_object_object_add(response, "status", json_object_new_string("FAILED"));
                json_object_object_add(response, "message", json_object_new_string("Impossibile aprire la stanza"));
        }
        else
        {
                rows = PQntuples(res);
                json_object_object_add(response, "status", json_object_new_string("OK"));
                json_object *message_list = json_object_new_array();
                for (int i = 0; i < rows; i++)
                {
                        json_object *obj = json_object_new_object();
                        json_object_object_add(obj, "message_id", json_object_new_int(atoi(PQgetvalue(res, i, 0))));
                        json_object_object_add(obj, "sender", json_object_new_string(PQgetvalue(res, i, 1)));
                        json_object_object_add(obj, "chat", json_object_new_int(atoi(PQgetvalue(res, i, 2))));
                        json_object_object_add(obj, "message_content", json_object_new_string(PQgetvalue(res, i, 3)));

                        json_object_array_add(message_list, obj);
                }
                json_object_object_add(response, "message_list", message_list);
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int joinRoom(const int user_id, const int chat_room_id, json_object *response)
{
        int rows = 0;
        PGconn *conn = getConnection();
        char sql[256];
        sprintf(sql, "INSERT INTO join_requests(user_id, chat_room, accepted) VALUES (%d, %d, false)", user_id, chat_room_id);
        PGresult *res = PQexec(conn, sql);

        json_object_object_add(response, "action", json_object_new_string("JOIN_ROOM"));
        if (PQresultStatus(res) != PGRES_COMMAND_OK)
        {
                printf("%s\n", PQresultErrorMessage(res));
                json_object_object_add(response, "status", json_object_new_string("FAILED"));
                json_object_object_add(response, "message", json_object_new_string("Impossibile accedere alla stanza"));
        }
        else
        {
                json_object_object_add(response, "status", json_object_new_string("OK"));
                json_object_object_add(response, "message", json_object_new_string("Richiesta effettuata con successo"));
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int waitingUsers(const int user_id, json_object *response)
{
        json_object_object_add(response, "action", json_object_new_string("GET_WAITING_USERS"));
        json_object *waiting = json_object_new_array();
        json_object_object_add(response, "waiting", waiting);

        int rows, char_converted;
        char char_id[10];
        sprintf(char_id, "%d", user_id);
        char *PGstatement = "select user_account.user_id, request.chat_room_id, request.chat_room_name, user_account.username "
                            "from (join_requests INNER JOIN chat_room ON chat_room=chat_room_id)as REQUEST INNER JOIN user_account ON REQUEST.user_id=user_account.user_id "
                            "where accepted='false' and room_owner= $1::INTEGER";
        const char *paramValues[1] = {(const char *)char_id};
        PGconn *conn = getConnection();
        PGresult *res = PQexecParams(conn, PGstatement, 1, NULL, paramValues, NULL, NULL, 0);

        if (PQresultStatus(res) != PGRES_TUPLES_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                rows = PQntuples(res);
                if (rows == 0)
                {
                        json_object_object_add(response, "status", json_object_new_string("FAILED"));
                        json_object_object_add(response, "message", json_object_new_string("Non ci sono utenti in attesa"));
                }
                else
                {
                        for (int i = 0; i < rows; i++)
                        {
                                json_object *obj = json_object_new_object();

                                char_converted = strtol(PQgetvalue(res, i, 0), NULL, 10);
                                json_object_object_add(obj, "user_id", json_object_new_int64(char_converted));

                                char_converted = strtol(PQgetvalue(res, i, 1), NULL, 10);
                                json_object_object_add(obj, "chat_room_id", json_object_new_int64(char_converted));

                                char_converted = strtol(PQgetvalue(res, i, 2), NULL, 10);
                                json_object_object_add(obj, "chat_room_name", json_object_new_string(PQgetvalue(res, i, 2)));

                                char_converted = strtol(PQgetvalue(res, i, 3), NULL, 10);
                                json_object_object_add(obj, "username", json_object_new_string(PQgetvalue(res, i, 3)));
                                json_object_array_add(waiting, obj);
                        }
                }
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int acceptRequest(const int user_id, const int chat_room_id, json_object *response)
{
        int rows = 0;
        PGconn *conn = getConnection();
        char sql[256];
        sprintf(sql, "UPDATE join_requests SET accepted=true WHERE join_requests.user_id = %d AND join_requests.chat_room = %d", user_id, chat_room_id);
        PGresult *res = PQexec(conn, sql);

        json_object_object_add(response, "action", json_object_new_string("ACCEPT_REQUEST"));
        if (PQresultStatus(res) != PGRES_COMMAND_OK)
        {
                printf("%s\n", PQresultErrorMessage(res));
                json_object_object_add(response, "status", json_object_new_string("FAILED"));
                json_object_object_add(response, "message", json_object_new_string("Impossibile accettare la richiesta"));
        }
        else
        {
                json_object_object_add(response, "status", json_object_new_string("OK"));
                json_object_object_add(response, "message", json_object_new_string("Richiesta accettata"));
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int removeUser(const int user_id, const int chat_room_id, json_object *response)
{
        int rows = 0;
        PGconn *conn = getConnection();
        char sql[256];
        sprintf(sql, "DELETE FROM join_requests WHERE join_requests.user_id = %d AND join_requests.chat_room = %d", user_id, chat_room_id);
        PGresult *res = PQexec(conn, sql);

        if (PQresultStatus(res) != PGRES_COMMAND_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                rows = PQntuples(res);
                json_object_object_add(response, "action", json_object_new_string("DELETE"));
                json_object_object_add(response, "chat_room_id", json_object_new_int(chat_room_id));
        }

        PQclear(res);
        PQfinish(conn);
        return USER;
}

int createRoom(const int fd, const int room_owner_id, const char *chat_room_name, json_object *response)
{
        int rows = 0;
        int char_converted;
        char char_id[10];
        sprintf(char_id, "%d", room_owner_id);
        char *PGstatement = "INSERT INTO Chat_room (chat_room_name, room_owner) VALUES ($1::VARCHAR, $2::INTEGER) RETURNING chat_room_id";
        const char *paramValues[2] = {chat_room_name, (const char *)char_id};
        PGconn *conn = getConnection();
        PGresult *res = PQexecParams(conn, PGstatement, 2, NULL, paramValues, NULL, NULL, 0);

        if (PQresultStatus(res) != PGRES_TUPLES_OK)
        {
                printf("%s\n", PQresultErrorMessage(res));
                json_object_object_add(response, "action", json_object_new_string("CREATE"));
                json_object_object_add(response, "status", json_object_new_string("FAILED"));
        }
        else
        {
                rows = PQntuples(res);
                char_converted = strtol(PQgetvalue(res, 0, 0), NULL, 10);
                json_object_object_add(response, "action", json_object_new_string("CREATE"));
                json_object_object_add(response, "status", json_object_new_string("OK"));
                json_object_object_add(response, "chat_room_id", json_object_new_int64(char_converted));
                json_object_object_add(response, "chat_room_name", json_object_new_string(chat_room_name));
                json_object_object_add(response, "room_owner_id", json_object_new_int64(room_owner_id));

                json_object *new_room = json_object_new_array();
                json_object_object_add(chat_room_list, PQgetvalue(res, 0, 0), new_room);
                json_object_array_add(new_room, json_object_new_int64(fd));
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}

int updateRoom(const char *chat_room_owner, const char *chat_room_id, const char *new_name, json_object *response)
{
        int rows = 0;
        char *PGstatement = "UPDATE Chat_room SET chat_room_name = $3::VARCHAR WHERE chat_room_id = $2::INTEGER AND room_owner = $1::INTEGER";
        const char *paramValues[3] = {chat_room_owner, chat_room_id, new_name};
        PGconn *conn = getConnection();
        PGresult *res = PQexecParams(conn, PGstatement, 3, NULL, paramValues, NULL, NULL, 0);

        if (PQresultStatus(res) != PGRES_COMMAND_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                rows = PQntuples(res);
                json_object_object_add(response, "action", json_object_new_string("UPDATE"));
                json_object_object_add(response, "chat_room_id", json_object_new_int(atoi(chat_room_id)));
                json_object_object_add(response, "chat_room_name", json_object_new_string(new_name));
        }
        PQclear(res);
        PQfinish(conn);
        return ALL;
}

int deleteRoom(const char *chat_room_id, json_object *response)
{
        int rows = 0;
        char *PGstatement = "DELETE FROM Chat_room WHERE chat_room_id = $1::INTEGER";
        const char *paramValues[1] = {chat_room_id};
        PGconn *conn = getConnection();
        PGresult *res = PQexecParams(conn, PGstatement, 1, NULL, paramValues, NULL, NULL, 0);

        if (PQresultStatus(res) != PGRES_COMMAND_OK)
                printf("%s\n", PQresultErrorMessage(res));
        else
        {
                rows = PQntuples(res);
                json_object_object_add(response, "action", json_object_new_string("DELETE"));
                json_object_object_add(response, "chat_room_id", json_object_new_int(atoi(chat_room_id)));
        }
        PQclear(res);
        PQfinish(conn);
        return ALL;
}

int getRooms(int fd, const int user_id, json_object *response)
{
        json_object_object_add(response, "action", json_object_new_string("GET_ROOMS"));
        json_object *accepted = json_object_new_array();
        json_object_object_add(response, "accepted", accepted);
        json_object *waiting = json_object_new_array();
        json_object_object_add(response, "waiting", waiting);
        json_object *other = json_object_new_array();
        json_object_object_add(response, "other", other);

        int rows, found, char_converted;
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
                for (int i = 0; i < json_object_array_length(accepted); ++i)
                {
                        found = 0;
                        json_object *chat_room_id, *array;
                        json_object *room = json_object_array_get_idx(accepted, i);
                        json_object_object_get_ex(room, "chat_room_id", &chat_room_id);
                        int chat = json_object_get_int(chat_room_id);
                        sprintf(char_id, "%d", chat);
                        json_object_object_get_ex(chat_room_list, char_id, &array);
                        for (int j = 0; j < json_object_array_length(array); ++j)
                        {
                                if (json_object_get_int(json_object_array_get_idx(array, j)) == fd)
                                        found = 1;
                        }
                        if (!found)
                                json_object_array_add(array, json_object_new_int64(fd));
                }
        }
        PQclear(res);
        PQfinish(conn);
        return USER;
}
