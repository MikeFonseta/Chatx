package com.mikefonseta.chatx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerConnection implements Runnable {

    private static final String SERVER_IP = "192.168.1.16";
    private static final int SERVER_PORT = 8888;
    private Socket socket;
    private BufferedReader input;

    @Override
    public void run() {

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            socket = new Socket(serverAddr, SERVER_PORT);
            while (!Thread.currentThread().isInterrupted()) {
                this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = input.readLine();
                if (null == message || "Disconnect".contentEquals(message)) {
                    boolean interrupted = Thread.interrupted();
                    message = "Server Disconnected: " + interrupted;
                    System.out.println(message);
                    break;
                }
                System.out.println("Server: " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(final String message) {
        new Thread(() -> {
            try {
                if (null != socket) {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.print(message);
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}