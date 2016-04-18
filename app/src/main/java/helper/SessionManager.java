package helper;

/**
 * Created by Yosua Susanto on 25/9/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;
    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PrescriptionReaderNFCLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_PATIENT_ID = "patientID";
    private static final String KEY_PATIENT_NAME = "patientName";
    private static final String KEY_ACCEPT_TERMS = "isTermsAccepted";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setPatientID(String patientID) {
        editor.putString(KEY_PATIENT_ID, patientID);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setPatientName(String patientName) {
        editor.putString(KEY_PATIENT_NAME, patientName);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setAcceptTerms(boolean isAccepted) {
        editor.putBoolean(KEY_ACCEPT_TERMS, isAccepted);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getPatientID(){
        return pref.getString(KEY_PATIENT_ID, "");
    }

    public String getPatientName(){
        return pref.getString(KEY_PATIENT_NAME, "");
    }

    public boolean isTermsAccepted(){
        return pref.getBoolean(KEY_ACCEPT_TERMS, false);
    }
}
