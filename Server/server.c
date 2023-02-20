#include <stdio.h>
#include <string.h> //strlen
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>	   //close
#include <arpa/inet.h> //close
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>
#include <sys/time.h> //FD_SET, FD_ISSET, FD_ZERO macros
#include "db.c"
#include "server.h"

struct client
{
	int sock;
	struct sockaddr_in address;
};

int main(int argc, char *argv[])
{
	int socket_Master,*new_sock,connect_sd,listen_sd;
	int* thread_sd;
	socklen_t client_len;
	pthread_t tid;
	struct sockaddr_in address,client_addr;
	char buffer[1025]; // data buffer of 1K

	// create a socket_Master
	if ((socket_Master = socket(AF_INET, SOCK_STREAM, 0)) == 0)
	{
		perror("socket_Master failed");
		exit(EXIT_FAILURE);
	}

	// type of socket created
	address.sin_family = AF_INET;
	address.sin_addr.s_addr = INADDR_ANY;
	address.sin_port = htons(PORT);

	// bind the socket_Master to localhost port 8888
	if (bind(socket_Master, (struct sockaddr *)&address, sizeof(address)) < 0)
	{
		perror("bind failed");
		exit(EXIT_FAILURE);
	}

	printf("Listener on port %d \n", PORT);

	// try to specify maximum of 3 pending connections for the master socket_Master
	if (listen(socket_Master, 3) < 0)
	{
		perror("listen");
		exit(EXIT_FAILURE);
	}

	// accept the incoming connection
	client_len = sizeof(client_len);

	while(1)
	{
		connect_sd = accept(socket_Master, (struct sockaddr *) &client_addr, &client_len);
		if(connect_sd != -1)
		{
			struct client newClient;
			newClient.sock = connect_sd;
			newClient.address = client_addr;

			printf("[+] %s:%d connected\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
			pthread_create(&tid, 0, client_handler, (void *)&newClient);
			pthread_detach(tid);
		}
	}

	return 0;
}

void* client_handler(void * arg)
{
	char client_message[100];
	struct client clientInfo = *(struct client*)arg;
	struct sockaddr_in address = clientInfo.address;

	int read_size,write_size;
	char* message;


	while((read_size = recv(clientInfo.sock,client_message,100,0)) > 0)
	{
		//printf("[ ] %s:%d %s\n", inet_ntoa(address.sin_addr), ntohs(address.sin_port),client_message);
		char* response = evaluate_action(json_tokener_parse(client_message));
		write(clientInfo.sock,response,strlen(response));
		free(response);
	}

	if(read_size == 0)
	{
		printf("[-] %s:%d disconnected\n", inet_ntoa(address.sin_addr), ntohs(address.sin_port));
		fflush(stdout);
	}else if(read_size == -1)
	{
		perror("recv failed");
	}

	return 0;
}


