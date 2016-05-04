package com.example.reico_000.prescriptionreadernfc;

import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Yosua Susanto on 30/12/2015.
 */
public class DefaultConsumptionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ConsumptionReceiver", "onReceive");
        Account account = null;
        String patientID = "";
        if (intent.getParcelableExtra("account") != null) {
            account = intent.getParcelableExtra("account");
        }
        if (intent.getStringExtra("patientID") != null) {
            patientID = intent.getStringExtra("patientID");
        }

//        if (account != null && !patientID.equals("")) {
        if (account != null && !patientID.equals("")) {
//            Toast.makeText(context, "Account: " + account.name + " (" + account.type + "), PatientID: " + patientID,
//                    Toast.LENGTH_SHORT).show();
            insertDefaultConsumptionData(context, account, patientID);
        }
//        Toast.makeText(context, "PatientID: " + patientID, Toast.LENGTH_SHORT).show();
        scheduleAlarm(context, patientID);
        insertDefaultConsumptionData(context, account, patientID);
    }

    public void scheduleAlarm(Context context, String patientID) {
        List<String> alarmList = new ArrayList<String>();
        ContentResolver resolver = context.getContentResolver();
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

        Intent intent = new Intent(context, NotificationBarAlarm.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (int i = 0; i < alarmList.size(); i++) {
            String id = alarmList.get(i).split(" ")[0];
            String time = alarmList.get(i).split(" ")[1];
            Integer hour = Integer.parseInt(time.split(":")[0]);
            Integer min = Integer.parseInt(time.split(":")[1]);
            Calendar curCal = Calendar.getInstance();
            long curTimeInMillis = curCal.getTimeInMillis();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long sdl = cal.getTimeInMillis();

            AlarmManager ALARM = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i+1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ALARM.cancel(pendingIntent);
            if (Build.VERSION.SDK_INT >= 19) {
                if (curTimeInMillis < sdl) {
                    ALARM.setExact(AlarmManager.RTC_WAKEUP, sdl, pendingIntent);
                }
            } else {
                if (curTimeInMillis < sdl) {
                    ALARM.set(AlarmManager.RTC_WAKEUP, sdl, pendingIntent);
                }
            }
        }
    }

    public void insertDefaultConsumptionData(Context context, Account account, String patientID) {
        Log.d("Test", "insertDefaultConsumptionData entered");
        Log.d("Test", "patientID: " + patientID);
        ContentResolver resolver = context.getContentResolver();
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
        Log.d("Test", "insertDefaultConsumptionData entered(after cursor)");
        while (cursor.moveToNext()) {
            int med_id = cursor.getInt(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ID));
            String consumptionTime = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME));
            String remaining_dosage = cursor.getString(cursor.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE));
            uri = MedicationContract.Consumption.CONTENT_URI;
            projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_ID, MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID,
                    MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT};
            selection = MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID + " = ? AND " +
                    MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " >= date('now', 'localtime') AND " +
                    MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " < date('now', 'localtime', '+1 day')";
            selectionArgs = new String[]{Integer.toString(med_id)};
            Cursor cursor2 =
                    resolver.query(uri,
                            projection,
                            selection,
                            selectionArgs,
                            null);
            Log.d("Test", "cursor2.Length: " + cursor2.getCount());
            if (cursor2.getCount() == 0) {
                String[] consumptionTimeArr = consumptionTime.split(", ");
//                for (int i = 0; i < consumptionTime.length(); i++) {
                for (int i = 0; i < consumptionTimeArr.length; i++) {
//                    char c = consumptionTime.charAt(i);
                    String reminderTime = consumptionTimeArr[i] + ":00";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
                    String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
                    currentTimeStamp += " " + reminderTime;
//                    switch (c) {
//                        case 'M':
//                            currentTimeStamp += " 07:00:00";
//                            break;
//                        case 'A':
//                            currentTimeStamp += " 12:00:00";
//                            break;
//                        case 'E':
//                            currentTimeStamp += " 17:00:00";
//                            break;
//                        case 'B':
//                            currentTimeStamp += " 22:00:00";
//                            break;
//                    }
                    ContentValues values = new ContentValues();
                    values.put(MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID, med_id);
                    values.put(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT, currentTimeStamp);
                    values.put(MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN, "No");
                    values.put(MedicationDatabaseSQLiteHandler.KEY_REMAINING_DOSAGE, remaining_dosage);
                    resolver.insert(MedicationContract.Consumption.CONTENT_URI, values);
                    insertDefaultConsumptionRemoteDB(account, med_id, currentTimeStamp, "No", remaining_dosage);
                    Log.d("Test", "Default consumption data doesn't exist... Inserting data...");
                }
            }
            else {
                while (cursor2.moveToNext()) {
                    String consumedAt = cursor2.getString(cursor2.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT));
                    Log.d("Test", "ConsumedAt: " + consumedAt);
                }
                Log.d("Test", "Default consumption data exists... Not inserting...");
            }
            cursor2.close();
        }
        Log.d("Test", "insertDefaultConsumption exited");
        cursor.close();
    }

    /**
     * Insert stub consumptions remote DB
     * */
    private void insertDefaultConsumptionRemoteDB(Account account, int med_id, String consumption_time, String is_taken, String remaining_dosage) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("functions", "insertConsumption");
        settingsBundle.putInt("med_id", med_id);
        settingsBundle.putString("consumption_time", consumption_time);
        settingsBundle.putString("is_taken", is_taken);
        settingsBundle.putString("remaining_dosage", remaining_dosage);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(account, MedicationContract.AUTHORITY, settingsBundle);
    }

}
