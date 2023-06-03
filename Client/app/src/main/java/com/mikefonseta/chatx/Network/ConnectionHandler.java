package com.mikefonseta.chatx.Network;

import com.mikefonseta.chatx.Controller.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionHandler {

    private static final boolean exit = false;
    private static final ConnectionHandler connectionHandler = new ConnectionHandler();
    private static PrintWriter printWriter;
    private BufferedReader bufferedReader;


    private ConnectionHandler() {
        String IP_ADDRESS = "192.168.137.82";
        int PORT = 8889;
        try {
            Socket socket = new Socket(IP_ADDRESS, PORT);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            listen();
        } catch (UnknownHostException e) {
            System.err.println("Cannot find host called: " + IP_ADDRESS);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("failed to create socket");
            e.printStackTrace();
        }
        System.out.println("connected");
    }

    public static ConnectionHandler getInstance() {
        return connectionHandler;
    }

    public static void doRequest(String message) {
        Thread thread = new Thread(() -> {
            System.out.println(message);
            printWriter.println(message);
        });
        thread.start();
    }

    public void listen() {
        Thread listenThread = new Thread(() -> {
            while (!exit) {
                try {
                    String response = bufferedReader.readLine();
                    System.out.println(response);
                    Controller.evaluate_action(response);
                } catch (IOException e) {
                    System.out.println("failed to read data");
                    e.printStackTrace();
                    break;
                }
            }
        });
        listenThread.start();
    }

}