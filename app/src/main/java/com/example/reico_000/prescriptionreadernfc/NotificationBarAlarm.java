package com.example.reico_000.prescriptionreadernfc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import helper.SessionManager;

/**
 * Created by reico_000 on 20/3/2015.
 */
public class NotificationBarAlarm extends BroadcastReceiver {

    NotificationManager notifyManager = null;
    List<String> medicineList;
    List<String> lateMedicineList;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (notifyManager == null) {
            notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        boolean isSnoozed = false;
        if (intent.getBooleanExtra("isSnoozed", false)) {
            isSnoozed = intent.getBooleanExtra("isSnoozed", false);
        }
        if (isSnoozed) {
            notifyManager.cancel(0);
            AlarmManager ALARM = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            intent.putExtra("isSnoozed", false);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= 19) {
                ALARM.setExact(AlarmManager.RTC, System.currentTimeMillis() + 1000*60*15 ,pendingIntent);
            } else {
                ALARM.set(AlarmManager.RTC, System.currentTimeMillis() + 1000*60*15 ,pendingIntent);
            }
        } else {
            Log.d("NotificationAlarm", "onReceive");
            SessionManager session = new SessionManager(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String PatientID = session.getPatientID();

            // This Activity will be started when the user clicks the notification
            // in the notification bar

            long current_time = System.currentTimeMillis();
            String[] titleArray = new String[4];
            titleArray[0] = "Morning Medications";
            titleArray[1] = "Afternoon Medications";
            titleArray[2] = "Evening Medications";
            titleArray[3] = "Before Sleep Medications";
            int timeMessage = 3;

            Calendar timeMorn = Calendar.getInstance();
            timeMorn.set(Calendar.HOUR_OF_DAY, 12);
            timeMorn.set(Calendar.MINUTE, 0);
            long timeForMorn = timeMorn.getTimeInMillis();

            Calendar timeAft = Calendar.getInstance();
            timeAft.set(Calendar.HOUR_OF_DAY, 17);
            timeAft.set(Calendar.MINUTE, 0);
            long timeForAft = timeAft.getTimeInMillis();

            Calendar timeEve = Calendar.getInstance();
            timeEve.set(Calendar.HOUR_OF_DAY, 22);
            timeEve.set(Calendar.MINUTE, 0);
            long timeForEve = timeEve.getTimeInMillis();

            Calendar timeBS = Calendar.getInstance();
            timeBS.set(Calendar.HOUR_OF_DAY, 2);
            timeBS.set(Calendar.MINUTE, 0);
            long timeForBS = timeBS.getTimeInMillis();

            if (current_time < timeForEve) {
                timeMessage = 2;
            }

            if (current_time < timeForAft) {
                timeMessage = 1;
            }

            if (current_time < timeForMorn) {
                timeMessage = 0;
            }

            Log.d("Test", "Current time: " + current_time + ", timeMessage: " + timeMessage);

            if (prefs.getBoolean("pref_reminderToggle", true)) {
                medicineList = new ArrayList<String>();
                lateMedicineList = new ArrayList<String>();
                String textToShow = "Remember to take the following medications:";
                if (updateMedicineList(context, medicineList, lateMedicineList, PatientID, timeMessage)) {
                    if (medicineList.size() > 0) {
                        for (int i = 0; i < medicineList.size(); i++) {
                            textToShow += "\n- " + medicineList.get(i);
                        }
                    }
                    if (lateMedicineList.size() > 0) {
                        textToShow += "\n\n For these following medications, you have missed the dose of " +
                                "your previous medication. If you are near the next dose of your " +
                                "medication, skip the previous dose and consume your next dose instead";
                        for (int i = 0; i < lateMedicineList.size(); i++) {
                            textToShow += "\n- " + lateMedicineList.get(i);
                        }
                    }
                    if (medicineList.size() > 0 || lateMedicineList.size() > 0) {
                        Intent notificationIntent = new Intent(context, MainActivity.class);
                        notificationIntent.putExtra("text", textToShow);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        notificationIntent.setAction("foo");
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        Intent snoozeIntent = new Intent(context, NotificationBarAlarm.class);
                        snoozeIntent.putExtra("isSnoozed", true);
                        PendingIntent pIntent = PendingIntent.getActivity(context, 0, snoozeIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        AlarmManager ALARM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        ALARM.set(AlarmManager.RTC, System.currentTimeMillis() + 1000 * 60 * 15, pendingIntent);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                        builder = builder.setContentIntent(contentIntent)
                                .setSmallIcon(R.mipmap.launcher)
                                .setAutoCancel(true).setContentTitle("Medications Reminder")
                                .setContentText(textToShow)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(textToShow))
                                .setPriority(Notification.PRIORITY_HIGH)
                                .addAction(R.drawable.ic_action_alarms, "Snooze 15 mins", pIntent);

                        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        if (prefs.getBoolean("pref_reminderVibrationToggle", true)) {
                            builder = builder.setDefaults(Notification.DEFAULT_VIBRATE);
                        } else {
                            builder = builder.setVibrate(new long[0]);
                        }

                        if (prefs.getBoolean("pref_reminderSoundToggle", true) &&
                                am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                            builder = builder.setSound(RingtoneManager.
                                    getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        }
                        notifyManager.notify(0, builder.build());
                    }
                }
            }
        }
    }

    public boolean updateMedicineList(Context context, List<String> medicineList,
                                      List<String> lateMedicineList, String PatientID, int timeMessage) {
        ContentResolver resolver = context.getContentResolver();
        Calendar currentTime, tempTime1, tempTime2;
        boolean reminderOn = false, lateReminderOn = false;
        String timeCode = "", timeString = "", consumptionTime = "";

        if (timeMessage == 0) {
            timeCode = "M";
            timeString = "07:00:00";
        } else if (timeMessage == 1) {
            timeCode = "A";
            timeString = "12:00:00";
        } else if (timeMessage == 2) {
            timeCode = "E";
            timeString = "17:00:00";
        } else if (timeMessage == 3) {
            timeCode = "BS";
            timeString = "22:00:00";
        }

        Uri uri = MedicationContract.Medications.CONTENT_URI;
        String[] projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID, MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME,
                MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME, MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME,
                MedicationDatabaseSQLiteHandler.KEY_ID};
        String selection = MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID + " = ?";
        String[] selectionArgs = new String[]{PatientID};

        Cursor cursor =
                resolver.query(uri,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        while (cursor.moveToNext()) {
            List<String> consumptionTimeStart = new ArrayList<String>(), consumptionTimeEnd = new ArrayList<String>();
            List<Calendar> consumptionTimeStartCalendar = new ArrayList<Calendar>(), consumptionTimeEndCalendar = new ArrayList<Calendar>();

            String brandName, genericName, currentReminderTime = "";
            int medId;
            reminderOn = false;
            lateReminderOn = false;

            brandName = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME));
            genericName = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME));
            consumptionTime = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME));
            medId = cursor.getInt(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ID));
            Log.d("Test", "BrandName: " + brandName + ", medId: " + medId);

            consumptionTime = consumptionTime.replace("BS", "B");
            String[] consumptionTimeArr = consumptionTime.split(", ");
//            for (int i = 0; i < consumptionTime.length(); i++) {
            for (int i = 0; i < consumptionTimeArr.length; i++) {
                timeString = consumptionTimeArr[i] + ":00";
//                if (consumptionTime.contains("M") || consumptionTime.contains("A")
//                        || consumptionTime.contains("E") || consumptionTime.contains("B")) {
//                    char c = consumptionTime.charAt(i);
//                    if (c == 'M') {
//                        timeString = "07:00:00";
//                    } else if (c == 'A') {
//                        timeString = "12:00:00";
//                    } else if (c == 'E') {
//                        timeString = "17:00:00";
//                    } else if (c == 'B') {
//                        timeString = "22:00:00";
//                    }
//                }
                consumptionTimeStart.add(timeString);

                long current_time = System.currentTimeMillis();
                Calendar timeAlarm = Calendar.getInstance();
                String hour = timeString.substring(0, 2), minute = timeString.substring(3, 5);
                timeAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                timeAlarm.set(Calendar.MINUTE, Integer.parseInt(minute));
                long timeForAlarm = timeAlarm.getTimeInMillis();

                if (current_time > timeForAlarm){
                    currentReminderTime = timeString;
                }
            }
            for (int i = 0; i < consumptionTimeStart.size(); i++) {
                String[] startTime = consumptionTimeStart.get(i).split(":");
                tempTime1 = Calendar.getInstance();
                tempTime1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
                tempTime1.set(Calendar.MINUTE, Integer.parseInt(startTime[1]));
                if (startTime.length > 2) {
                    tempTime1.set(Calendar.SECOND, Integer.parseInt(startTime[2]));
                } else {
                    tempTime1.set(Calendar.SECOND, 0);
                }
                tempTime1.set(Calendar.MILLISECOND, 0);
                consumptionTimeStartCalendar.add(tempTime1);
            }
            for (int i = 0; i < consumptionTimeStartCalendar.size(); i++) {
                tempTime1 = (Calendar) consumptionTimeStartCalendar.get(i).clone();
                if (i == consumptionTimeStartCalendar.size() - 1) {
                    tempTime2 = (Calendar) consumptionTimeStartCalendar.get(0).clone();
                    tempTime2.add(Calendar.DAY_OF_YEAR, 1);
                } else {
                    tempTime2 = (Calendar) consumptionTimeStartCalendar.get(i+1).clone();
                }
                long timeDiff = tempTime2.getTimeInMillis() - tempTime1.getTimeInMillis();
                int secondDiffInt = (int) timeDiff/1000/2;
                tempTime1.add(Calendar.SECOND, secondDiffInt);
                consumptionTimeEndCalendar.add(tempTime1);
            }
            for (int i = 0; i < consumptionTimeStartCalendar.size(); i++) {
                Log.d("Test", "consumptionTimeStart(" + i + "): " + consumptionTimeStartCalendar.get(i));
                Log.d("Test", "consumptionTimeEnd(" + i + "): " + consumptionTimeEndCalendar.get(i));
            }
            for (int i = 0; i < consumptionTimeStartCalendar.size(); i++) {
                currentTime = Calendar.getInstance();
                if (i == consumptionTimeStartCalendar.size() - 1) {
                    tempTime2 = (Calendar) consumptionTimeStartCalendar.get(0).clone();
                    tempTime2.add(Calendar.DAY_OF_YEAR, 1);
                } else {
                    tempTime2 = (Calendar) consumptionTimeStartCalendar.get(i+1).clone();
                }
//                if (currentTime.after(consumptionTimeStartCalendar.get(i)) &&
//                        currentTime.before(consumptionTimeEndCalendar.get(i))) {
//                    reminderOn = true;
//                    Log.d("Test", "reminder is on");
//                    break;
//                }
                if (currentTime.after(consumptionTimeStartCalendar.get(i)) &&
                        currentTime.before(tempTime2)) {
                    if (currentTime.before(consumptionTimeEndCalendar.get(i))) {
                        reminderOn = true;
                        Log.d("Test", "reminder is on");
                        break;
                    } else {
                        lateReminderOn = true;
                        Log.d("Test", "late reminder is on");
                        break;
                    }
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            String timeStamp = dateFormat.format(new Date()); // Find todays date
            timeStamp += " " + currentReminderTime;
            Log.d("Test", "TimeStamp: " + timeStamp);

            uri = MedicationContract.Consumption.CONTENT_URI;
            projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID, MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT,
                    MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN};
            selection = MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID + " = ? AND " +
                    MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " = ? AND " +
                    MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN + " = ?";
            selectionArgs = new String[]{Integer.toString(medId), timeStamp, "No"};
            Cursor cursor2 =
                    resolver.query(uri,
                            projection,
                            selection,
                            selectionArgs,
                            null);
            while (cursor2.moveToNext()) {
                String consumptionTime2;
                consumptionTime2 = cursor2.getString(cursor2.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT));

                if (consumptionTime2.contains(currentReminderTime) && reminderOn) {
//                if (consumptionTime2.contains(timeString)) {
                    if (brandName.equals("")) {
                        medicineList.add(genericName);
                    } else {
                        medicineList.add(genericName + " (" + brandName + ")");
                    }
                } else if (consumptionTime2.contains(currentReminderTime) && lateReminderOn) {
                    if (brandName.equals("")) {
                        lateMedicineList.add(genericName);
                    } else {
                        lateMedicineList.add(genericName + " (" + brandName + ")");
                    }
                }
            }
            cursor2.close();
        }
        cursor.close();
        if (medicineList.size() > 0 || lateMedicineList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
