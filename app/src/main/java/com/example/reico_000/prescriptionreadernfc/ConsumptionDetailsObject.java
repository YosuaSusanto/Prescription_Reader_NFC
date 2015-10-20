package com.example.reico_000.prescriptionreadernfc;

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
}
