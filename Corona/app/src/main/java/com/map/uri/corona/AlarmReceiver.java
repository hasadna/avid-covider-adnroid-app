package com.map.uri.corona;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            NotificationManager manager = null;
            NotificationCompat.Builder notificationBuilder;
            final String NOTIFICATION_CHANNEL_ID = "05";
            int NOTIFICATION_ID = 100;

            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "corona Service";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
                manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(chan);
            }

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dfDateTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String s1 = context.getResources().getString(R.string.notification_message1);
            String s2 = context.getResources().getString(R.string.notification_message2);
            String s = dfDateTime.format(c.getTimeInMillis()) + "  " + s1 + "\n" + s2;

            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            contentView.setTextViewText(R.id.tv, s);
            contentView.setImageViewResource(R.id.iv, R.drawable.corona);

            notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.corona)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContent(contentView)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000});

            manager.notify(NOTIFICATION_ID, notificationBuilder.build());

        } else {//intent.getAction()=="android.intent.action.BOOT_COMPLETED"

            SharedPreferences sharedPref = context.getSharedPreferences("corona",
                    Context.MODE_PRIVATE);
            int hourOfDay = sharedPref.getInt("hourOfDay", 0);
            int minute = sharedPref.getInt("minute", 0);
            long interval = sharedPref.getLong("interval", 0);

            if (interval != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                // notification time
                long when = calendar.getTimeInMillis();

                /* Setting the alarm here */
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intentAlarm = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC, when, interval, pendingIntent);

                String s = context.getResources().getString(R.string.toast_notification_created);
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

    }
}