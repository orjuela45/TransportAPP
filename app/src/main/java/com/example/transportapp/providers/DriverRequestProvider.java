package com.example.transportapp.providers;


import com.example.transportapp.models.DriverRequest;
import com.example.transportapp.models.User;
import com.example.transportapp.models.UserInformation;
import com.example.transportapp.models.UserRoles;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class DriverRequestProvider {

    public DatabaseReference databaseReference;

    public DriverRequestProvider() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DriverRequest");
    }

    public DriverRequest createRequest(String driverId) {
        try {
            DriverRequest driverRequest =  new DriverRequest();
            driverRequest.setId(UUID.randomUUID().toString());
            driverRequest.setDriverId(driverId);
            driverRequest.setStatus("Pendiente");
            driverRequest.setCreatedAt();
            databaseReference.child(driverRequest.getId()).setValue(driverRequest);
            return driverRequest;
        } catch (Exception error){
            return null;
        }
    }

    boolean registerUSerRolTraveler(User user) {
        try {
            UserRoles userRol = new UserRoles();
            userRol.setRol("Traveler");
            userRol.setCreatedAt();
            userRol.setCurrent(true);
            databaseReference.child(user.getId()).child("Rols").child(userRol.getRol()).setValue(userRol);
            return true;
        } catch (Exception error) {
            return false;
        }
    }

   public boolean registerUserInformation(UserInformation userInf , String userId) {
        try {
            databaseReference.child(userId).child("UserInformation").setValue(userInf);
            return true;
        } catch (Exception error) {
            return false;
        }
    }

}
