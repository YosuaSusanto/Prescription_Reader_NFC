package com.example.reico_000.prescriptionreadernfc;

/**
 * Created by Yosua Susanto on 25/9/2015.
 *
 * Insert the path to the php file
 */

public class AppConfig {
    private static String NGROK_URL = "https://d93597b8.ngrok.io/android_login_api/";
    // Server user login url
    public static String URL_LOGIN = "https://onco-informatics.com/medfc/android_login_api/login.php";

    // Server user register url
    public static String URL_REGISTER = "https://onco-informatics.com/medfc/android_login_api/register.php";

    // Server populateMedDB url
    public static String URL_POPULATE_DB = "https://onco-informatics.com/medfc/android_login_api/populateMedDB.php";

    // Server populateDB url
    public static String URL_POPULATE_SYMPTOMS_DB = "https://onco-informatics.com/medfc/android_login_api/populateSymptomsDB.php";

    // Server getNRIC url
    public static String URL_GET_NRIC = "https://onco-informatics.com/medfc/android_login_api/getNRIC.php";

    // Server getPDFFilePath url
    public static String URL_GET_PDF_FILE_PATH = "https://onco-informatics.com/medfc/android_login_api/getPDFFilePath.php";

    // Server blockMedication url
    public static String URL_BLOCK_MEDICATION = "https://onco-informatics.com/medfc/android_login_api/blockMedication.php";

    // updateRemarks url
    public static String URL_UPDATE_REMARKS = "https://onco-informatics.com/medfc/android_login_api/updateRemarks.php";

    // Server updateConsumptionTime url
    public static String URL_UPDATE_CONSUMPTION_TIME = "https://onco-informatics.com/medfc/android_login_api/updateConsumptionTime.php";

    // Server updateUntakenConsumptionTime url
    public static String URL_UPDATE_UNTAKEN_CONSUMPTION_TIME = "https://onco-informatics.com/medfc/android_login_api/updateUntakenConsumptionTime.php";

    // Server updateDosage url
    public static String URL_UPDATE_DOSAGE = "https://onco-informatics.com/medfc/android_login_api/updateDosage.php";

    // Server insertConsumption url
    public static String URL_INSERT_CONSUMPTION = "https://onco-informatics.com/medfc/android_login_api/insertConsumption.php";

    // Server updateConsumption url
    public static String URL_UPDATE_CONSUMPTION = "https://onco-informatics.com/medfc/android_login_api/updateConsumption.php";

    // Server insertSymptoms url
    public static String URL_INSERT_SYMPTOMS = "https://onco-informatics.com/medfc/android_login_api/insertSymptoms.php";
}