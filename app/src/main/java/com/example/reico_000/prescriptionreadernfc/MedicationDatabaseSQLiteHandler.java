package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reico_000 on 14/3/2015.
 */
public class  MedicationDatabaseSQLiteHandler extends SQLiteOpenHelper{
    private static MedicationDatabaseSQLiteHandler sInstance;

    public static synchronized MedicationDatabaseSQLiteHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MedicationDatabaseSQLiteHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private MedicationDatabaseSQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
    public static final String DATABASE_NAME = "medicationManager";

    // DB Fields
    public static final int COL_ROWID = 0;

	// Contacts table name
    public static final String TABLE_MEDICATIONS = "medicationTable";
    public static final String TABLE_CONSUMPTIONS = "consumptionTable";
    public static final String TABLE_SYMPTOMS = "symptomsTable";
    public static final String TABLE_PAST_MEDICATIONS = "pastMedicationTable";

	// Common columns names
	public static final String KEY_ID = "_id";

    // Medications table column names
    public static final String KEY_BRAND_NAME = "BrandName";
    public static final String KEY_GENERIC_NAME = "GenericName";
    public static final String KEY_DOSAGE_FORM = "DosageForm";
    public static final String KEY_UNIT = "Unit";
    public static final String KEY_PER_DOSAGE = "PerDosage";
    public static final String KEY_TOTAL_DOSAGE = "TotalDosage";
    public static final String KEY_CONSUMPTION_TIME = "ConsumptionTime";
    public static final String KEY_PATIENT_ID = "PatientID";
    public static final String KEY_ADMINISTRATION = "Administration";
    public static final String KEY_REMARKS = "Remarks";
    public static final String KEY_PRESCRIPTIONDATE = "PrescriptionDate";
    public static final String KEY_STATUS = "Status";
    public static final String[] ALL_MED_KEYS = new String[] {KEY_ID, KEY_BRAND_NAME,
            KEY_GENERIC_NAME, KEY_DOSAGE_FORM, KEY_UNIT, KEY_PER_DOSAGE, KEY_TOTAL_DOSAGE,
            KEY_CONSUMPTION_TIME, KEY_PATIENT_ID, KEY_ADMINISTRATION, KEY_REMARKS,
            KEY_PRESCRIPTIONDATE, KEY_STATUS};

    // Consumptions table column names
    public static final String KEY_MEDICATION_ID = "MedicationID";
    public static final String KEY_CONSUMED_AT = "ConsumedAt";
    public static final String KEY_IS_TAKEN = "IsTaken";
    public static final String KEY_REMAINING_DOSAGE = "RemainingDosage";

    // Symptoms table column names
    public static final String KEY_PATIENT_NAME = "PatientName";
    public static final String KEY_SYMPTOMS = "Symptoms";
    public static final String KEY_REPORTED_ON = "ReportedOn";

    // Consumptions table column names
    public static final String KEY_FINISHED_ON = "FinishedOn";

    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_BRANDNAME = 1;
    public static final int COL_GENERICNAME = 2;
    public static final int COL_DOSAGEFORM = 3;
    public static final int COL_PERDOSAGE = 4;
    public static final int COL_TOTALDOSAGE = 5;
    public static final int COL_CONSUMPTIONTIME = 6;
    public static final int COL_PATIENTID = 7;
    public static final int COL_ADMINISTRATION = 8;

	@Override
	public void onCreate(SQLiteDatabase db) {
        String CREATE_MEDICATION_TABLE = "CREATE TABLE " + TABLE_MEDICATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_BRAND_NAME + " TEXT, " + KEY_GENERIC_NAME + " TEXT, " + KEY_DOSAGE_FORM + " TEXT, " +
                KEY_PER_DOSAGE + " TEXT, " + KEY_UNIT + " TEXT, " + KEY_TOTAL_DOSAGE + " TEXT, " +
                KEY_CONSUMPTION_TIME + " TEXT, " + KEY_PATIENT_ID + " TEXT, " + KEY_ADMINISTRATION + " TEXT, " +
                KEY_REMARKS + " TEXT, " + KEY_PRESCRIPTIONDATE + " TEXT, " + KEY_STATUS + " TEXT )";
        String CREATE_CONSUMPTION_TABLE = "CREATE TABLE " + TABLE_CONSUMPTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_MEDICATION_ID + " INTEGER," + KEY_CONSUMED_AT + " TEXT, " + KEY_IS_TAKEN + " TEXT, " + KEY_REMAINING_DOSAGE +" TEXT )";
        String CREATE_SYMPTOMS_TABLE = "CREATE TABLE " + TABLE_SYMPTOMS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_PATIENT_ID + " TEXT," + KEY_PATIENT_NAME + " TEXT, " + KEY_SYMPTOMS + " TEXT, " + KEY_REPORTED_ON +" TEXT )";
        String CREATE_PAST_MEDICATION_TABLE = "CREATE TABLE " + TABLE_PAST_MEDICATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_PATIENT_ID + " TEXT," + KEY_BRAND_NAME + " TEXT, " + KEY_GENERIC_NAME + " TEXT, " + KEY_FINISHED_ON + " TEXT, " +
                KEY_PRESCRIPTIONDATE + " TEXT )";

        db.execSQL(CREATE_MEDICATION_TABLE);
        db.execSQL(CREATE_CONSUMPTION_TABLE);
        db.execSQL(CREATE_SYMPTOMS_TABLE);
        db.execSQL(CREATE_PAST_MEDICATION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSUMPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYMPTOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAST_MEDICATIONS);
        onCreate(db);
    }

    // Adding new MedicationObject
    public void addMedication(MedicationObject medicationObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BRAND_NAME, medicationObject.get_brandName()); // Brand Name
        values.put(KEY_GENERIC_NAME, medicationObject.get_genericName()); // Generic Name
        values.put(KEY_DOSAGE_FORM, medicationObject.get_dosageForm()); // Dosage Form and Strength
        values.put(KEY_UNIT, medicationObject.get_unit()); // Dosage Form and Strength
        values.put(KEY_PER_DOSAGE, medicationObject.get_perDosage()); // Pill/Tablet Per Dosage
        values.put(KEY_TOTAL_DOSAGE, medicationObject.get_totalDosage()); // Total Dosage
        values.put(KEY_CONSUMPTION_TIME, medicationObject.get_consumptionTime()); // Time of Consumption
        values.put(KEY_PATIENT_ID, medicationObject.get_patientID()); // Patient's ID
        values.put(KEY_ADMINISTRATION, medicationObject.get_administration()); // Administration Info
        values.put(KEY_REMARKS, medicationObject.get_remarks()); // Remarks
        values.put(KEY_PRESCRIPTIONDATE, medicationObject.get_prescriptionDate()); // Prescription date
        values.put(KEY_STATUS, medicationObject.get_status()); // Med status

        // Inserting Row
        db.insert(TABLE_MEDICATIONS, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Medications
    public List<MedicationObject> getAllMedications() {
        List<MedicationObject> medicationList = new ArrayList<MedicationObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MedicationObject medicationObject= new MedicationObject(cursor.getInt(0),
                        cursor.getString(1),cursor.getString(2),cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12));

                //Do I need to put in UID?
//                medicationObject.set_ID(Integer.parseInt(cursor.getString(0)));
                // Adding contact to list
                medicationList.add(medicationObject);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return medicationList;
    }

    public Cursor getAllRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            Log.d("Test Fragment Cursor", "it ain't null");
            c.moveToFirst();
        } else {
            Log.d("Test Fragment Cursor", "it's null bitch");
        }
        return c;
    }

    public Cursor getRowsByPatientId(String scan_PatientId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_PATIENT_ID + " = '" + scan_PatientId + "'";

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            Log.d("Test Fragment Cursor", "it ain't null");
            c.moveToFirst();
        } else {
            Log.d("Test Fragment Cursor", "it's null");
        }
        return c;
    }

    public Cursor getRow(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_ID + "=" + rowId;
        Cursor c = 	db.query(true, TABLE_MEDICATIONS, ALL_MED_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getByNameAndDosageForm(String scan_BrandName, String scan_DosageForm){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_BRAND_NAME + " = '" + scan_BrandName + "' AND " + KEY_DOSAGE_FORM + " = '" + scan_DosageForm + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public int getTotalDosage(String scan_BrandName, String scan_DosageForm){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_BRAND_NAME + " = '" + scan_BrandName + "' AND " + KEY_DOSAGE_FORM + " = '" + scan_DosageForm + "'";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            return c.getInt(c.getColumnIndex("TotalDosage"));//MedicationDatabaseSQLiteHandler.COL_TOTALDOSAGE);
        } else{
            return 0;
        }

    }

    public boolean updateRow(long rowId, int saved_TotalDosage) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_ID + "=" + rowId;
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TOTAL_DOSAGE, saved_TotalDosage);

        Log.d("update Row", "Total Dosage saved = " + saved_TotalDosage);
        // Insert it into the database.
        return db.update(TABLE_MEDICATIONS, newValues, where, null) != 0;
    }

    // Getting MedObjects Count
    public int getMedObjectsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MEDICATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Deleting single MedObject
    public void deleteMedicationObject(int MedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICATIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(MedId)});
        db.close();
    }

    public void addConsumptionDetails(String patientId, String brandName, String genericName, String consumedAt, String remainingDosage) {
        SQLiteDatabase db = this.getWritableDatabase();
                String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_BRAND_NAME + " = '" + brandName + "' AND " +
                        KEY_GENERIC_NAME + " = '" + genericName + "' AND " + KEY_PATIENT_ID + " = '" + patientId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            if (c.moveToFirst()) {
                int colIndex = c.getColumnIndex(KEY_ID);
                int med_id = c.getInt(colIndex);
//        int med_id = c.getInt(0);
                ContentValues values = new ContentValues();
                values.put(KEY_MEDICATION_ID, med_id);
                values.put(KEY_CONSUMED_AT, consumedAt);
                values.put(KEY_REMAINING_DOSAGE, remainingDosage);

                // Inserting Row
                db.insert(TABLE_CONSUMPTIONS, null, values);
                db.close(); // Closing database connection
            }
            c.close();
        }
    }

    public ConsumptionDetailsObject getConsumptionDetails(String brandName, String genericName, String patientId) {
        int med_id = 0;
        ConsumptionDetailsObject consumptionObject = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = KEY_BRAND_NAME + " = ? AND " + KEY_GENERIC_NAME + " = ? AND " + KEY_PATIENT_ID + " = ?";
        String[] selectionArgs = new String[]{brandName, genericName, patientId};
        String addQuery = "SELECT * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_BRAND_NAME + " = '" + brandName + "' AND " +
                KEY_GENERIC_NAME + " = '" + genericName + "' AND " + KEY_PATIENT_ID + " = '" + patientId + "'";
//        Cursor c = db.rawQuery(addQuery, null);
        Cursor c = db.query(TABLE_MEDICATIONS, ALL_MED_KEYS, selection, selectionArgs, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                med_id = c.getInt(c.getColumnIndex(KEY_ID));
                String getQuery = "SELECT * FROM " + TABLE_CONSUMPTIONS + " WHERE " + KEY_MEDICATION_ID + " = " + med_id;
                Cursor c2 = db.rawQuery(getQuery, null);
                if (c2 != null) {
                    if (c2.moveToFirst()) {
                        String consumedAt = c2.getString(c2.getColumnIndex("ConsumedAt"));
                        String isTaken = c2.getString(c2.getColumnIndex("IsTaken"));
                        String remainingDosage = c2.getString(c2.getColumnIndex("RemainingDosage"));
                        consumptionObject = new ConsumptionDetailsObject(med_id, consumedAt, isTaken, remainingDosage);
                    }
                    c2.close();
                }
            }
            c.close();
        }
        db.close(); // Closing database connection

        return consumptionObject;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String[] dbfields, String[] fieldValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TableName + " WHERE ";
        for (int i = 0; i < dbfields.length; i++) {
            Query += dbfields[i] + " = '" + fieldValues[i] + "'";
            if (i < dbfields.length - 1) {
                Query += " AND ";
            }
        }
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void deleteNonExistingMedication(List<Integer> onlineIdList, String patientId) {
        Log.d("deleteMed", "Deleting nonexistent medication");
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + MedicationDatabaseSQLiteHandler.TABLE_MEDICATIONS + " where "
                + MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID + " = '" + patientId + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
        } else {
            while (cursor.moveToNext()) {
                Integer medId = cursor.getInt(0);
                Log.d("deleteMed", "current medId: " + medId);

                if (!onlineIdList.contains(medId)) {
                    deleteMedicationObject(medId);
                }
            }
//            while (cursor.moveToNext());
            cursor.close();
        }
    }
}
