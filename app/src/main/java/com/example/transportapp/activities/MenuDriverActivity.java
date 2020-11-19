package com.example.transportapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.transportapp.CommonDriver;
import com.example.transportapp.R;
import com.example.transportapp.Utils.UserUtilsDriver;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class MenuDriverActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 7172;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;

    private AlertDialog waitingDialog;
    private StorageReference storageReference;

    private Uri imageUri;
    private ImageView img_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu_user ID as a set of Ids because each
        // menu_user should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        init();
    }

        private void init () {
            waitingDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("Waiting...")
                    .create();

            storageReference = FirebaseStorage.getInstance().getReference();

            navigationView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_sign_out_driver) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuDriverActivity.this);
                    builder.setTitle("Cerrar sesión")
                            .setMessage("¿De verdad quieres salir?")
                            .setNegativeButton("CANCELAR", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("SALIR", (dialogInterface, i) -> {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MenuDriverActivity.this, LoginActivity.class);
                                startActivity(intent);
                            })
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialogInterface -> {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(R.color.colorAccent));

                    });

                    dialog.show();
                }
                return false;
            });

            //Set data for user
            View headerView = navigationView.getHeaderView(0);
            TextView txt_name = (TextView) headerView.findViewById(R.id.txt_name);
            //TextView txt_phone = (TextView)headerView.findViewById(R.id.txt_phone);
            TextView txt_star = (TextView) headerView.findViewById(R.id.txt_star);
            img_avatar = (ImageView) headerView.findViewById(R.id.img_avatar);

            txt_name.setText(CommonDriver.buildWelcomeMessage());
            //txt_phone.setText(CommonDriver.currentUser != null ? CommonDriver.currentUser.getPhoneNumber() : "");
            txt_star.setText(CommonDriver.currentUser != null ? String.valueOf(CommonDriver.currentUser.getRating()) : "0.0");

            img_avatar.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });

            if (CommonDriver.currentUser != null && CommonDriver.currentUser.getAvatar() != null &&
                    !TextUtils.isEmpty(CommonDriver.currentUser.getAvatar())) {
                Glide.with(this)
                        .load(CommonDriver.currentUser.getAvatar())
                        .into(img_avatar);
            }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            if(data != null && data.getData() != null)
            {
                imageUri = data.getData();
                img_avatar.setImageURI(imageUri);

                showDialogUpload();

            }
        }
    }

    private void showDialogUpload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuDriverActivity.this);
        builder.setTitle("Change avatar")
                .setMessage("Do you really want to change avatar?")
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("UPLOAD", (dialogInterface, i) -> {
                    if(imageUri != null)
                    {
                        waitingDialog.setMessage("Uploading...");
                        waitingDialog.show();

                        String unique_name = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        StorageReference avatarFolder = storageReference.child("avatars/"+unique_name);

                        avatarFolder.putFile(imageUri)
                                .addOnFailureListener(e -> {
                                    waitingDialog.dismiss();
                                    Snackbar.make(drawer,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                avatarFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Map<String,Object> updateData = new HashMap<>();
                                    updateData.put("avatar",uri.toString());

                                    UserUtilsDriver.updateUser(drawer,updateData);
                                });
                            }
                            waitingDialog.dismiss();
                        }).addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            waitingDialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                        });
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.colorAccent));
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_user; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}