package com.example.juiceup;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryLevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    void sendNotification(Context context) {
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Juice up at the nearest socket!")
                .setContentText("Find nearby sockets to charge up your phone")
                .build();
    }
}
