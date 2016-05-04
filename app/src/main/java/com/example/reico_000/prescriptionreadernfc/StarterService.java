package com.example.reico_000.prescriptionreadernfc;


import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StarterService extends Service {
    private static final String TAG = "StarterService";
    private Account mAccount = null;
    private String patientID = "";
    private List<String> alarmList = new ArrayList<String>();
    /**
     * The started service starts the AlarmManager.
     */
//    @Override
//    public void onStart(Intent intent, int startid) {
//        mAccount = null;
//        if (intent.getParcelableExtra("account") != null) {
//            mAccount = intent.getParcelableExtra("account");
//        }
//        scheduleAlarm();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        if (intent.getParcelableExtra("account") != null) {
            mAccount = intent.getParcelableExtra("account");
        }
        if (intent.getStringExtra("patientID") != null) {
            patientID = intent.getStringExtra("patientID");
        }
//        if (mAccount == null) {
//            Toast.makeText(this, "Account is null in Service!! PatientID: " + patientID, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Account is not null and PatientID: " + patientID, Toast.LENGTH_SHORT).show();
//        }
        populateAlarmList();

        scheduleAlarm();
        return START_REDELIVER_INTENT;
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

    public void populateAlarmList() {
        Log.d("Test", "populateAlarmList entered");
        alarmList = new ArrayList<String>();
        ContentResolver resolver = this.getContentResolver();
        Uri uri = MedicationContract.Medications.CONTENT_URI;
        String[] projection = MedicationDatabaseSQLiteHandler.ALL_MED_KEYS;
        String selection = MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID + " = ?";
        String[] selectionArgs = new String[]{patientID};

        Cursor cursor =
                resolver.query(uri,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        while (cursor.moveToNext()) {
            Integer med_id = cursor.getInt(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ID));
            String consumptionTime = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME));
            String[] timeArr = consumptionTime.split(", ");
            for (int i = 0; i < timeArr.length; i++) {
                alarmList.add(med_id.toString() + " " + timeArr[i]);
            }
        }
        Log.d("Test", "populateAlarmList exited");
        cursor.close();
    }

    public void scheduleAlarm() {
        Intent intent = new Intent(this, NotificationBarAlarm.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent2 = new Intent(this, DefaultConsumptionReceiver.class);
        intent2.putExtra("account", mAccount);
        intent2.putExtra("patientID", patientID);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long sdl = calendar.getTimeInMillis();

        AlarmManager ALARM = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pi5 = PendingIntent.getBroadcast(this, 4, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19) {
//            for (int i = 0; i < alarmList.size(); i++) {
//                String id = alarmList.get(i).split(" ")[0];
//                String time = alarmList.get(i).split(" ")[1];
//                Integer hour = Integer.parseInt(time.split(":")[0]);
//                Integer min = Integer.parseInt(time.split(":")[1]);
//                Calendar cal = Calendar.getInstance();
//                cal.set(Calendar.HOUR_OF_DAY, hour);
//                cal.set(Calendar.MINUTE, min);
//                cal.set(Calendar.SECOND, 0);
//                cal.set(Calendar.MILLISECOND, 0);
//                sdl = cal.getTimeInMillis();
//
//                AlarmManager ALARM = (AlarmManager)getSystemService(ALARM_SERVICE);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i+1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                ALARM.setRepeating(AlarmManager.RTC_WAKEUP, sdl, 1000 * 60 * 1440, pendingIntent);
//            }
            ALARM.setRepeating(AlarmManager.RTC_WAKEUP, sdl, 1000 * 60 * 1440, pi);
        } else {
            ALARM.setRepeating(AlarmManager.RTC_WAKEUP, sdl, 1000 * 60 * 1440, pi);
        }

//        Toast.makeText(this, "Prescription Reader\nAlarm Started!", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

    }
}