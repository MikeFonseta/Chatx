package com.example.chat2023.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chat2023.util.ConnectionRepository;

public class SigningViewModel extends AndroidViewModel {
    private ConnectionRepository connectionRepository;
    private LiveData<String> response;


    public SigningViewModel(Application application) {
        super(application);
        connectionRepository = new ConnectionRepository();
        response = connectionRepository.getResponse();
    }

    public LiveData<String> getResponse() {
        return connectionRepository.getResponse();
    }
}
