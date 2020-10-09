package com.example.transportapp.ui.homedriver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeDriverViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeDriverViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}