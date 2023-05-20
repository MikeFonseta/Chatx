#ifndef SERVER_H_INCLUDED
#define SERVER_H_INCLUDED

#include <netinet/in.h>
#include <json-c/json.h>

#define USER 1
#define ALL 2

typedef struct
{
	int socketfd;
	struct sockaddr_in address;
} client;

void *client_handler(void *arg);

#endif // SERVER_H_INCLUDED