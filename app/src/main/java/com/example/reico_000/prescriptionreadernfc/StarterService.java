package com.example.reico_000.prescriptionreadernfc;


import android.accounts.Account;
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
    private static final String TAG = "StarterService";
//    private Account mAccount = null;
//    private String patientID = "";

    /**
     * The started service starts the AlarmManager.
     */
    @Override
    public void onStart(Intent intent, int startid) {
//        mAccount = null;
//        if (intent.getParcelableExtra("account") != null) {
//            mAccount = intent.getParcelableExtra("account");
//        }
        scheduleAlarm();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
//        if (intent.getParcelableExtra("account") != null) {
//            mAccount = intent.getParcelableExtra("account");
//        }
//        if (intent.getParcelableExtra("patientID") != null) {
//            patientID = intent.getStringExtra("patientID");
//        }

        scheduleAlarm();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "StarterService stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    public void scheduleAlarm() {
        Intent i = new Intent(this, NotificationBarAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        Intent i2 = new Intent(this, DefaultConsumptionReceiver.class);
//        i2.putExtra("account", mAccount);
//        i2.putExtra("patientID", patientID);
//        i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long sdl = calendar.getTimeInMillis();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 12);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        long sdl2 = calendar2.getTimeInMillis();

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.HOUR_OF_DAY, 17);
        calendar3.set(Calendar.MINUTE, 0);
        calendar3.set(Calendar.SECOND, 0);
        calendar3.set(Calendar.MILLISECOND, 0);
        long sdl3 = calendar3.getTimeInMillis();

        Calendar calendar4 = Calendar.getInstance();
        calendar4.set(Calendar.HOUR_OF_DAY, 22);
        calendar4.set(Calendar.MINUTE, 0);
        calendar4.set(Calendar.SECOND, 0);
        calendar4.set(Calendar.MILLISECOND, 0);
        long sdl4 = calendar4.getTimeInMillis();

//        Calendar calendar5 = Calendar.getInstance();
//        calendar5.set(Calendar.HOUR_OF_DAY, 1);
//        calendar5.set(Calendar.MINUTE, 0);
//        calendar5.set(Calendar.SECOND, 0);
//        calendar5.set(Calendar.MILLISECOND, 0);
//        long sdl5 = calendar5.getTimeInMillis();
//
        AlarmManager ALARM1 = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager ALARM2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi2 = PendingIntent.getBroadcast(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager ALARM3 = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi3 = PendingIntent.getBroadcast(this, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager ALARM4 = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi4 = PendingIntent.getBroadcast(this, 3, i, PendingIntent.FLAG_UPDATE_CURRENT);

//        AlarmManager ALARM5 = (AlarmManager)getSystemService(ALARM_SERVICE);
//        PendingIntent pi5 = PendingIntent.getBroadcast(this, 4, i2, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19) {
            ALARM1.setRepeating(AlarmManager.RTC_WAKEUP, sdl, 1000 * 60 * 1440, pi);
            ALARM2.setRepeating(AlarmManager.RTC_WAKEUP, sdl2, 1000 * 60 * 1440, pi2);
            ALARM3.setRepeating(AlarmManager.RTC_WAKEUP, sdl3, 1000 * 60 * 1440, pi3);
            ALARM4.setRepeating(AlarmManager.RTC_WAKEUP, sdl4, 1000 * 60 * 1440, pi4);
//            ALARM5.setRepeating(AlarmManager.RTC_WAKEUP, sdl5, 1000 * 60 * 1440, pi5);
        } else {
            ALARM1.set(AlarmManager.RTC_WAKEUP, sdl, pi);
            ALARM2.set(AlarmManager.RTC_WAKEUP, sdl2, pi2);
            ALARM3.set(AlarmManager.RTC_WAKEUP, sdl3, pi3);
            ALARM4.set(AlarmManager.RTC_WAKEUP, sdl4, pi4);
        }

        Toast.makeText(this, "Prescription Reader\nAlarm Started!", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

    }
}