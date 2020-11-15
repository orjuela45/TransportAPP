package com.example.transportapp.providers;


import com.example.transportapp.models.DriverInformation;
import com.example.transportapp.models.User;
import com.example.transportapp.models.UserFavRoutes;
import com.example.transportapp.models.UserInformation;
import com.example.transportapp.models.UserRoles;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.UUID;

public class UserProvider {

    public DatabaseReference databaseReference;

    public UserProvider() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public User createUser(String email, String password, String id, String token) {
        try {
            User userRegister = new User();
            userRegister.setId(id);
            userRegister.setEmail(email);
            userRegister.setPassword(password);
            userRegister.setStatusID(1);
            userRegister.setCreatedAt();
            userRegister.setToken(token);
            databaseReference.child(userRegister.getId()).setValue(userRegister);
            if (registerUSerRolTraveler(userRegister)){
                return userRegister;
            }
            return null;
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

    boolean registerUserRolDriver(String userId) {
        try {
            UserRoles userRol = new UserRoles();
            userRol.setRol("Driver");
            userRol.setCreatedAt();
            userRol.setCurrent(false);
            databaseReference.child(userId).child("Rols").child(userRol.getRol()).setValue(userRol);
            return true;
        } catch (Exception error) {
            return false;
        }
    }

    public DriverInformation registerDriverInformation(DriverInformation di, String userId) {
        try {
            if (registerUserRolDriver(userId)) {
                DriverInformation driverInformation = new DriverInformation();
                driverInformation.setCarModel(di.getCarModel());
                driverInformation.setLicensePlate(di.getLicensePlate());
                driverInformation.setCarColour(di.getCarColour());
                driverInformation.setCountBank(di.getCountBank());
                driverInformation.setBank(di.getBank());
                driverInformation.setStatus("Pendiente");
                databaseReference.child(userId) .child("DriverInformation").setValue(driverInformation);
                return driverInformation;
            }
            return null;
        } catch (Exception error) {
            return null;
        }
    }


    public UserFavRoutes createFavUser(String name_direction, String direction, String id) {
        try {
            UserFavRoutes userFavRoutes = new UserFavRoutes();
          //  userFavRoutes.setId(id);
            userFavRoutes.setId(UUID.randomUUID().toString());
            userFavRoutes.setName_direction(name_direction);
            userFavRoutes.setDirection(direction);
            databaseReference.child(id).child("FavPlaces").child(userFavRoutes.getId()).setValue(userFavRoutes);

            return userFavRoutes;
        } catch (Exception error){
            return null;
        }
    }
}
