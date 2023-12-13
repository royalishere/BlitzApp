package com.example.blitz;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
    @SuppressLint("NewApi")

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String channel_id = "MESSAGE";

        NotificationChannel channel = new NotificationChannel(
                channel_id,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Context context;
        Notification.Builder notification = new Notification.Builder(this, channel_id)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.chat_2_svgrepo_com)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());

        super.onMessageReceived(remoteMessage);
    }
}
