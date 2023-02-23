// Example code: A simple server side code, which echos back the received message.
// Handle multiple socket_Master connections with select and fd_set on Linux
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

#define TRUE 1
#define FALSE 0
#define PORT 8888

struct client
{
	int socketfd;
	struct sockaddr_in address;
};

void *client_handler(void *arg);

int main(int argc, char *argv[])
{
	int socket_Master, connect_sd;
	socklen_t client_len;
	pthread_t tid;
	struct sockaddr_in address, client_addr;
	struct client *newClient;

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

	while (1)
	{
		connect_sd = accept(socket_Master, (struct sockaddr *)&client_addr, &client_len);
		if (connect_sd != -1)
		{
			newClient = malloc(sizeof(struct client));
			newClient->socketfd = connect_sd;
			newClient->address = client_addr;
			// newClient.sock = connect_sd;
			// newClient.address = client_addr;

			printf("[+] %s:%d connected\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
			pthread_create(&tid, 0, client_handler, (void *)newClient);
			pthread_detach(tid);
		}
	}

	return 0;
}

// void client_handler(int sockfd)
//{
//	json_object *received;
//	json_object *response = json_object_new_object();
//	if ((received = json_object_from_fd_ex(sockfd, -1)) == NULL)
//		printf("Error: %s\n", json_util_get_last_err());
//	else {
//		printf("json file:\n\n%s\n", json_object_to_json_string_ext(received, JSON_C_TO_STRING_PRETTY));
//		evaluate_action(received, response);
//		const char *response_char = json_object_to_json_string_ext(received, JSON_C_TO_STRING_PLAIN);
//		write(sockfd, response_char, strlen(response_char));
//	}
//
//	json_object_put(received);
//	json_object_put(response);
// }

void *client_handler(void *arg)
{
	char client_message[100];
	// struct client clientInfo = *(struct client *)arg;
	struct client *clientInfo = (struct client *)arg;
	// struct sockaddr_in address = clientInfo.address;

	int read_size, write_size;
	char *message;

	while ((read_size = recv(clientInfo->socketfd, client_message, 100, 0)) > 0)
	{
		printf("[ ] %s:%d %s\n", inet_ntoa(clientInfo->address.sin_addr), ntohs(clientInfo->address.sin_port), client_message);
		write(clientInfo->socketfd, client_message, strlen(client_message));
	}

	if (read_size == 0)
	{
		printf("[ ] %s:%d disconnected\n", inet_ntoa(clientInfo->address.sin_addr), ntohs(clientInfo->address.sin_port));
		fflush(stdout);
	}
	else if (read_size == -1)
	{
		perror("recv failed");
	}

	return 0;
}