package com.example.transportapp.ui.travelsuser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TravelsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TravelsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Historial viajes de usuario");
    }

    public LiveData<String> getText() {
        return mText;
    }
}