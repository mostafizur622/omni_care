package com.srapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.srapp.LoginActivity;
import com.srapp.R;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // notification payload (title/body)
        RemoteMessage.Notification n = remoteMessage.getNotification();
        String title = null, body = null;

        if (n != null) {
            title = n.getTitle();
            body  = n.getBody();
        }

        // data payload fallback
        if (title == null) title = remoteMessage.getData().get("title");
        if (body  == null) body  = remoteMessage.getData().get("body");

        Log.d("JsonFromServer", "Data: " + remoteMessage.getData() + " | Notif: " + (n != null));

        showNotification(title != null ? title : "Notification",
                body  != null ? body  : "",
                remoteMessage);
    }

    private void showNotification(String title, String body,RemoteMessage remoteMessage) {
        // Notification channel (for Android 8.0 and above)
        String channelId = "MyChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.field_force)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title) // expanded title
                        .bigText(body)) // expanded large text
/*                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(null) // big image when expanded
                        .bigLargeIcon(BitmapFactory. decodeResource (getApplicationContext().getResources() , R.drawable. alart_icon )) // remove large icon from expanded header
                        .setSummaryText(null)) // long text summary*/
                .setAutoCancel(true);

        // Intent for when notification is tapped
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    this,
                    0, intent,
                    PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        builder.setContentIntent(pendingIntent);
        int notificationId = (int) System.currentTimeMillis(); // unique ID
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();

            // Extract image URL from data payload
            String imageUrl = data.get("image_url");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Load the image using Picasso
              //  loadImage(imageUrl,builder);
                Log.e("ImageUrl",imageUrl);
            }

            // Handle other data payload elements as needed
        }
    }
}