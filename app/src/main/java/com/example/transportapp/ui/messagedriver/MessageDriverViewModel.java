package com.example.transportapp.ui.messagedriver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessageDriverViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MessageDriverViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Mensajes");
    }

    public LiveData<String> getText() {
        return mText;
    }
}