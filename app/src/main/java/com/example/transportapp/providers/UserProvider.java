package com.example.transportapp.providers;


import com.example.transportapp.models.User;
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

    public User createUser(String email, String password) {
        try {
            User userRegister = new User();
            userRegister.setId(UUID.randomUUID().toString());
            userRegister.setEmail(email);
            userRegister.setPassword(password);
            userRegister.setStatusID(1);
            userRegister.setCreatedAt();
            databaseReference.child(userRegister.getId()).setValue(userRegister);
            if (registerUSerRolViajero(userRegister)){
                return userRegister;
            }
            return null;
        } catch (Exception error){
            return null;
        }
    }

    boolean registerUSerRolViajero(User user) {
        try {
            UserRoles userRol = new UserRoles();
            userRol.setRol("Viajero");
            userRol.setCreatedAt();
            userRol.setCurrent(true);
            databaseReference.child(user.getId()).child("Roles").child(userRol.getRol()).setValue(userRol);
            return true;
        } catch (Exception error) {
            return false;
        }
    }

   public boolean registerInformationUser(UserInformation userInf , String userId) {
        try {
            databaseReference.child(userId).child("InformacionUsuario").setValue(userInf);
            return true;
        } catch (Exception error) {
            return false;
        }
    }

}
