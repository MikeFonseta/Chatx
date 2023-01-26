package com.example.chat2023.ui.utentiInAttesa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UtentiInAttesaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UtentiInAttesaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}