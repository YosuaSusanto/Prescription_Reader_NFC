package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Yosua Susanto on 16/10/2015.
 */
public class ConsumptionDetailsObject {
    int _ID = -1;
    int _medicationID = -1;
    String _consumedAt = "";

    public ConsumptionDetailsObject(){
    }

    public ConsumptionDetailsObject(int medicationID, String consumedAt){
        this._medicationID = medicationID;
        this._consumedAt = consumedAt;
    }

    public int get_ID() {
        return this._ID;
    }

    public int get_medicationID() {
        return this._medicationID;
    }

    public String get_consumedAt() {
        return this._consumedAt;
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
        values.put(MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID, _medicationID);
        values.put(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT, _consumedAt);
        return values;
    }

    // Create a ConsumptionDetailsObject object from a cursor
    public static ConsumptionDetailsObject fromCursor(Cursor curConsumptionDetails) {
        int medId = curConsumptionDetails.getInt(curConsumptionDetails.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID));
        String consumedAt = curConsumptionDetails.getString(curConsumptionDetails.getColumnIndex(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT));

        return new ConsumptionDetailsObject(medId, consumedAt);
    }

}
