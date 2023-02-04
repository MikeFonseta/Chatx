package com.example.chat2023.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

final class ServiceHandler extends Handler {

    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int SEND = 2;
    private Socket chatSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServiceHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case OPEN:
                startConnection();
                break;
            case CLOSE:
                stopConnection();
                break;
            case SEND:
                String json = msg.getData().getString("json");
                System.out.println(json);
                String resp = send(json);
                System.out.println(resp);
                break;
        }
        // Stop the service using the startId, so that we don't stop
        // the service in the middle of handling another job
//        connectionService.stopSelf(msg.arg1);
    }

    public void startConnection() {
        String ip = "192.168.0.108";
        int port = 8888;
        try {
            chatSocket = new Socket(ip, port);
            out = new PrintWriter(chatSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(String msg) {
        out.println(msg);
        String resp;
        try {
            resp = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            chatSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
