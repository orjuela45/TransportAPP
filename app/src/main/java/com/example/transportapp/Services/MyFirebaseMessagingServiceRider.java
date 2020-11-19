package com.example.transportapp.Services;

import androidx.annotation.NonNull;

import com.example.transportapp.CommonRider;
import com.example.transportapp.Model.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.example.transportapp.Model.EventBus.DeclineRequestFromDriver;
import com.example.transportapp.Model.EventBus.DriverAcceptTripEvent;
import com.example.transportapp.Model.EventBus.DriverCompleteTripEvent;
import com.example.transportapp.Utils.UserUtilsRider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingServiceRider extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            UserUtilsRider.updateToken(this, s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null) {
            if (dataRecv.get(CommonRider.NOTI_TITLE) != null) {

                if (dataRecv.get(CommonRider.NOTI_TITLE).equals(CommonRider.REQUEST_DRIVER_DECLINE)) {
                    EventBus.getDefault().postSticky(new DeclineRequestFromDriver());
                }

                else if (dataRecv.get(CommonRider.NOTI_TITLE).equals(CommonRider.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP)) {
                    EventBus.getDefault().postSticky(new DeclineRequestAndRemoveTripFromDriver());
                }

                else if (dataRecv.get(CommonRider.NOTI_TITLE).equals(CommonRider.REQUEST_DRIVER_ACCEPT)) {
                    String tripKey = dataRecv.get(CommonRider.TRIP_KEY);
                    EventBus.getDefault().postSticky(new DriverAcceptTripEvent(tripKey));
                }
                else if (dataRecv.get(CommonRider.NOTI_TITLE).equals(CommonRider.RIDER_COMPLETE_TRIP)) {
                    String tripKey = dataRecv.get(CommonRider.TRIP_KEY);
                    EventBus.getDefault().postSticky(new DriverCompleteTripEvent(tripKey));
                }
                else
                    CommonRider.showNotification(this, new Random().nextInt(),
                            dataRecv.get(CommonRider.NOTI_TITLE),
                            dataRecv.get(CommonRider.NOTI_CONTENT),
                            null);
            }
        }
    }
}
