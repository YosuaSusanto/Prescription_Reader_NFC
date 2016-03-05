package com.example.reico_000.prescriptionreadernfc;

/**
 * Created by Yosua Susanto on 25/9/2015.
 *
 * Insert the path to the php file
 */

public class AppConfig {
    private static String NGROK_URL = "http://d93597b8.ngrok.io/android_login_api/";
    // Server user login url
    public static String URL_LOGIN = NGROK_URL + "login.php";

    // Server user register url
    public static String URL_REGISTER = NGROK_URL + "register.php";

    // Server populateDB url
    public static String URL_POPULATE_DB = "http://pohhuanyu.com/android_login_api/populateDB.php";

    // Server getNRIC url
    public static String URL_GET_NRIC = "http://pohhuanyu.com/android_login_api/getNRIC.php";

    // Server getPDFFilePath url
    public static String URL_GET_PDF_FILE_PATH = "http://pohhuanyu.com/android_login_api/getPDFFilePath.php";

    // Server blockMedication url
    public static String URL_BLOCK_MEDICATION = "http://pohhuanyu.com/android_login_api/blockMedication.php";

    // Server updateConsumptionTime url
    public static String URL_UPDATE_CONSUMPTION_TIME = "http://pohhuanyu.com/android_login_api/updateConsumptionTime.php";

    // Server updateUntakenConsumptionTime url
    public static String URL_UPDATE_UNTAKEN_CONSUMPTION_TIME = "http://pohhuanyu.com/android_login_api/updateUntakenConsumptionTime.php";

    // Server updateDosage url
    public static String URL_UPDATE_DOSAGE = "http://pohhuanyu.com/android_login_api/updateDosage.php";

    // Server insertConsumption url
    public static String URL_INSERT_CONSUMPTION = "http://pohhuanyu.com/android_login_api/insertConsumption.php";

    // Server updateConsumption url
    public static String URL_UPDATE_CONSUMPTION = "http://pohhuanyu.com/android_login_api/updateConsumption.php";
}