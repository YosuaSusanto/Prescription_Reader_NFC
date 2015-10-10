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
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
    public static final String DATABASE_NAME = "medicationManager";

    // DB Fields
    public static final int COL_ROWID = 0;

	// Contacts table name
    public static final String TABLE_MEDICATIONS = "medicationTable";

	// Contacts Table Columns names
	public static final String KEY_ID = "_id";
    public static final String KEY_BRANDNAME = "BrandName";
    public static final String KEY_GENERICNAME = "GenericName";
    public static final String KEY_DOSAGEFORM = "DosageForm";
    public static final String KEY_PERDOSAGE = "PerDosage";
    public static final String KEY_TOTALDOSAGE = "TotalDosage";
    public static final String KEY_CONSUMPTIONTIME = "ConsumptionTime";
    public static final String KEY_PATIENTID = "PatientID";
    public static final String KEY_ADMINISTRATION = "Administration";
    public static final String[] ALL_KEYS = new String[] {KEY_ID, KEY_BRANDNAME, KEY_GENERICNAME, KEY_DOSAGEFORM, KEY_PERDOSAGE, KEY_TOTALDOSAGE,
            KEY_CONSUMPTIONTIME, KEY_PATIENTID, KEY_ADMINISTRATION};

    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_BRANDNAME = 1;
    public static final int COL_GENERICNAME = 2;
    public static final int COL_DOSAGEFORM = 3;
    public static final int COL_PERDOSAGE = 4;
    public static final int COL_TOTALDOSAGE = 5;
    public static final int COL_CONSUMPTIONTIME = 6;
    public static final int COL_PATIENTID = 7;
    public static final int COL_ADMINISTRATION = 8;

	public MedicationDatabaseSQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MEDICATION_TABLE = "CREATE TABLE " + TABLE_MEDICATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_BRANDNAME + " TEXT," + KEY_GENERICNAME + " TEXT," + KEY_DOSAGEFORM + " TEXT," + KEY_PERDOSAGE + " TEXT, " + KEY_TOTALDOSAGE + " TEXT, " + KEY_CONSUMPTIONTIME + " TEXT, " + KEY_PATIENTID + " TEXT, " + KEY_ADMINISTRATION + " TEXT )";
		db.execSQL(CREATE_MEDICATION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        onCreate(db);
    }
		// Adding new MedicationObject
		public void addMedication(MedicationObject medicationObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BRANDNAME, medicationObject.get_brandName()); // Brand Name
        values.put(KEY_GENERICNAME, medicationObject.get_genericName()); // Generic Name
        values.put(KEY_DOSAGEFORM, medicationObject.get_dosageForm()); // Dosage Form and Strength
        values.put(KEY_PERDOSAGE, medicationObject.get_perDosage()); // Pill/Tablet Per Dosage
        values.put(KEY_TOTALDOSAGE, medicationObject.get_totalDosage()); // Total Dosage
        values.put(KEY_CONSUMPTIONTIME, medicationObject.get_consumptionTime()); // Time of Consumption
        values.put(KEY_PATIENTID, medicationObject.get_patientID()); // Patient's ID
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
                    MedicationObject medicationObject= new MedicationObject(cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getColumnName(8));

                    //Do I need to put in UID?
                    medicationObject.set_ID(Integer.parseInt(cursor.getString(0)));
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
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_PATIENTID + " = '" + scan_PatientId + "'";

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
        Cursor c = 	db.query(true, TABLE_MEDICATIONS, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getByNameAndDosageForm(String scan_BrandName, String scan_DosageForm){
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_BRANDNAME + " = '" + scan_BrandName + "' AND " + KEY_DOSAGEFORM + " = '" + scan_DosageForm + "'";
        Cursor c = db.rawQuery(countQuery, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public int getTotalDosage(String scan_BrandName, String scan_DosageForm){
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_BRANDNAME + " = '" + scan_BrandName + "' AND " + KEY_DOSAGEFORM + " = '" + scan_DosageForm + "'";
        Cursor c = db.rawQuery(countQuery, null);

        if (c != null) {
            return c.getInt(MedicationDatabaseSQLiteHandler.COL_TOTALDOSAGE);
        } else{
            return 0;
        }

    }

    public boolean updateRow(long rowId, int saved_TotalDosage) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_ID + "=" + rowId;
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TOTALDOSAGE, saved_TotalDosage);

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
                    new String[] { String.valueOf(MedId) });
            db.close();
        }
}
