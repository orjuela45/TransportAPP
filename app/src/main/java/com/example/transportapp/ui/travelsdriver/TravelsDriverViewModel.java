package com.example.transportapp.ui.travelsdriver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TravelsDriverViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TravelsDriverViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Historial de viajes");
    }

    public LiveData<String> getText() {
        return mText;
    }
}