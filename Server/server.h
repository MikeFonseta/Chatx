#ifndef SERVER_H_INCLUDED
#define SERVER_H_INCLUDED

#include <netinet/in.h>
#define PORT 8888

struct client
{
	int socketfd;
	struct sockaddr_in address;
};

void *client_handler(void *arg);

#endif // SERVER_H_INCLUDED