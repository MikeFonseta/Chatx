package com.example.chat2023.util;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

public class ConnectionService extends Service {
    public static final String LOGIN = "com.example.chat2023.LOGIN";
    public static final String CONNECT = "com.example.chat2023.CONNECT";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;


    @Override
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        serviceLooper = handlerThread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;

        if(intent.getAction().equals(CONNECT))
            msg.what = ServiceHandler.OPEN;
        if(intent.getAction().equals(LOGIN)) {
            msg.what = ServiceHandler.SEND;
            msg.setData(intent.getExtras());
        }

        System.out.println("msg: " + msg);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


}