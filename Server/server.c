// Example code: A simple server side code, which echos back the received message.
// Handle multiple socket connections with select and fd_set on Linux
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <poll.h>
//#include <errno.h>
//#include <sys/time.h> //FD_SET, FD_ISSET, FD_ZERO macros
#include "db.c"

#define TRUE 1
#define FALSE 0
#define PORT 8888

// Get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }

    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

// Return a listening socket
int get_listener_socket(void)
{
    int listener;     // Listening socket descriptor
    int yes=1;        // For setsockopt() SO_REUSEADDR, below
    int rv;

    struct addrinfo hints, *ai, *p;

    // Get us a socket and bind it
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    if ((rv = getaddrinfo(NULL, PORT, &hints, &ai)) != 0) {
        fprintf(stderr, "selectserver: %s\n", gai_strerror(rv));
        exit(1);
    }

    for(p = ai; p != NULL; p = p->ai_next) {
        listener = socket(p->ai_family, p->ai_socktype, p->ai_protocol);
        if (listener < 0) {
            continue;
        }

        // Lose the pesky "address already in use" error message
        setsockopt(listener, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));

        if (bind(listener, p->ai_addr, p->ai_addrlen) < 0) {
            close(listener);
            continue;
        }

        break;
    }

    // If we got here, it means we didn't get bound
    if (p == NULL) {
        return -1;
    }

    freeaddrinfo(ai); // All done with this

    // Listen
    if (listen(listener, 10) == -1) {
        return -1;
    }

    return listener;
}

// Add a new file descriptor to the set
void add_to_pfds(struct pollfd *pfds[], int newfd, int *fd_count, int *fd_size)
{
    // If we don't have room, add more space in the pfds array
    if (*fd_count == *fd_size) {
        *fd_size *= 2; // Double it

        *pfds = realloc(*pfds, sizeof(**pfds) * (*fd_size));
    }

    (*pfds)[*fd_count].fd = newfd;
    (*pfds)[*fd_count].events = POLLIN; // Check ready-to-read

    (*fd_count)++;
}

// Remove an index from the set
void del_from_pfds(struct pollfd pfds[], int i, int *fd_count)
{
    // Copy the one from the end over this one
    pfds[i] = pfds[*fd_count-1];

    (*fd_count)--;
}

void client_handler(int sockfd);

int main(int argc, char *argv[])
{
//	int opt = TRUE;
//	int master_socket, addrlen, new_socket, client_socket[30],
//		max_clients = 30, activity, i, valread, sd;
//	int max_sd;
//	struct sockaddr_in address;
//
//	char buffer[1025]; // data buffer of 1K
//
//	// set of socket descriptors
//	fd_set readfds;
//
//	// a message
//	char *message = "Chatx server v1.0 \r\n";
//
//	// initialise all client_socket[] to 0 so not checked
//	for (i = 0; i < max_clients; i++)
//	{
//		client_socket[i] = 0;
//	}
//
//	// create a master socket
//	if ((master_socket = socket(AF_INET, SOCK_STREAM, 0)) == 0)
//	{
//		perror("socket failed");
//		exit(EXIT_FAILURE);
//	}
//
//	// set master socket to allow multiple connections ,
//	// this is just a good habit, it will work without this
//	if (setsockopt(master_socket, SOL_SOCKET, SO_REUSEADDR, (char *)&opt,
//				   sizeof(opt)) < 0)
//	{
//		perror("setsockopt");
//		exit(EXIT_FAILURE);
//	}
//
//	// type of socket created
//	address.sin_family = AF_INET;
//	address.sin_addr.s_addr = INADDR_ANY;
//	address.sin_port = htons(PORT);
//
//	// bind the socket to localhost port 8888
//	if (bind(master_socket, (struct sockaddr *)&address, sizeof(address)) < 0)
//	{
//		perror("bind failed");
//		exit(EXIT_FAILURE);
//	}
//	printf("Listener on port %d \n", PORT);
//
//	// try to specify maximum of 3 pending connections for the master socket
//	if (listen(master_socket, 3) < 0)
//	{
//		perror("listen");
//		exit(EXIT_FAILURE);
//	}
//
//	// accept the incoming connection
//	addrlen = sizeof(address);
//	puts("Waiting for connections ...");
//
//	while (TRUE)
//	{
//		// clear the socket set
//		FD_ZERO(&readfds);
//
//		// add master socket to set
//		FD_SET(master_socket, &readfds);
//		max_sd = master_socket;
//
//		// add child sockets to set
//		for (i = 0; i < max_clients; i++)
//		{
//			// socket descriptor
//			sd = client_socket[i];
//
//			// if valid socket descriptor then add to read list
//			if (sd > 0)
//				FD_SET(sd, &readfds);
//
//			// highest file descriptor number, need it for the select function
//			if (sd > max_sd)
//				max_sd = sd;
//		}
//
//		// wait for an activity on one of the sockets , timeout is NULL ,
//		// so wait indefinitely
//		activity = select(max_sd + 1, &readfds, NULL, NULL, NULL);
//
//		if ((activity < 0) && (errno != EINTR))
//		{
//			printf("select error");
//		}
//
//		// If something happened on the master socket ,
//		// then its an incoming connection
//		if (FD_ISSET(master_socket, &readfds))
//		{
//			if ((new_socket = accept(master_socket,
//									 (struct sockaddr *)&address, (socklen_t *)&addrlen)) < 0)
//			{
//				perror("accept");
//				exit(EXIT_FAILURE);
//			}
//
//			// inform user of socket number - used in send and receive commands
//			printf("New connection , socket fd is %d , ip is : %s , port : %d\n", new_socket, inet_ntoa(address.sin_addr), ntohs(address.sin_port));
//
//			// send new connection greeting message
//			if (send(new_socket, message, strlen(message), 0) != strlen(message))
//			{
//				perror("send");
//			}
//
//			// add new socket to array of sockets
//			for (i = 0; i < max_clients; i++)
//			{
//				// if position is empty
//				if (client_socket[i] == 0)
//				{
//					client_socket[i] = new_socket;
//					printf("Adding to list of sockets as %d\n", i);
//					break;
//				}
//			}
//		}
//
//		// else its some IO operation on some other socket
//		for (i = 0; i < max_clients; i++)
//		{
//			sd = client_socket[i];
//
//			if (FD_ISSET(sd, &readfds))
//			{
//				// Check if it was for closing
//				if ((valread = recv(sd, buffer, 1024, MSG_PEEK)) <= 0)
//				{
//					if (valread == 0)
//					{
//						// Somebody disconnected , get his details and print
//						getpeername(sd, (struct sockaddr *)&address, (socklen_t *)&addrlen);
//						printf("Host disconnected , ip %s , port %d \n", inet_ntoa(address.sin_addr), ntohs(address.sin_port));
//					}
//					else
//						perror("recv");
//					// Close the socket and mark as 0 in list for reuse
//					close(sd);
//					client_socket[i] = 0;
//				}
//
//				// Read the incoming message
//				else
//				{
//					client_handler(sd);
//				}
//			}
//		}
//	}
    int listener;     // Listening socket descriptor

    int newfd;        // Newly accept()ed socket descriptor
    struct sockaddr_storage remoteaddr; // Client address
    socklen_t addrlen;

    char buf[256];    // Buffer for client data

    char remoteIP[INET6_ADDRSTRLEN];

    // Start off with room for 5 connections
    // (We'll realloc as necessary)
    int fd_count = 0;
    int fd_size = 5;
    struct pollfd *pfds = malloc(sizeof *pfds * fd_size);

    // Set up and get a listening socket
    listener = get_listener_socket();

    if (listener == -1) {
        fprintf(stderr, "error getting listening socket\n");
        exit(1);
    }

    // Add the listener to set
    pfds[0].fd = listener;
    pfds[0].events = POLLIN; // Report ready to read on incoming connection

    fd_count = 1; // For the listener

    // Main loop
    for(;;) {
        int poll_count = poll(pfds, fd_count, -1);

        if (poll_count == -1) {
            perror("poll");
            exit(1);
        }

        // Run through the existing connections looking for data to read
        for(int i = 0; i < fd_count; i++) {

            // Check if someone's ready to read
            if (pfds[i].revents & POLLIN) { // We got one!!

                if (pfds[i].fd == listener) {
                    // If listener is ready to read, handle new connection

                    addrlen = sizeof remoteaddr;
                    newfd = accept(listener,
                        (struct sockaddr *)&remoteaddr,
                        &addrlen);

                    if (newfd == -1) {
                        perror("accept");
                    } else {
                        add_to_pfds(&pfds, newfd, &fd_count, &fd_size);

                        printf("pollserver: new connection from %s on "
                            "socket %d\n",
                            inet_ntop(remoteaddr.ss_family,
                                get_in_addr((struct sockaddr*)&remoteaddr),
                                remoteIP, INET6_ADDRSTRLEN),
                            newfd);
                    }
                } else {
                    // If not the listener, we're just a regular client
                    int nbytes = recv(pfds[i].fd, buf, sizeof buf, 0);

                    int sender_fd = pfds[i].fd;

                    if (nbytes <= 0) {
                        // Got error or connection closed by client
                        if (nbytes == 0) {
                            // Connection closed
                            printf("pollserver: socket %d hung up\n", sender_fd);
                        } else {
                            perror("recv");
                        }

                        close(pfds[i].fd); // Bye!

                        del_from_pfds(pfds, i, &fd_count);

                    } else {
                        // We got some good data from a client

                        for(int j = 0; j < fd_count; j++) {
                            // Send to everyone!
                            int dest_fd = pfds[j].fd;

                            // Except the listener and ourselves
                            if (dest_fd != listener && dest_fd != sender_fd) {
                                if (send(dest_fd, buf, nbytes, 0) == -1) {
                                    perror("send");
                                }
                            }
                        }
                    }
                } // END handle data from client
            } // END got ready-to-read from poll()
        } // END looping through file descriptors
    } // END for(;;)--and you thought it would never end!

    return 0;
}

void client_handler(int sockfd)
{
	json_object *received;
	json_object *response = json_object_new_object();
	if ((received = json_object_from_fd_ex(sockfd, -1)) == NULL)
		printf("Error: %s\n", json_util_get_last_err());
	else {
		printf("json file:\n\n%s\n", json_object_to_json_string_ext(received, JSON_C_TO_STRING_PRETTY));
		evaluate_action(received, response);
		const char *response_char = json_object_to_json_string_ext(received, JSON_C_TO_STRING_PLAIN);
		write(sockfd, response_char, strlen(response_char));
	}
		
	json_object_put(received);
	json_object_put(response);
}
