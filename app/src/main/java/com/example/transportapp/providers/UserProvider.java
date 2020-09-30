package com.example.transportapp.providers;

import com.example.transportapp.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class UserProvider {

    DatabaseReference mDatabase;

    public UserProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public Task<Void> create(User user) {
        User userRegister = new User();
        userRegister.setId(UUID.randomUUID().toString());
        userRegister.setEmail(user.getEmail());
        userRegister.setPassword(user.getPassword());
        userRegister.setStatusID(user.getStatusID());
        userRegister.setCurrentRole(user.getCurrentRole());
        userRegister.setCreatedAt();
        return mDatabase.child(userRegister.getId()).setValue(userRegister);
    }

}
