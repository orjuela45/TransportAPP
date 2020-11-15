package com.example.transportapp.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.transportapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class Fcm extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        GlobalClass globalClass = (GlobalClass) getApplicationContext();
        globalClass.setToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        /*Log.e("tag", "mensaje recivido de : " + from);
        if (remoteMessage.getNotification() != null){
            Log.e("Tag","titulo" + remoteMessage.getNotification().getTitle());
            Log.e("Tag","cuerpo" + remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() >0){
            Log.e("Tag","titulo perzonalizado" + remoteMessage.getData().get("titulo"));
            Log.e("Tag","cuerpo perzonalizado" + remoteMessage.getData().get("cuerpo"));
        }*/
        if (remoteMessage.getData().size() >0){
            String titel = remoteMessage.getData().get("titel");
            String body = remoteMessage.getData().get("body");
            notification(titel, body);
        }
    }

    public void notification(String titel, String body){
        String id = "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id,"nuevo", NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            nm.createNotificationChannel(nc);
        }
        builder.setAutoCancel(true).setWhen(System.currentTimeMillis()).setContentTitle(titel)
                .setSmallIcon(R.mipmap.ic_launcher).setContentText(body).setContentInfo("Nuevo").setContentIntent(openNoti());
        Random random = new Random();
        int idNotify = random.nextInt(8000);
        nm.notify(idNotify, builder.build());
    }

    public PendingIntent openNoti(){
        Intent nf = new Intent(getApplicationContext(), MenuActivity.class);
        nf.putExtra("color", "azul");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return  PendingIntent.getActivity(this, 0, nf, 0);
    }
}
