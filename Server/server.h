#ifndef SERVER_H_INCLUDED
#define SERVER_H_INCLUDED

#include <netinet/in.h>
#define PORT 8888

typedef struct
{
	int socketfd;
	struct sockaddr_in address;
}client;

void *client_handler(void *arg);

#endif // SERVER_H_INCLUDED