package com.example.medappv4;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");
        showNotification(context, medicineName);
        Log.d("MedAppV4", "Alarm received for medicine: " + medicineName);
    }

    private void showNotification(Context context, String medicineName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MEDICINE_CHANNEL")
                .setSmallIcon(R.mipmap.ic_launcher) // Replace with your icon
                .setContentTitle("Medicine Reminder")
                .setContentText("It's time to take " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        int permissionCheck = ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(100, builder.build());
        } else {
            Log.d("MedAppV4", "POST_NOTIFICATIONS permission is not granted.");
        }
    }
}
