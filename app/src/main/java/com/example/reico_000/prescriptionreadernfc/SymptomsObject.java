package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yosua Susanto on 18/4/2016.
 */
public class SymptomsObject implements Comparable<SymptomsObject> {
    int _ID = -1;
    String _nric = "";
    String _patientName="";
    String _symptoms = "";
    String _reportedOn = "";

    public SymptomsObject(){
    }

    public SymptomsObject(int id, String nric, String patientName, String symptoms, String reportedOn) {
        this._ID = id;
        this._nric = nric;
        this._patientName = patientName;
        this._symptoms = symptoms;
        this._reportedOn = reportedOn;
    }
    public int get_id(){
        return this._ID;
    }

    public String get_nric(){
        return this._nric;
    }

    public String get_patientName(){
        return this._patientName;
    }

    public String get_symptoms(){
        return this._symptoms;
    }

    public String get_reportedOn(){
        return this._reportedOn;
    }

     /**
     * Convenient method to get the objects data members in ContentValues object.
     * This will be useful for Content Provider operations,
     * which use ContentValues object to represent the data.
     *
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(MedicationDatabaseSQLiteHandler.KEY_ID, _ID);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID, _nric);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PATIENT_NAME, _patientName);
        values.put(MedicationDatabaseSQLiteHandler.KEY_SYMPTOMS, _symptoms);
        values.put(MedicationDatabaseSQLiteHandler.KEY_REPORTED_ON, _reportedOn);
        return values;
    }

    // Create a Symptoms object from a cursor
    public static SymptomsObject fromCursor(Cursor curSymptoms) {
        int id = curSymptoms.getInt(curSymptoms.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ID));
        String nric = curSymptoms.getString(curSymptoms.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID));
        String patientName = curSymptoms.getString(curSymptoms.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PATIENT_NAME));
        String symptoms = curSymptoms.getString(curSymptoms.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_SYMPTOMS));
        String reportedOn = curSymptoms.getString(curSymptoms.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_REPORTED_ON));

        return new SymptomsObject(id, nric, patientName, symptoms, reportedOn);
    }

    @Override
    public int compareTo(SymptomsObject obj) {
        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        Date curObjDate = dateFormat.parse(get_reportedOn(), new ParsePosition(0));
        Date targetObjDate = dateFormat.parse(obj.get_reportedOn(), new ParsePosition(0));

        return curObjDate.compareTo(targetObjDate);
    }
}
