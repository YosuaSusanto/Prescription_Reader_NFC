package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Yosua Susanto on 18/4/2016.
 */
public class PastMedicationObject {
    int _ID = -1;
    String _genericName = "";
    String _brandName = "";
    String _prescriptionDate = "";
    String _finishedOn = "";

    public PastMedicationObject(){
    }

    public PastMedicationObject(int id, String brandName, String genericName,
                                String prescriptionDate, String finishedOn){
        this._ID = id;
        this._brandName = brandName;
        this._genericName = genericName;
        this._prescriptionDate = prescriptionDate;
        this._finishedOn = finishedOn;
    }

    public int get_id(){
        return this._ID;
    }

    public String get_brandName(){
        return this._brandName;
    }

    public String get_genericName(){
        return this._genericName;
    }

    public String get_prescriptionDate(){
        return this._prescriptionDate;
    }

    public String get_finishedOn(){
        return this._finishedOn;
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
        values.put(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME, _brandName);
        values.put(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME, _genericName);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PRESCRIPTIONDATE, _prescriptionDate);
        values.put(MedicationDatabaseSQLiteHandler.KEY_FINISHED_ON, _finishedOn);
        return values;
    }

    // Create a PastMedicationObject object from a cursor
    public static PastMedicationObject fromCursor(Cursor curPastMedication) {
        int id = curPastMedication.getInt(curPastMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ID));
        String brandName = curPastMedication.getString(curPastMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME));
        String genericName = curPastMedication.getString(curPastMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME));
        String prescriptionDate = curPastMedication.getString(curPastMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PRESCRIPTIONDATE));
        String finishedOn = curPastMedication.getString(curPastMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_FINISHED_ON));

        return new PastMedicationObject(id, brandName, genericName, prescriptionDate, finishedOn);
    }
}
