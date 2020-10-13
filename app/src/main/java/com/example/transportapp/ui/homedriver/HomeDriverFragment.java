package com.example.transportapp.ui.homedriver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.transportapp.R;
import com.example.transportapp.activities.Common;
import com.example.transportapp.activities.DetailRequestActivity;
import com.example.transportapp.activities.MenuAdminActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeDriverFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private HomeDriverViewModel homeViewModel;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Spinner spinnerStatusDriver;
    private String statusDriver;
    private FirebaseAuth mAuth;

    SupportMapFragment mapFragment;
    private  boolean isFirstTime;
    private  boolean changeStatus;

    //Online system
    DatabaseReference onlineRef, currentUserRef, driversLocationRef, driverInfromationRef;
    GeoFire geofire;
    ValueEventListener onlinevalueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists() && currentUserRef != null)
            {
                currentUserRef.onDisconnect().removeValue();
                isFirstTime = true;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                    .show();

        }
    };

    @Override
    public void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        geofire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.removeEventListener(onlinevalueEventListener);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerOnlineSystem();
    }

    private void registerOnlineSystem() {
        onlineRef.addValueEventListener(onlinevalueEventListener);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeDriverViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_driver, container, false);
        changeStatus = false;
        spinnerStatusDriver = root.findViewById(R.id.spinnerStatusDriver);
        ArrayAdapter<CharSequence> adapterBanks = ArrayAdapter.createFromResource(getContext(), R.array.statusDriver, android.R.layout.simple_spinner_item);
        adapterBanks.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusDriver.setAdapter(adapterBanks);
        spinnerStatusDriver.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        driverInfromationRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        init();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#80FFFFFF"));
        statusDriver = text;
        Map<String, Object> driverStatus =  new HashMap<>();
        driverStatus.put("statusID", statusDriver);
        driverInfromationRef.updateChildren(driverStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (!changeStatus){
                    Toast.makeText(getContext(), "Estas libre, buen dia.", Toast.LENGTH_SHORT).show();
                    changeStatus = true;
                } else {
                    Toast.makeText(getContext(), "Cambio de estado exitoso", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Ocurrio un error al cambiar el estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void init() {
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(), getString(R.string.permission_requiere), Snackbar.LENGTH_SHORT).show();
            return;
        }
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(50f); //50m
        locationRequest.setInterval(15000); //15 sec
        locationRequest.setFastestInterval(10000); // 10 sec
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 18f));


                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude(), 1);
                    String cityName = addressList.get(0).getLocality();


                    driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERENCES)
                            .child(cityName);
                    currentUserRef = driversLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    geofire = new GeoFire(driversLocationRef);

                    //Update Location
                    geofire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            new GeoLocation(locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude()),
                            (key, error) -> {
                                if (error != null)
                                    Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                else
                                {
                                    if(isFirstTime)
                                    {
                                        Snackbar.make(mapFragment.getView(), "¡Conectado!", Snackbar.LENGTH_LONG)
                                                .show();
                                        isFirstTime=false;
                                    }

                                }
                            });
                    registerOnlineSystem();
                } catch (IOException e) {
                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }



            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(), getString(R.string.permission_requiere), Snackbar.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check permission
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(getView(), getString(R.string.permission_requiere), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(() -> {
                            fusedLocationProviderClient.getLastLocation()
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                    .addOnSuccessListener(location -> {
                                        LatLng userLatLing = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLing, 18));
                                    });
                            return true;
                        });

                        // Set layout button
                        View locationButton = ((View)mapFragment.getView().findViewById(Integer.parseInt("1"))
                                .getParent())
                                .findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

                        // Right bottom
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                        params.setMargins(0,0,0,50);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "Permission"+permissionDeniedResponse.getPermissionName()+""+
                                " was danied!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.transport_maps_style));
            if (!success)
                Log.e("EDMT_ERROR", "Style parsing error");
        }catch (Resources.NotFoundException e){
            Log.e("EDMT_ERROR",e.getMessage());
        }
    }
}