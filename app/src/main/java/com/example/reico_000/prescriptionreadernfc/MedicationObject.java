package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by reico_000 on 14/3/2015.
 */
public class MedicationObject {

    int _ID = -1;
    String _patientID ="";
    String _brandName = "";
    String _genericName = "";
    String _dosageForm = "";
    String _unit= "";
    String _perDosage = "";
    String _totalDosage = "";
    String _consumptionTime = "";
    String _administration = "";
    String _remarks = "";
    String _prescriptionDate = "";
    String _status = "";

    public MedicationObject(){
    }

    public MedicationObject(String brandName, String genericName){
        this._brandName = brandName;
        this._genericName = genericName;
    }

    public MedicationObject(int id, String brandName, String genericName, String dosageForm,
                            String unit, String perDosage, String totalDosage,
                            String consumptionTime, String patientID, String administration,
                            String remarks, String prescriptionDate, String status){
        this._ID = id;
        this._brandName = brandName;
        this._genericName = genericName;
        this._dosageForm = dosageForm;
        this._unit = unit;
        this._perDosage = perDosage;
        this._totalDosage = totalDosage;
        this._consumptionTime = consumptionTime;
        this._patientID = patientID;
        this._administration = administration;
        this._remarks = remarks;
        this._prescriptionDate = prescriptionDate;
        this._status = status;
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

    public String get_dosageForm(){
        return this._dosageForm;
    }

    public String get_unit(){
        return this._unit;
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

    public String get_remarks(){
        return this._remarks;
    }

    public String get_prescriptionDate(){
        return this._prescriptionDate;
    }

    public String get_status(){
        return this._status;
    }

    public void set_consumptionTime(String newConsumptionTime){
        this._consumptionTime = newConsumptionTime;
    }

    public void set_remarksBlock(){
        this._remarks = "Patient has stopped taking this medicine.";
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
        values.put(MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM, _dosageForm);
        values.put(MedicationDatabaseSQLiteHandler.KEY_UNIT, _unit);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PER_DOSAGE, _perDosage);
        values.put(MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE, _totalDosage);
        values.put(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME, _consumptionTime);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID, _patientID);
        values.put(MedicationDatabaseSQLiteHandler.KEY_ADMINISTRATION, _administration);
        values.put(MedicationDatabaseSQLiteHandler.KEY_REMARKS, _remarks);
        values.put(MedicationDatabaseSQLiteHandler.KEY_PRESCRIPTIONDATE, _prescriptionDate);
        values.put(MedicationDatabaseSQLiteHandler.KEY_STATUS, _status);
        return values;
    }

    // Create a MedicationObject object from a cursor
    public static MedicationObject fromCursor(Cursor curMedication) {
        int id = curMedication.getInt(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ID));
        String brandName = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME));
        String genericName = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME));
        String dosageForm = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM));
        String unit = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_UNIT));
        String perDosage = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PER_DOSAGE));
        String totalDosage = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE));
        String consumptionTime = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME));
        String patientID = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID));
        String administration = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_ADMINISTRATION));
        String remarks = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_REMARKS));
        String prescriptionDate = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_PRESCRIPTIONDATE));
        String status = curMedication.getString(curMedication.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_STATUS));

        return new MedicationObject(id, brandName, genericName, dosageForm, unit, perDosage, totalDosage,
                consumptionTime, patientID, administration, remarks, prescriptionDate, status);
    }
}
