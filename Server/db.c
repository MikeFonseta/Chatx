#include <stdio.h>
#include <postgresql/libpq-fe.h>
#include <stdlib.h>
#include <string.h>


PGconn* getConnection();
int checkUser(char* user);
int registerUser(char* user,char* password);
int loginUser(char* user,char* password);

// int main(int argc, char const *argv[])
// {
// 	char* user = "Mike177p";
// 	char* password = "password1";
// 	printf("User: %s\n", user);
// 	printf("Password: %s\n", password);
// 	registerUser(user,password);
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
	sprintf(sql,"SELECT * FROM user_test WHERE user_test.user = '%s'",user);
	PGresult *res = PQexec(conn, sql);    
    

	if (PQresultStatus(res) != PGRES_TUPLES_OK) {   
		printf("SELECT failed: %d\n", PQntuples(res)); 
		rows = -1;
    }else{
		rows = PQntuples(res);	
	}

	PQclear(res);
    PQfinish(conn);

	printf("%d\n",rows);
    return rows;
};

int registerUser(char* user,char* password)
{
	PGconn* conn = getConnection();

	int userTaken = checkUser(user);

	if(userTaken == 0)
	{
		char sql[256];
		sprintf(sql,"INSERT INTO user_test VALUES('%s','%s')",user,password);
		PGresult *res = PQexec(conn, sql);    

		if (PQresultStatus(res) != PGRES_COMMAND_OK) {

			printf("INSERT failed\n");    
			return -1;
		}
		PQclear(res);
	} 
	else{
		printf("User already taken\n");
		return 1;
	}
	PQfinish(conn);

	return 0;
};

int loginUser(char* user,char* password)
{
	int rows = 0;
	PGconn* conn = getConnection();
	char sql[256];
	sprintf(sql,"SELECT * FROM user_test WHERE user_test.user = '%s' AND user_test.password = '%s'",user,password);
	
	PGresult *res = PQexec(conn, sql);    
    
	if (PQresultStatus(res) != PGRES_TUPLES_OK) {   
		rows = -1;
    }else{
		rows = PQntuples(res);	
	}

	printf("SELECT: %d\n %s %s", PQntuples(res),user, password); 

	PQclear(res);
    PQfinish(conn);
    return rows==1 ? 1 : 0;
};