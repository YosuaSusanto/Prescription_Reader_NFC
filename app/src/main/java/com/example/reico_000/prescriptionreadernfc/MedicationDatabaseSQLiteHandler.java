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

	// Common columns names
	public static final String KEY_ID = "_id";

    // Medications table column names
    public static final String KEY_BRAND_NAME = "BrandName";
    public static final String KEY_GENERIC_NAME = "GenericName";
    public static final String KEY_DOSAGE_FORM = "DosageForm";
    public static final String KEY_PER_DOSAGE = "PerDosage";
    public static final String KEY_TOTAL_DOSAGE = "TotalDosage";
    public static final String KEY_CONSUMPTION_TIME = "ConsumptionTime";
    public static final String KEY_PATIENT_ID = "PatientID";
    public static final String KEY_ADMINISTRATION = "Administration";
    public static final String[] ALL_MED_KEYS = new String[] {KEY_ID, KEY_BRAND_NAME, KEY_GENERIC_NAME, KEY_DOSAGE_FORM,
            KEY_PER_DOSAGE, KEY_TOTAL_DOSAGE, KEY_CONSUMPTION_TIME, KEY_PATIENT_ID, KEY_ADMINISTRATION};

    // Consumptions table column names
    public static final String KEY_MEDICATION_ID = "MedicationID";
    public static final String KEY_CONSUMED_AT = "ConsumedAt";

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
                KEY_BRAND_NAME + " TEXT," + KEY_GENERIC_NAME + " TEXT," + KEY_DOSAGE_FORM + " TEXT," + KEY_PER_DOSAGE + " TEXT, " +
                KEY_TOTAL_DOSAGE + " TEXT, " + KEY_CONSUMPTION_TIME + " TEXT, " + KEY_PATIENT_ID + " TEXT, " +
                KEY_ADMINISTRATION + " TEXT )";
        String CREATE_CONSUMPTION_TABLE = "CREATE TABLE " + TABLE_CONSUMPTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_MEDICATION_ID + " INTEGER," + KEY_CONSUMED_AT + " TEXT )";

        db.execSQL(CREATE_MEDICATION_TABLE);
        db.execSQL(CREATE_CONSUMPTION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSUMPTIONS);
        onCreate(db);
    }

    // Adding new MedicationObject
    public void addMedication(MedicationObject medicationObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BRAND_NAME, medicationObject.get_brandName()); // Brand Name
        values.put(KEY_GENERIC_NAME, medicationObject.get_genericName()); // Generic Name
        values.put(KEY_DOSAGE_FORM, medicationObject.get_dosageForm()); // Dosage Form and Strength
        values.put(KEY_PER_DOSAGE, medicationObject.get_perDosage()); // Pill/Tablet Per Dosage
        values.put(KEY_TOTAL_DOSAGE, medicationObject.get_totalDosage()); // Total Dosage
        values.put(KEY_CONSUMPTION_TIME, medicationObject.get_consumptionTime()); // Time of Consumption
        values.put(KEY_PATIENT_ID, medicationObject.get_patientID()); // Patient's ID
        values.put(KEY_ADMINISTRATION, medicationObject.get_administration()); // Administration Info

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
                MedicationObject medicationObject= new MedicationObject(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getColumnName(8));

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
    public void deleteMedicationObject(Long MedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICATIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(MedId)});
        db.close();
    }

    public void addConsumptionDetails(String patientId, String brandName, String genericName, String consumedAt) {
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

                // Inserting Row
                db.insert(TABLE_CONSUMPTIONS, null, values);
                db.close(); // Closing database connection
            }
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
                        consumptionObject = new ConsumptionDetailsObject(med_id, consumedAt);
                    }
                }
                c2.close();
            }
        }
        c.close();
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
}
