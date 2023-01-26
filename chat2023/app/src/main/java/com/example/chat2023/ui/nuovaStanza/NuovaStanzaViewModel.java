package com.example.chat2023.ui.nuovaStanza;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NuovaStanzaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NuovaStanzaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}