package com.example.transportapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transportapp.R;
import com.example.transportapp.models.User;
import com.example.transportapp.models.UserInformation;
import com.example.transportapp.providers.UserProvider;

import dmax.dialog.SpotsDialog;

public class DataUpdate extends AppCompatActivity {

    Button btnUpdateInformation;
    TextView getTxtEmailUser, txtNombre, txtApellido, txtIdentifiacion, txtTelefono, txtFechaNacimiento;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);
        getTxtEmailUser = (TextView) findViewById(R.id.getEmail);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        txtApellido = (TextView) findViewById(R.id.txtApellido);
        txtIdentifiacion = (TextView) findViewById(R.id.txtNumeroIdentificacion);
        txtTelefono = (TextView) findViewById(R.id.txtCelular);
        txtFechaNacimiento = (TextView) findViewById(R.id.txtFechaNacimiento);
        btnUpdateInformation = (Button) findViewById(R.id.btnUpdate);
        Intent dataUpdateIn = getIntent();
        User userGet = (User) dataUpdateIn.getSerializableExtra("userObj");
        if (userGet != null) {
            getTxtEmailUser.setText(userGet.getEmail().toString());
        }

        btnUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent dataUpdateIn = getIntent();
                    User userGet = (User) dataUpdateIn.getSerializableExtra("userObj");
                    boolean updateOK = UpdateInformation(userGet);
                    if(updateOK){
                            // Aqui se envia al menu principal
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean UpdateInformation(User userGet) throws InterruptedException {

        if (txtNombre.getText().toString().isEmpty()) {
            Toast.makeText(this, "El campo de nombre es requerido!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtApellido.getText().toString().isEmpty()) {
            Toast.makeText(this, "El campo de apellido es requerido!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtIdentifiacion.getText().toString().isEmpty()) {
            Toast.makeText(this, "El numero de identifiacion es requerido!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtTelefono.getText().toString().isEmpty()) {
            Toast.makeText(this, "El campo de telefono es requerido!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtFechaNacimiento.getText().toString().isEmpty()) {
            Toast.makeText(this, "La fecha de nacimiento es requerida!", Toast.LENGTH_SHORT).show();
            return false;
        }
        mDialog = new SpotsDialog.Builder().setContext(DataUpdate.this).setMessage("Registrando...").build();
        mDialog.show();
        UserProvider prov = new UserProvider();
        UserInformation userInformation = new UserInformation(
                txtNombre.getText().toString(),
                txtApellido.getText().toString(),
                txtIdentifiacion.getText().toString(),
                txtTelefono.getText().toString(),
                txtFechaNacimiento.getText().toString()
                );
        boolean result = prov.registerInformationUser(userInformation, userGet.getId());
        if(!result){
            Toast.makeText(this, "Ocurrio un error al intentar actualizar la informacion del usuario!", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(this, "Se actualizo correctamente la informacion del usuario!", Toast.LENGTH_SHORT).show();
        return true;
    }
}