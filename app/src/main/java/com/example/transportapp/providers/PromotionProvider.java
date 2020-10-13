package com.example.transportapp.providers;

import java.util.UUID;
import com.example.transportapp.models.DriverInformation;
import com.example.transportapp.models.DriverRequest;
import com.example.transportapp.models.Promotion;
import com.example.transportapp.models.User;
import com.example.transportapp.models.UserInformation;
import com.example.transportapp.models.UserRoles;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PromotionProvider {

    public DatabaseReference databaseReference;

    public PromotionProvider() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Promotion");
    }

    public boolean createPromotion(Promotion promotion) {
        try {
            String Id = UUID.randomUUID().toString();
            promotion.setId(Id);
            databaseReference.child(Id).setValue(promotion);
            return true;
        } catch (Exception error){
            return false;
        }
    }
}

