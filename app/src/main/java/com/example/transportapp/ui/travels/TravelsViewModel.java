package com.example.transportapp.ui.travels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TravelsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TravelsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is travels fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}