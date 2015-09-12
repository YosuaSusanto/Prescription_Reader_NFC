package com.example.reico_000.prescriptionreadernfc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by reico_000 on 20/3/2015.
 */
public class NotificationBarAlarm extends BroadcastReceiver {

    NotificationManager notifyManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NotificationAlarm", "onReceive");

        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // This Activity will be started when the user clicks the notification
        // in the notification bar
        Intent notificationIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notif = new Notification(R.mipmap.ic_launcher, "Medication Reminder!", System.currentTimeMillis());

        long current_time = System.currentTimeMillis();
        String[] titleArray = new String[4];
        titleArray[0] = "Morning Medications";
        titleArray[1] = "Afternoon Medications";
        titleArray[2] = "Evening Medications";
        titleArray[3] = "Before Sleep Medications";
        int timeMessage = 3;

        Calendar timeMorn = Calendar.getInstance();
        timeMorn.set(Calendar.HOUR_OF_DAY, 7);
        timeMorn.set(Calendar.MINUTE, 10);
        long timeForMorn = timeMorn.getTimeInMillis();

        Calendar timeAft = Calendar.getInstance();
        timeAft.set(Calendar.HOUR_OF_DAY, 12);
        timeAft.set(Calendar.MINUTE, 10);
        long timeForAft = timeAft.getTimeInMillis();

        Calendar timeEve = Calendar.getInstance();
        timeEve.set(Calendar.HOUR_OF_DAY, 17);
        timeEve.set(Calendar.MINUTE, 10);
        long timeForEve = timeEve.getTimeInMillis();

        Calendar timeBS = Calendar.getInstance();
        timeBS.set(Calendar.HOUR_OF_DAY, 21);
        timeBS.set(Calendar.MINUTE, 50);
        long timeForBS = timeEve.getTimeInMillis();

        if (current_time < timeForEve){
            timeMessage = 2;
        }

        if (current_time < timeForAft){
            timeMessage = 1;
        }

        if (current_time < timeForMorn){
            timeMessage = 0;
        }

        notif.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notif.setLatestEventInfo(context, titleArray[timeMessage], "Remember to take your medications!", contentIntent);

        notif.flags = Notification.FLAG_AUTO_CANCEL;

        notifyManager.notify(timeMessage, notif);

        //reset alarms when BS alarm is called
        if(current_time > timeForBS) {
            StarterService start = new StarterService();
            start.scheduleAlarm();
        }
    }

}
