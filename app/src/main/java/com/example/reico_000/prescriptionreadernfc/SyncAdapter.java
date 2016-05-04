package com.example.reico_000.prescriptionreadernfc;

import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yosua Susanto on 26/10/2015.
 *
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    Context mContext;
    MedicationDatabaseSQLiteHandler medicationDBHandler;

//    android.os.Debug.waitForDebugger();
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext = context;
    }
    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    /*
 * Specify the code you want to run in the sync adapter. The entire
 * sync adapter runs in a background thread, so you don't have to set
 * up your own background processing.
 */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
    /*
     * Put the data transfer code here.
     */
        String functions = extras.getString("functions");
        Log.d("SyncAdapter", functions);
        if (functions.equals("insertConsumption")) {
            int med_id = extras.getInt("med_id");
            String consumption_time = extras.getString("consumption_time");
            String is_taken = extras.getString("is_taken");
            String remaining_dosage = extras.getString("remaining_dosage");
            insertDefaultConsumptionRemoteDB(med_id, consumption_time, is_taken, remaining_dosage);
        } else if (functions.equals("updateConsumption")) {
            int med_id = extras.getInt("med_id");
            String consumption_time_for_update = extras.getString("consumption_time_for_update");
            String consumption_time = extras.getString("consumption_time");
            String remaining_dosage = extras.getString("remaining_dosage");
            updateConsumptionsRemoteDB(med_id, consumption_time_for_update, consumption_time, remaining_dosage);
        } else if (functions.equals("populateLocalDB")) {
            String patient_id = extras.getString("patient_id");
            populateLocalDB(patient_id);
            populateLocalSymptomsDB(patient_id);
        } else if (functions.equals("updateDosage")) {
            int med_id = extras.getInt("med_id");
            String effective_dosage = extras.getString("effective_dosage");
            updateDosageRemoteDB(med_id, effective_dosage);
        } else if (functions.equals("updateMedConsumptionTime")) {
            int med_id = extras.getInt("med_id");
            String newConsumptionTime = extras.getString("newConsumptionTime");
            updateMedConsumptionTimeRemoteDB(med_id, newConsumptionTime);
        } else if (functions.equals("updateUntakenConsumptionTime")) {
            int med_id = extras.getInt("med_id");
            String oldConsumptionTime = extras.getString("oldConsumptionTime");
            String newConsumptionTime = extras.getString("newConsumptionTime");
            updateUntakenConsumptionTimeRemoteDB(med_id, oldConsumptionTime, newConsumptionTime);
        } else if (functions.equals("updateRemarks")) {
            int med_id = extras.getInt("med_id");
            String remarks = extras.getString("remarks");
            updateRemarksRemoteDB(med_id, remarks);
        } else if (functions.equals("blockMedication")) {
            int med_id = extras.getInt("med_id");
            blockMedicationRemoteDB(med_id);
        } else if (functions.equals("insertSymptoms")) {
            String patientID = extras.getString("patient_id");
            String patientName = extras.getString("patient_name");
            String symptoms = extras.getString("symptoms");
            String timestamp = extras.getString("timestamp");
            insertSymptomsRemoteDB(patientID, patientName, symptoms, timestamp);
        }
    }

    /**
     * Populate local db with medication data from server
     * */
    private void populateLocalDB(final String patientID) {
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(mContext);
//        final ProgressDialog pDialog = new ProgressDialog(mContext);
//        pDialog.setMessage("Fetching data from server...");
//        pDialog.setCancelable(false);
//        pDialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_POPULATE_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean newMed = false;
                        Log.d("populateLocalDB Method", response);
                        String patient_id = "";
                        try {
                            ContentResolver resolver = mContext.getContentResolver();
                            ContentValues values = new ContentValues();
                            List<Integer> onlineIdList = new ArrayList<Integer>();
                            // Parsing json array response
                            // loop through each json object
                            JSONArray jArr = new JSONArray(response);
                            for (int i = 0; i < jArr.length(); i++) {

                                JSONObject medication = (JSONObject) jArr.get(i);

                                int id = medication.getInt("id");
                                String brand_name = medication.getString("brand_name");
                                String generic_name = medication.getString("generic_name");
                                String dosage_form = medication.getString("dosage_form");
                                String per_dosage = medication.getString("per_dosage");
                                String unit = "";
                                if (per_dosage.trim().contains(" ")) {
                                    unit = per_dosage.substring(per_dosage.indexOf(" ") + 1);
                                    per_dosage = per_dosage.substring(0, per_dosage.indexOf(" "));
                                }
                                String total_dosage = medication.getString("total_dosage");
                                if (total_dosage.trim().contains(" ")) {
                                    total_dosage = total_dosage.substring(0, total_dosage.indexOf(" "));
                                }
                                String consumption_time = medication.getString("consumption_time");
                                patient_id = medication.getString("patient_id");
                                String administration = medication.getString("administration");
                                String remarks = medication.getString("remarks");
                                String prescriptionDate = medication.getString("prescriptionDate");

//                                consumption_time = consumption_time.replace("Morning", "M");
//                                consumption_time = consumption_time.replace("Afternoon", "A");
//                                consumption_time = consumption_time.replace("Evening", "E");
//                                consumption_time = consumption_time.replace("Before sleep", "B");
                                //consumption_time = consumption_time.replace(",", "");
//                                consumption_time = consumption_time.replace(" ", "");
                                consumption_time = consumption_time.replaceAll(" +", " ");

                                MedicationObject medObj = new MedicationObject(id, brand_name,
                                        generic_name, dosage_form, unit, per_dosage, total_dosage,
                                        consumption_time, patient_id, administration, remarks,
                                        prescriptionDate, "");
                                onlineIdList.add(id);
                                values = medObj.getContentValues();
                                if (!medicationDBHandler.CheckIsDataAlreadyInDBorNot(MedicationDatabaseSQLiteHandler.TABLE_MEDICATIONS,
                                        MedicationDatabaseSQLiteHandler.KEY_ID, String.valueOf(id))) {
                                    resolver.insert(MedicationContract.Medications.CONTENT_URI, values);
                                    insertDefaultConsumption(id, consumption_time, total_dosage);
                                    newMed = true;
                                    Toast.makeText(mContext,
                                            generic_name + " inserted",
                                            Toast.LENGTH_LONG).show();
                                }
                                values.clear();
                            }
                            medicationDBHandler.deleteNonExistingMedication(onlineIdList, patient_id);
                            if (newMed) {
                                scheduleAlarm(mContext, patient_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(mContext,
//                                    "Error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(mContext,
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("patient_id", patientID);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
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

    /**
     * Populate local db with medication data from server
     * */
    private void populateLocalSymptomsDB(final String patientID) {
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(mContext);
//        final ProgressDialog pDialog = new ProgressDialog(mContext);
//        pDialog.setMessage("Fetching data from server...");
//        pDialog.setCancelable(false);
//        pDialog.show();

        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_POPULATE_SYMPTOMS_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("populateSymptomsDB", response);
                        String patient_id = "";
                        try {
                            ContentResolver resolver = mContext.getContentResolver();
                            ContentValues values = new ContentValues();
                            // Parsing json array response
                            // loop through each json object
                            JSONArray jArr = new JSONArray(response);
                            for (int i = 0; i < jArr.length(); i++) {

                                JSONObject symptoms = (JSONObject) jArr.get(i);

                                int id = symptoms.getInt("id");
                                String patient_name = symptoms.getString("patient_name");
                                String patient_nric = symptoms.getString("patient_nric");
                                String reported_symptoms = symptoms.getString("symptoms");
                                String reported_on = symptoms.getString("reported_on");
                                Log.d("populateSymptoms", "ID: " + String.valueOf(id));
                                Log.d("populateSymptoms", "Patient Name: " + patient_name);
                                Log.d("populateSymptoms", "Patient NRIC: " + patient_nric);
                                Log.d("populateSymptoms", "Symptoms: " + reported_symptoms);
                                Log.d("populateSymptoms", "Reported On: " + reported_on);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a", java.util.Locale.US);
                                SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date reported_time = oldDateFormat.parse(reported_on, new ParsePosition(0));
                                String timeStamp = dateFormat.format(reported_time);
                                SymptomsObject symptomsObj = new SymptomsObject(id, patient_nric,
                                        patient_name, reported_symptoms, timeStamp);
//                                onlineIdList.add(id);
                                values = symptomsObj.getContentValues();
                                String[] dbFields = new String[]{MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID, MedicationDatabaseSQLiteHandler.KEY_SYMPTOMS};
                                String[] fieldValues = new String[]{patient_nric, reported_symptoms};
                                if (!medicationDBHandler.CheckIsDataAlreadyInDBorNot(MedicationDatabaseSQLiteHandler.TABLE_SYMPTOMS,
                                        dbFields, fieldValues)) {
                                    resolver.insert(MedicationContract.Symptoms.CONTENT_URI, values);
                                }
                                values.clear();
                            }
//                            medicationDBHandler.deleteNonExistingMedication(onlineIdList, patient_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(mContext,
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("patient_id", patientID);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    /**
     * Update consumption time on remote medication DB
     * */
    private void updateMedConsumptionTimeRemoteDB(final int med_id, final String newConsumptionTime) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_CONSUMPTION_TIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("updateMedConsTime", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String operation = jObj.getString("operation");
                                String row_nums = jObj.getString("row_nums");
                                String dispMessage = operation;
                                if (operation.equals("update")) {
                                    dispMessage = "Updated " + row_nums + "row(s)";
                                } else if (operation.equals("delete")) {
                                    dispMessage = "Deleted " + row_nums + "row(s)";
                                }
//                                Toast.makeText(mContext,
//                                        dispMessage, Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));
                params.put("newConsumptionTime", newConsumptionTime);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    /**
     * Block marked medication on remote medication DB
     * */
    private void blockMedicationRemoteDB(final int med_id) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_BLOCK_MEDICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("blockMedication", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String dispMessage = "Medication has been marked to be reviewed by your physician.";
                                Toast.makeText(mContext,
                                        dispMessage, Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    /**
     * Update medication remarks on remote medication DB
     * */
    private void updateRemarksRemoteDB(final int med_id, final String remarks) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_REMARKS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("udpateRemarks", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
//                                if (!remarks.equals("")) {
//                                    String dispMessage = "Medication has been marked to be reviewed by your physician.";
//                                    Toast.makeText(mContext,
//                                            dispMessage, Toast.LENGTH_LONG).show();
//                                }
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));
                params.put("remarks", remarks);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    /**
     * Update untaken consumption time on remote medication DB
     * */
    private void updateUntakenConsumptionTimeRemoteDB(final int med_id, final String oldConsumptionTime,
                                                      final String newConsumptionTime) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_UNTAKEN_CONSUMPTION_TIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("updateUntakenConsTime", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String operation = jObj.getString("operation");
                                String row_nums = jObj.getString("row_nums");
                                String dispMessage = operation;
                                if (operation.equals("update")) {
                                    dispMessage = "Updated " + row_nums + "row(s)";
                                } else if (operation.equals("delete")) {
                                    dispMessage = "Deleted " + row_nums + "row(s)";
                                }
//                                Toast.makeText(mContext,
//                                        dispMessage, Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));
                params.put("oldConsumptionTime", oldConsumptionTime);
                params.put("newConsumptionTime", newConsumptionTime);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    /**
     * Update dosage on remote medication DB
     * */
    private void updateDosageRemoteDB(final int med_id, final String effective_dosage) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_DOSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("updateDosageRemoteDB", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String operation = jObj.getString("operation");
                                String row_nums = jObj.getString("row_nums");
                                String dispMessage = operation;
                                if (operation.equals("update")) {
                                    dispMessage = "Updated " + row_nums + "row(s)";
                                } else if (operation.equals("delete")) {
                                    dispMessage = "Deleted " + row_nums + "row(s)";
                                }
//                                Toast.makeText(mContext,
//                                        dispMessage, Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));
                params.put("effective_dosage", effective_dosage);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    private void insertDefaultConsumption(int med_id, String consumptionTime, String remaining_dosage) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = MedicationContract.Consumption.CONTENT_URI;
        String[] projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_ID, MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID,
                MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT};
        String selection = MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID + " = ? AND " +
                MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " >= date('now', 'localtime') AND " +
                MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " < date('now', 'localtime', '+1 day')";
        String[] selectionArgs = new String[]{Integer.toString(med_id)};
        Cursor cursor2 =
                resolver.query(uri,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        Log.d("Test", "cursor2.Length: " + cursor2.getCount());
        if (cursor2.getCount() == 0) {
            String[] consumptionTimeArr = consumptionTime.split(", ");
            for (int i = 0; i < consumptionTimeArr.length; i++) {
                String reminderTime = consumptionTimeArr[i] + ":00";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
                String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
                currentTimeStamp += " " + reminderTime;

                ContentValues values = new ContentValues();
                values.put(MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID, med_id);
                values.put(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT, currentTimeStamp);
                values.put(MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN, "No");
                values.put(MedicationDatabaseSQLiteHandler.KEY_REMAINING_DOSAGE, remaining_dosage);
                resolver.insert(MedicationContract.Consumption.CONTENT_URI, values);
                insertDefaultConsumptionRemoteDB(med_id, currentTimeStamp, "No", remaining_dosage);
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


    /**
     * Insert stub consumptions remote DB
     * */
    private void insertDefaultConsumptionRemoteDB(final int med_id, final String consumption_time, final String is_taken, final String remaining_dosage) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_INSERT_CONSUMPTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("insertConsumptionRemote", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
//                                Toast.makeText(mContext,
//                                        "Initial consumption data inserted", Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));
                params.put("consumption_time", consumption_time);
                params.put("is_taken", is_taken);
                params.put("remaining_dosage", remaining_dosage);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }

    /**
     * Update consumption details on remote medication DB
     * */
    private void updateConsumptionsRemoteDB(final int med_id, final String consumption_time_for_update,
                                            final String consumption_time, final String remaining_dosage) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_CONSUMPTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("updateConsRemoteDB", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String row_nums = jObj.getString("row_nums");
                                String dispMessage = "Updated " + row_nums + "row(s)";
//                                Toast.makeText(mContext,
//                                        dispMessage, Toast.LENGTH_LONG).show();
                            } else {
                                // Error in updating. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("med_id", String.valueOf(med_id));
                params.put("consumption_time_for_update", consumption_time_for_update);
                params.put("consumption_time", consumption_time);
                params.put("remaining_dosage", remaining_dosage);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }
    /**
     * Insert symptoms report remote DB
     * */
    private void insertSymptomsRemoteDB(final String patientID, final String patientName,
                                        final String symptoms, final String timestamp) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_INSERT_SYMPTOMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("insertSymptomsRemote", response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                Toast.makeText(mContext,
                                        "Report submitted", Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(mContext,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(mContext,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("patient_id", patientID);
                params.put("patient_name", patientName);
                params.put("symptoms", symptoms);
                params.put("timestamp", timestamp);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(mContext).addToRequestQueue(req);
    }
}
