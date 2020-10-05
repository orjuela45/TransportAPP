package com.example.transportapp.ui.travels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.transportapp.R;

public class TravelsFragment extends Fragment {

    private TravelsViewModel travelsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        travelsViewModel =
                ViewModelProviders.of(this).get(TravelsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_travels, container, false);
        final TextView textView = root.findViewById(R.id.text_travels);
        travelsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}