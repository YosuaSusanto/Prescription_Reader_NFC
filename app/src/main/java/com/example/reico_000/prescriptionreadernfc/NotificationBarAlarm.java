package com.example.reico_000.prescriptionreadernfc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import helper.SessionManager;

/**
 * Created by reico_000 on 20/3/2015.
 */
public class NotificationBarAlarm extends BroadcastReceiver {

    NotificationManager notifyManager;
    List<String> medicineList;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NotificationAlarm", "onReceive");
        SessionManager session = new SessionManager(context);
        String PatientID = session.getPatientID();
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // This Activity will be started when the user clicks the notification
        // in the notification bar
        Intent notificationIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

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
        long timeForBS = timeBS.getTimeInMillis();

        if (current_time < timeForEve){
            timeMessage = 2;
        }

        if (current_time < timeForAft){
            timeMessage = 1;
        }

        if (current_time < timeForMorn){
            timeMessage = 0;
        }

        medicineList = new ArrayList<String>();
        String textToShow = "Remember to take the following medications:";
        updateMedicineList(context, medicineList, PatientID, timeMessage);
        if (medicineList.size() > 0) {
            for (int i = 0; i < medicineList.size(); i++) {
                textToShow += "\n- " + medicineList.get(i);
            }
            builder = builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true).setContentTitle(titleArray[timeMessage])
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(textToShow))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            notifyManager.notify(timeMessage, builder.build());
        }

        //reset alarms when BS alarm is called
//        if(current_time > timeForBS) {
//            StarterService start = new StarterService();
//            start.scheduleAlarm();
//        }
    }

    public void updateMedicineList(Context context, List<String> medicineList, String PatientID, int timeMessage) {
        ContentResolver resolver = context.getContentResolver();
        String timeCode = "";

        if (timeMessage == 0) {
            timeCode = "M";
        } else if (timeMessage == 1) {
            timeCode = "A";
        } else if (timeMessage == 2) {
            timeCode = "E";
        } else if (timeMessage == 3) {
            timeCode = "BS";
        }

        Uri uri = MedicationContract.Medications.CONTENT_URI;
        String[] projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID, MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME,
                MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME, MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME};
        String selection = MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID + " = ?";
        String[] selectionArgs = new String[]{PatientID};
        Cursor cursor =
                resolver.query(uri,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        while (cursor.moveToNext()) {
            String brandName, genericName, consumptionTime;
            brandName = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME));
            genericName = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME));
            consumptionTime = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME));

            if (consumptionTime.contains(timeCode)) {
                medicineList.add(brandName + " (" + genericName + ")");
            }
        }
        cursor.close();
    }
}
