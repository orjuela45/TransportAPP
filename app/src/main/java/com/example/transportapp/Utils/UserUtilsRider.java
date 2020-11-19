package com.example.transportapp.Utils;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.transportapp.CommonRider;
import com.example.transportapp.Model.DriverGeoModel;
import com.example.transportapp.Model.EventBus.SelectePlaceEvent;
import com.example.transportapp.Model.FCMSendData;
import com.example.transportapp.Model.TokenModel;
import com.example.transportapp.R;
import com.example.transportapp.Remote.IFCMService;
import com.example.transportapp.Remote.RetrofitFCMClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserUtilsRider {
    public static void updateUser(View view, Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(CommonRider.RIDER_INFO_REFENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Snackbar.make(view,"Update information successfully!",Snackbar.LENGTH_SHORT).show());
    }

    public static void updateToken(Context context, String token) {
        TokenModel tokenModel = new TokenModel(token);

        FirebaseDatabase.getInstance()
                .getReference(CommonRider.TOKEN_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(aVoid -> {

        });
    }

    public static void sendRequestToDriver(Context context, RelativeLayout main_layout, DriverGeoModel foundDriver, SelectePlaceEvent selectePlaceEvent) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //Get token
        FirebaseDatabase
                .getInstance()
                .getReference(CommonRider.TOKEN_REFERENCE)
                .child(foundDriver.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            TokenModel tokenModel = dataSnapshot.getValue(TokenModel.class);

                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(CommonRider.NOTI_TITLE, CommonRider.REQUEST_DRIVER_TITLE);
                            notificationData.put(CommonRider.NOTI_CONTENT,"This message respersent for request driver action");
                            notificationData.put(CommonRider.RIDER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            notificationData.put(CommonRider.RIDER_PICKUP_LOCATION_STRING,selectePlaceEvent.getOriginString());
                            notificationData.put(CommonRider.RIDER_PICKUP_LOCATION,new StringBuilder("")
                                    .append(selectePlaceEvent.getOrigin().latitude)
                                    .append(",")
                                    .append(selectePlaceEvent.getOrigin().longitude)
                                    .toString());

                            notificationData.put(CommonRider.RIDER_DESTINATION_STRING,selectePlaceEvent.getAddress());
                            notificationData.put(CommonRider.RIDER_DESTINATION,new StringBuilder("")
                                    .append(selectePlaceEvent.getDestination().latitude)
                                    .append(",")
                                    .append(selectePlaceEvent.getDestination().longitude)
                                    .toString());

                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if(fcmResponse.getSuccess() == 0)
                                        {
                                            compositeDisposable.clear();
                                            Snackbar.make(main_layout,context.getString(R.string.request_driver_failed),Snackbar.LENGTH_LONG).show();
                                        }

                                    }, throwable -> {
                                        compositeDisposable.clear();
                                        Snackbar.make(main_layout,throwable.getMessage(),Snackbar.LENGTH_LONG).show();
                                    }));
                        }
                        else
                        {
                            Snackbar.make(main_layout,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Snackbar.make(main_layout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();

                    }
                });
    }
}
