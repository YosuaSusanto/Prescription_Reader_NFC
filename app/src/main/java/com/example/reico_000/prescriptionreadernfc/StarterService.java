package com.example.reico_000.prescriptionreadernfc;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class StarterService extends Service {
    private static final String TAG = "MyService";

    /**
     * The started service starts the AlarmManager.
     */
    @Override
    public void onStart(Intent intent, int startid) {
    scheduleAlarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    public void scheduleAlarm() {
        Intent i = new Intent(this, NotificationBarAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.HOUR_OF_DAY, 7);
        calendar.set(calendar.MINUTE, 0);
        calendar.set(calendar.SECOND, 0);
        calendar.set(calendar.MILLISECOND, 0);
        long sdl = calendar.getTimeInMillis();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar.HOUR_OF_DAY, 12);
        calendar2.set(calendar.MINUTE, 0);
        calendar2.set(calendar.SECOND, 0);
        calendar2.set(calendar.MILLISECOND, 0);
        long sdl2 = calendar2.getTimeInMillis();

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(calendar.HOUR_OF_DAY, 17);
        calendar3.set(calendar.MINUTE, 0);
        calendar3.set(calendar.SECOND, 0);
        calendar3.set(calendar.MILLISECOND, 0);
        long sdl3 = calendar3.getTimeInMillis();

        Calendar calendar4 = Calendar.getInstance();
        calendar4.set(calendar.HOUR_OF_DAY, 22);
        calendar4.set(calendar.MINUTE, 0);
        calendar4.set(calendar.SECOND, 0);
        calendar4.set(calendar.MILLISECOND, 0);
        long sdl4 = calendar3.getTimeInMillis();
//
////        Intent intent = new Intent(AlarmList.this, AlarmReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager ALARM1 = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager ALARM2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi2 = PendingIntent.getBroadcast(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager ALARM3 = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi3 = PendingIntent.getBroadcast(this, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager ALARM4 = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi4 = PendingIntent.getBroadcast(this, 3, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19) {
            ALARM1.setExact(AlarmManager.RTC_WAKEUP, sdl,pi);
            ALARM2.setExact(AlarmManager.RTC_WAKEUP, sdl2, pi2);
            ALARM3.setExact(AlarmManager.RTC_WAKEUP, sdl3, pi3);
            ALARM4.setExact(AlarmManager.RTC_WAKEUP, sdl4,  pi4);
        } else {
            ALARM1.set(AlarmManager.RTC_WAKEUP, sdl,pi);
            ALARM2.set(AlarmManager.RTC_WAKEUP, sdl2, pi2);
            ALARM3.set(AlarmManager.RTC_WAKEUP, sdl3, pi3);
            ALARM4.set(AlarmManager.RTC_WAKEUP, sdl4, pi4);
        }

        Toast.makeText(this, "Prescription Reader\nAlarm Started!", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

    }
}