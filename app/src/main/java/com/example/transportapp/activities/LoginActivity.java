package com.example.transportapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.transportapp.R;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    EditText mTextInputEmail, mTextInputPassword;
    Button mButtonLogin;
    Button mButtonGoToRegister;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String userId;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);
        mButtonGoToRegister = findViewById(R.id.btnRegister);

        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               login(); 
            }
        });
        mButtonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()) {
            if(password.length() >= 6) {
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()){
                            userId = mAuth.getCurrentUser().getUid();
                            redirectMenu();
                        } else {
                            Toast.makeText(LoginActivity.this, "El correo electronico o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "La contraseña debe tener más de 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "El correo o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
    private void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            userId = mAuth.getCurrentUser().getUid();
            redirectMenu();
        }
    }

    public void redirectMenu(){
        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Iniciando...").build();
        mDialog.show();
        Query q = databaseReference.child(userId).child("Rols");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if (dataSnapshot.child("current").getValue().toString() == "true"){
                            switch (dataSnapshot.child("rol").getValue().toString()){
                                case "Traveler":
                                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                                    break;
                                case "Manager":
                                    startActivity(new Intent(LoginActivity.this, MenuAdminActivity.class));
                                    break;
                                case "Driver":
                                    startActivity(new Intent(LoginActivity.this, MenuDriverActivity.class));
                                    break;
                            }
                            mDialog.hide();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}