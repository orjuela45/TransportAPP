package com.example.transportapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.transportapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private Button perfil;
    private DatabaseReference databaseReferenceUser, databaseReferenceRequest;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        databaseReferenceRequest = FirebaseDatabase.getInstance().getReference().child("DriverRequest");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu_user ID as a set of Ids because each
        // menu_user should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_travels, R.id.nav_message)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);
        perfil = headView.findViewById(R.id.dataupdate);
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, DataUpdate.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_user; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnSignOut){
            mAuth.signOut();
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
        }
        if (item.getItemId() == R.id.btnRegisterDriver){
            Query q = databaseReferenceUser;
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("DriverInformation").exists()){
                        Query q2 = databaseReferenceRequest.orderByChild("driverId").equalTo(mAuth.getCurrentUser().getUid());
                        q2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                if (snapshot2.exists()){
                                    for (DataSnapshot dataSnapshot: snapshot2.getChildren()){
                                        String status = dataSnapshot.child("status").getValue().toString();
                                        if (status.equals("Aceptado")){
                                            Toast.makeText(MenuActivity.this, "Ya eres un conductor activo", Toast.LENGTH_SHORT).show();
                                        } else if (status.equals("Pendiente")){
                                            Toast.makeText(MenuActivity.this, "Ya tienes una solicitud pendiente", Toast.LENGTH_SHORT).show();
                                        } else if (status.equals("Rechazado")){
                                            Toast.makeText(MenuActivity.this, "Tu solicitud fue rechazada, debes esperar 3 meses para volver a enviarla", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else if (snapshot.exists() && snapshot.child("UserInformation").exists()){
                        if (snapshot.child("UserInformation").child("birthDate").getValue().toString().isEmpty() ||
                            snapshot.child("UserInformation").child("identification").getValue().toString().isEmpty() ||
                            snapshot.child("UserInformation").child("lastName").getValue().toString().isEmpty() ||
                            snapshot.child("UserInformation").child("name").getValue().toString().isEmpty() ||
                            snapshot.child("UserInformation").child("phone").getValue().toString().isEmpty()){
                            Toast.makeText(MenuActivity.this, "Falta información por diligenciar de tu perfil", Toast.LENGTH_SHORT).show();
                        } else{
                            startActivity(new Intent(MenuActivity.this, RegisterDriverActivity.class));
                        }
                    } else{
                        Toast.makeText(MenuActivity.this, "No has registrado la infromación del usuario", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        return false;
    }
}