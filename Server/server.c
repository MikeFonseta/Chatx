#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <pthread.h>
#include "server.h"
#include "db.h"

json_object* chat_room_list;

int main(int argc, char *argv[])
{
	chat_room_list = getAllChatRoom();

	int socket_Master, connect_sd;
	socklen_t client_len;
	pthread_t tid;
	struct sockaddr_in address, client_addr;
	client *newClient;


	// create a socket_Master
	if ((socket_Master = socket(AF_INET, SOCK_STREAM, 0)) == 0)
	{
		perror("socket_Master failed");
		exit(EXIT_FAILURE);
	}

	// type of socket created
	address.sin_family = AF_INET;
	address.sin_addr.s_addr = INADDR_ANY;
	// address.sin_port = htons(PORT);
	address.sin_port = htons(atoi(argv[1]));

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
	client_len = sizeof(address);
	while (1)
	{
		connect_sd = accept(socket_Master, (struct sockaddr *)&client_addr, &client_len);
		if (connect_sd != -1)
		{
			
			newClient = malloc(sizeof(client));
			newClient->socketfd = connect_sd;
			newClient->address = client_addr;

			pthread_create(&tid, 0, client_handler, (void *)newClient);
			pthread_detach(tid);
		}
	}

	return 0;
}


void *client_handler(void *arg)
{
	char client_message[100];
	client *clientInfo = (client *)arg;
	int read_size, write_size, sent_size;
	char *message;
	json_object *received;
	json_object *response;

	json_object* chat_room_list = getAllChatRoom();
	printf("[SOCKET: %d] [IP: %s:%d] connected\n", clientInfo->socketfd,inet_ntoa(clientInfo->address.sin_addr), ntohs(clientInfo->address.sin_port));
	fflush(stdout);

	while ((read_size = recv(clientInfo->socketfd, client_message, 100, 0)) > 0)
	{
		if ((received = json_tokener_parse(client_message)) == NULL)
			printf("Error: %s\n", json_util_get_last_err());
		else
		{
			printf("[SOCKET: %d] [IP: %s:%d] %s\n", 
				clientInfo->socketfd,
				inet_ntoa(clientInfo->address.sin_addr), 
				ntohs(clientInfo->address.sin_port),
				json_object_to_json_string_ext(received, JSON_C_TO_STRING_PLAIN));
			
			response = json_object_new_object();

			evaluate_action(clientInfo->socketfd ,chat_room_list, received, response);
			
			const char *response_char = json_object_to_json_string_ext(response, JSON_C_TO_STRING_PLAIN);
			char *sending = malloc(strlen(response_char) + 2);
			strcpy(sending, response_char);
			strcat(sending, "\n");
			int response_size = strlen(sending);

			// printf("sending: %s\n", sending);
			// printf("size: %d\n", response_size);


			if (sent_size = send(clientInfo->socketfd, sending, response_size, 0) == -1)
				perror("send failed");
			json_object_put(response);
		}
	}

	if (read_size == 0)
	{
		logout(clientInfo->socketfd,chat_room_list);
		printf("[-] %s:%d disconnected\n", inet_ntoa(clientInfo->address.sin_addr), ntohs(clientInfo->address.sin_port));
		fflush(stdout);
	}
	else if (read_size == -1)
	{
		perror("recv failed");
	}
	return 0;
}