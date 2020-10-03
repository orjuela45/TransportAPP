package com.example.transportapp.activities;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.transportapp.R;
import com.example.transportapp.models.User;
import com.example.transportapp.providers.UserProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterUserActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button btnRegister;
    EditText txtEmail, txtPassword;
    AlertDialog mDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        //Campos
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.password);
        //Accion Registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    registerUser();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Boolean registerUser() throws InterruptedException {
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Debes ingresar un correo electronico", Toast.LENGTH_SHORT).show();
            return false;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (!pattern.matcher(email).matches()) {
            Toast.makeText(this, "Correo electronico no valido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Debes ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe ser mayor a 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        mDialog = new SpotsDialog.Builder().setContext(RegisterUserActivity.this).setMessage("Registrando").build();
        mDialog.show();
        Query q = databaseReference.orderByChild("Roles").equalTo("Viajero");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot = snapshot;
                if (snapshot.exists()) {
                    Toast.makeText(RegisterUserActivity.this, "El correo ingresado ya se encuentra registrado.", Toast.LENGTH_SHORT).show();
                } else {
                    UserProvider prov = new UserProvider();
                    User userOk = prov.createUser(email, password);
                    if (userOk != null) {
                        String userId = userOk.getId();
                        String email = userOk.getEmail();
                        Log.d(TAG, "Usuario registrado exitosamente:success");
                        Intent dataUpdateIn = new Intent(RegisterUserActivity.this, DataUpdate.class);
                        dataUpdateIn.putExtra("userObj", userOk);
                        dataUpdateIn.putExtra("email", email);
                        startActivity(dataUpdateIn);
                        Toast.makeText(RegisterUserActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Error al registrar:danger");
                        Toast.makeText(RegisterUserActivity.this, "Ocurrio un error al crear el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mDialog.hide();
        return true;
    }
}