package com.example.transportapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transportapp.R;
import com.example.transportapp.models.DriverRequest;
import com.example.transportapp.models.User;
import com.example.transportapp.providers.DriverRequestProvider;
import com.example.transportapp.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerBank, spinnerModels;

    private Button licencia, tecnico, soat, propiedad, frontal, trasera, derecha, izquierda, enviar, salir;
    private EditText placa, color, cuenta;
    private String banco, userID;
    private Number modelo;
    private StorageReference storageReference;
    private Uri uriLicencia, uriTecnico, uriSOAT, uriPropiedad, uriFrontal, uriTrasera, uriDerecha, uriIzquierda;
    private FirebaseAuth mAuth;
    private AlertDialog mDialog;
    private DatabaseReference databaseReference;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DriverRequest");
        userID = mAuth.getCurrentUser().getUid();
        spinnerBank = findViewById(R.id.spinnerBanks);
        ArrayAdapter<CharSequence> adapterBanks = ArrayAdapter.createFromResource(this, R.array.banks, android.R.layout.simple_spinner_item);
        adapterBanks.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(adapterBanks);
        spinnerBank.setOnItemSelectedListener(this);
        spinnerModels = findViewById(R.id.spinnerModels);
        ArrayAdapter<CharSequence> adapterModels = ArrayAdapter.createFromResource(this, R.array.models, android.R.layout.simple_spinner_item);
        adapterModels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModels.setAdapter(adapterModels);
        spinnerModels.setOnItemSelectedListener(this);
        licencia = findViewById(R.id.btnLicencia);
        tecnico = findViewById(R.id.btnTecnico);
        soat = findViewById(R.id.btnSOAT);
        propiedad = findViewById(R.id.btnPropiedad);
        frontal = findViewById(R.id.btnFrontal);
        trasera = findViewById(R.id.btnTrasera);
        derecha = findViewById(R.id.btnDerecha);
        izquierda = findViewById(R.id.btnIzquierda);
        enviar = findViewById(R.id.btnEnviarSolicitud);
        cuenta = findViewById(R.id.countBank);
        placa = findViewById(R.id.placa);
        color = findViewById(R.id.colorCar);
        salir = findViewById(R.id.exitRegisterDriver);
        modelo = 0;
        //Eventos botones
        licencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        soat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });
        propiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
        });
        frontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 5);
            }
        });
        trasera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 6);
            }
        });
        derecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 7);
            }
        });
        izquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 8);
            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerRequest();
            }
        });
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterDriverActivity.this, MenuActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        try {
            modelo = Integer.parseInt(text);
        } catch (Exception err){
            banco = text;
        }
        Number id = view.getId();
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#80FFFFFF"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            switch (requestCode){
                case 1:
                    uriLicencia = data.getData();
                    break;
                case 2:
                    uriTecnico = data.getData();
                    break;
                case 3:
                    uriSOAT = data.getData();
                    break;
                case 4:
                    uriPropiedad = data.getData();
                    break;
                case 5:
                    uriFrontal = data.getData();
                    break;
                case 6:
                    uriTrasera = data.getData();
                    break;
                case 7:
                    uriDerecha = data.getData();
                    break;
                case 8:
                    uriIzquierda = data.getData();
                    break;
            }
        }

    }

    public boolean registerRequest(){
        if(uriLicencia == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto de licencia es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriTecnico == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto de técnico mecánica es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriSOAT == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto de SOAT es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriPropiedad == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto de propiedad es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriFrontal == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto frontal del auto es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriTrasera == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto trasera del auto es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriDerecha == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto del lado derecho del auto es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(uriIzquierda == null){
            Toast.makeText(RegisterDriverActivity.this, "La foto del lado izquierdo del auto es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cuenta.getText().toString().isEmpty()){
            Toast.makeText(RegisterDriverActivity.this, "La cuenta es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (banco.isEmpty()){
            Toast.makeText(RegisterDriverActivity.this, "El banco es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (modelo.intValue() == 0){
            Toast.makeText(RegisterDriverActivity.this, "El modelo del auto es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (color.getText().toString().isEmpty()){
            Toast.makeText(RegisterDriverActivity.this, "El color del auto es obligatorio es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        mDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage("Generando solicitud...").build();
        mDialog.show();
        Query q = databaseReference.orderByChild("driverId").equalTo(userID);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterDriverActivity.this, "Ya tienes una solicitud pendiente", Toast.LENGTH_SHORT).show();
                } else {
                    if (!uploadImages()){
                        Toast.makeText(RegisterDriverActivity.this, "Hubo un error al guardar las imagenes", Toast.LENGTH_SHORT).show();
                        mDialog.hide();
                    } else {
                        DriverRequestProvider dr = new DriverRequestProvider();
                        if (dr.createRequest(userID) == null){
                            Toast.makeText(RegisterDriverActivity.this, "No se pudo registrar la solicitud", Toast.LENGTH_SHORT).show();
                            mDialog.hide();
                        } else{
                            Toast.makeText(RegisterDriverActivity.this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterDriverActivity.this, MenuActivity.class));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mDialog.hide();

        return false;
    }

    public boolean uploadImages() {
        try {
            StorageReference filepathLicencia = storageReference.child("Uploads").child(userID).child("licencia");
            StorageReference filepathTecnico = storageReference.child("Uploads").child(userID).child("tecnico");
            StorageReference filepathSOAT = storageReference.child("Uploads").child(userID).child("SOAT");
            StorageReference filepathPropiedad = storageReference.child("Uploads").child(userID).child("propiedad");
            StorageReference filepathFrontal = storageReference.child("Uploads").child(userID).child("frontal");
            StorageReference filepathTrasera = storageReference.child("Uploads").child(userID).child("trasera");
            StorageReference filepathDerecha = storageReference.child("Uploads").child(userID).child("derecha");
            StorageReference filepathIzquierda = storageReference.child("Uploads").child(userID).child("izquierda");
            filepathLicencia.putFile(uriLicencia);
            filepathTecnico.putFile(uriTecnico);
            filepathSOAT.putFile(uriSOAT);
            filepathPropiedad.putFile(uriPropiedad);
            filepathFrontal.putFile(uriFrontal);
            filepathTrasera.putFile(uriTrasera);
            filepathDerecha.putFile(uriDerecha);
            filepathIzquierda.putFile(uriIzquierda);
            return true;
        } catch (Error error){
            return false;
        }
    }
}