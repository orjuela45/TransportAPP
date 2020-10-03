package com.example.transportapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.transportapp.R;
import com.example.transportapp.models.User;

public class DataUpdate extends AppCompatActivity {

    TextView getTxtEmailUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);
        getTxtEmailUser = (TextView) findViewById(R.id.txtNombre);

        Intent dataUpdateIn = getIntent();
        User user = (User)dataUpdateIn.getSerializableExtra("userObj");
/*        if (bundle != null) {
            User user = (User) bundle.get("userId");
            getTxtEmailUser.setText("Hola de nuevo usuario " + bundle.get("email").toString());
        } */
    }
}