package com.mikefonseta.chatx.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

;

public class ConnectionHandler {

    private static ConnectionHandler connectionHandler = null;
    private final String IP_ADDRESS = "192.168.1.16";
    private final int PORT = 8881;
    private Socket socket;
    private boolean exit = false;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private ConnectionHandler() {

        try {
            socket = new Socket(IP_ADDRESS, PORT);
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("failed to create socket");
            e.printStackTrace();
        }

        System.out.println("connected");


    }

    public static ConnectionHandler getInstance() {
        if (connectionHandler == null) {
            connectionHandler = new ConnectionHandler();
        }
        return connectionHandler;
    }

    public void listen(responseCallBack responseCallBack) {
        exit = false;
        while (!exit) {
            try {
                String response = this.bufferedReader.readLine();
                System.out.println(response);
                responseCallBack.onResponse(response);
            } catch (IOException e) {
                System.out.println("failed to read data");
                e.printStackTrace();
                break;
            }
        }
    }

    public void stopListen() {
        System.out.println("Stopping Listen");
        exit = true;
    }

    public void doRequest(String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                printWriter.println(message);
                printWriter.flush();
            }
        });
        thread.start();
    }

}