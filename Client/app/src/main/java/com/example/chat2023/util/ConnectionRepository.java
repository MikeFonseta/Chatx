package com.example.chat2023.util;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ConnectionRepository {

    public MutableLiveData<String> response = new MutableLiveData<>();
    private static ConnectionRepository instance;
//    private MutableLiveData<ConnectionService.ServiceBinder> mBinder = new MutableLiveData<>();

    public ConnectionRepository() {
    }

    public static synchronized ConnectionRepository getInstance() {
        if (instance == null)
            instance = new ConnectionRepository();
        return instance;
    }

    public LiveData<String> getResponse() {
        return response;
    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//
//        }
//    }
}
