package com.example.transportapp.providers;

import androidx.annotation.NonNull;

import com.example.transportapp.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class UserProvider {

    public DatabaseReference databaseReference;
    Boolean resultValidateEmai;

    public UserProvider() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public Task<Void> create(User user) {
        User userRegister = new User();
        userRegister.setId(UUID.randomUUID().toString());
        userRegister.setEmail(user.getEmail());
        userRegister.setPassword(user.getPassword());
        userRegister.setStatusID(user.getStatusID());
        userRegister.setCreatedAt();
        return databaseReference.child(userRegister.getId()).setValue(userRegister);
    }

    public boolean validateEmail(final String email) {
        resultValidateEmai = false;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query q = databaseReference.orderByChild("email").equalTo(email).limitToFirst(1);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    resultValidateEmai = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return resultValidateEmai;
    }
}
