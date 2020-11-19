package com.example.transportapp.Services;

import androidx.annotation.NonNull;

import com.example.transportapp.CommonDriver;
import com.example.transportapp.Model.EventBus.DriverRequestReceived;
import com.example.transportapp.Utils.UserUtilsDriver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingServiceDriverD extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            UserUtilsDriver.updateToken(this,s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> dataRecv = remoteMessage.getData();
        if(dataRecv != null)
        {
            if(dataRecv.get(CommonDriver.NOTI_TITLE).equals(CommonDriver.REQUEST_DRIVER_TITLE))
            {
                DriverRequestReceived driverRequestReceived = new DriverRequestReceived();
                driverRequestReceived.setKey(dataRecv.get(CommonDriver.RIDER_KEY));
                driverRequestReceived.setPickupLocation(dataRecv.get(CommonDriver.RIDER_PICKUP_LOCATION));
                driverRequestReceived.setPickupLocationString(dataRecv.get(CommonDriver.RIDER_PICKUP_LOCATION_STRING));
                driverRequestReceived.setDestinationLocation(dataRecv.get(CommonDriver.RIDER_DESTINATION));
                driverRequestReceived.setDestinationLocationString(dataRecv.get(CommonDriver.RIDER_DESTINATION_STRING));


                EventBus.getDefault().postSticky(driverRequestReceived);
            }
            else {
                CommonDriver.showNotification(this, new Random().nextInt(),
                        dataRecv.get(CommonDriver.NOTI_TITLE),
                        dataRecv.get(CommonDriver.NOTI_CONTENT),
                        null);
            }
        }
    }
}