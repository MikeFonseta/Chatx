#include <stdio.h>
#include <postgresql/libpq-fe.h>
#include <stdlib.h>
#include <string.h>


PGconn* getConnection();
int checkUser(char* user);
int registerUser(char* user,char* password);
int loginUser(char* user,char* password);

// int main()
// {
// 	char* user = "Mike";
// 	char* password = "12345678";
// 	printf("Register user: %d\n", registerUser(user,password));
// 	printf("Login user: %d\n", loginUser(user,password));

// 	return 0;
// }

PGconn* getConnection() {

	PGconn *conn;
	conn = PQconnectdb("dbname=chatx host=localhost user=mike password=admin");

	if (PQstatus(conn) == CONNECTION_BAD) {
			puts("We were unable to connect to the database");
			exit(0);
	}
	return conn;
};

int checkUser(char* user)
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

int registerUser(char* user,char* password)
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

int loginUser(char* user,char* password)
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

