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

    // Server user populateDB url
    public static String URL_POPULATE_DB = "http://pohhuanyu.com/android_login_api/populateDB.php";

    // Server user populateDB url
    public static String URL_BLOCK_MEDICINE = "http://pohhuanyu.com/android_login_api/blockMedicine.php";

    // Server user updateDosage url
    public static String URL_UPDATE_CONSUMPTION_TIME = "http://pohhuanyu.com/android_login_api/updateConsumptionTime.php";

    // Server user updateDosage url
    public static String URL_UPDATE_UNTAKEN_CONSUMPTION_TIME = "http://pohhuanyu.com/android_login_api/updateUntakenConsumptionTime.php";

    // Server user updateDosage url
    public static String URL_UPDATE_DOSAGE = "http://pohhuanyu.com/android_login_api/updateDosage.php";

    // Server user insertConsumption url
    public static String URL_INSERT_CONSUMPTION = "http://pohhuanyu.com/android_login_api/insertConsumption.php";

    // Server user updateConsumption url
    public static String URL_UPDATE_CONSUMPTION = "http://pohhuanyu.com/android_login_api/updateConsumption.php";

}