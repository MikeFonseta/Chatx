package com.example.chat2023.util;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import androidx.lifecycle.LifecycleService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionService extends LifecycleService {
    public static final String SEND = "com.example.chat2023.SEND";
    public static final String CONNECT = "com.example.chat2023.CONNECT";

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        serviceLooper = handlerThread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;

        if (intent.getAction().equals(CONNECT))
            msg.what = ServiceHandler.OPEN;
        if (intent.getAction().equals(SEND)) {
            msg.what = ServiceHandler.SEND;
            msg.setData(intent.getExtras());
        }

        System.out.println("msg: " + msg);
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    private void sendResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            String action = json.getString("action");

            if (action.equals("LOGIN") || action.equals("REGISTER")) {
                Intent intent = new Intent("signing");
                intent.putExtra("response", json.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            if (action.equals("GETROOMS")) {
                Intent intent = new Intent("chatrooms");
                intent.putExtra("response", json.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private final class ServiceHandler extends Handler {

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
                    sendResponse(resp);
                    break;
            }
        }

        public void startConnection() {
            String ip = "192.168.0.113";
            int port = 8888;
            try {
                chatSocket = new Socket(ip, port);
                in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                out = new PrintWriter(chatSocket.getOutputStream(), true);
            } catch (UnknownHostException e) {
                System.err.println("Cannot find host called: " + ip);
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Could not establish connection for " + ip);
                e.printStackTrace();
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
}