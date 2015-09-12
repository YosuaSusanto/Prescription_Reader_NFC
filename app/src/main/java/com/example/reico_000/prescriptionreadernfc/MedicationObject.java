package com.example.reico_000.prescriptionreadernfc;

/**
 * Created by reico_000 on 14/3/2015.
 */
public class MedicationObject {

    int _ID = -1;
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

    public int get_ID(){
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

    public void set_ID(int UID){
        this._ID = UID;
    }
}
