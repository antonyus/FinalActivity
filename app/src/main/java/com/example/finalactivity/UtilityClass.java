package com.example.finalactivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;


import androidx.core.app.NotificationCompat;


public class UtilityClass {
    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "channel0";
    Context context;
    NotificationManager notificationManager;

    public UtilityClass(Context context) {
        this.context = context;
    }


    /*Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel.
    For each channel, you can set the visual and auditory behavior that is applied to all notifications in that channel.
    Then, users can change these settings and decide which notification channels from your app should be intrusive or visible at all.*/
    //createNotificationChannel
    //sends individual notifications for each activity
    protected void createNotificationChannel(Class c, String profPicUrl, String firstAndLastName, String username,  String email, String phone) {

        //notifications work on API 26 and higher

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel0", importance);
            channel.setDescription("Notification");
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

       /* An Intent provides a facility for performing late runtime binding between the code in different applications.
       Its most significant use is in the launching of activities, where it can be thought of as the glue between activities.
       It is basically a passive data structure holding an abstract description of an action to be performed.*/

        Intent intent = new Intent(context, c);
        intent.putExtra("profilePic", profPicUrl);
        intent.putExtra("firstAndLastName", firstAndLastName);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Notification")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .build();


        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    protected void createNotificationChannel(Class c) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel0", importance);
            channel.setDescription("Notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, c);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Notification")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .build();


        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    public void onDestroyControl(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("isDestroyedCalled", true);
        editor.apply();
    }


}
