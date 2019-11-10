package com.example.juiceup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class BatteryLevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    void sendNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "All")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title1")
                .setContentText("Title2")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel(context);
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "All";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("All", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
