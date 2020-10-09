package com.example.transportapp.ui.messagedriver;

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

public class MessageDriverFragment extends Fragment {

    private MessageDriverViewModel messagedriverViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messagedriverViewModel =
                ViewModelProviders.of(this).get(MessageDriverViewModel.class);
        View root = inflater.inflate(R.layout.fragment_message_driver, container, false);
        final TextView textView = root.findViewById(R.id.text_message_driver);
        messagedriverViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}