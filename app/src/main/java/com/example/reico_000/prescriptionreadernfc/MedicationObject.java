package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by reico_000 on 14/3/2015.
 */
public class MedicationObject {

    String _patientID ="";
    String _brandName = "";
    String _genericName = "";
    String _dosageForm = "";
    String _perDosage = "";
    String _totalDosage = "";
    String _consumptionTime = "";
    String _administration = "";

    public MedicationObject(){
    }

    public MedicationObject(String brandName, String genericName){
        this._brandName = brandName;
        this._genericName = genericName;
    }

    public MedicationObject(String brandName, String genericName, String dosageForm, String perDosage, String totalDosage, String consumptionTime, String patientID, String administration){
        this._brandName = brandName;
        this._genericName = genericName;
        this._dosageForm = dosageForm;
        this._perDosage = perDosage;
        this._totalDosage = totalDosage;
        this._consumptionTime = consumptionTime;
        this._patientID = patientID;
        this._administration = administration;
    }

    public String get_brandName(){
        return this._brandName;
    }

    public String get_genericName(){
        return this._genericName;
    }

    public String get_dosageForm(){
        return this._dosageForm;
    }

    public String get_perDosage(){
        return this._perDosage;
    }

    public String get_totalDosage(){
        return this._totalDosage;
    }

    public String get_consumptionTime(){
        return this._consumptionTime;
    }

    public String get_patientID(){
        return this._patientID;
    }

    public String get_administration(){
        return this._administration;
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
        values.put(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME, _brandName);
        values.put(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME, _genericName);
        values.put(MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM, _dosageForm);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PER_DOSAGE, _perDosage);
        values.put(MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE, _totalDosage);
        values.put(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME, _consumptionTime);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID, _patientID);
        values.put(MedicationDatabaseSQLiteHandler.KEY_ADMINISTRATION, _administration);
        return values;
    }

    // Create a MedicationObject object from a cursor
    public static MedicationObject fromCursor(Cursor curMedication) {
        String brandName = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME));
        String genericName = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME));
        String dosageForm = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM));
        String perDosage = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PER_DOSAGE));
        String totalDosage = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE));
        String consumptionTime = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME));
        String patientID = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID));
        String administration = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ADMINISTRATION));

        return new MedicationObject(brandName, genericName, dosageForm, perDosage, totalDosage, consumptionTime, patientID, administration);
    }
}
