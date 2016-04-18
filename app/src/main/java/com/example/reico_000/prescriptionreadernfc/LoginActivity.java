package com.example.reico_000.prescriptionreadernfc;

/**
 * Created by Yosua Susanto on 25/9/2015.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import helper.SessionManager;
import helper.UserDatabaseSQLiteHandler;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
//    private Button btnLinkToRegister;
    private EditText inputName;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private UserDatabaseSQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputName = (EditText) findViewById(R.id.name);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
//        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new UserDatabaseSQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputName.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!name.isEmpty() && !password.isEmpty()) {
                    String rawString = "";
                    try {
                        rawString = new String(password.getBytes(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    String encryptedPassword = convertStringToHex(encryptPassword(rawString));
                    String encryptedPassword = encryptPassword(rawString);
                    // login user
                    checkLogin(name, encryptedPassword);
//                    checkLogin(name, password);
//                    session.setPatientID("G1159974K");
//                    session.setLogin(true);
//                    // Launch main activity
//                    Intent intent = new Intent(LoginActivity.this,
//                            MainActivity.class);
//                    startActivity(intent);
//                    finish();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

//        // Link to Register Screen
//        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        RegisterActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        Log.d("BackTest", "onBackPressed called!");
        finish();
        this.finishAffinity();
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String name, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
//                        String uid = jObj.getString("uid");

//                        JSONObject user = jObj.getJSONObject("user");
//                        String name = user.getString("name");
//                        String email = user.getString("email");
//                        String created_at = user.getString("created_at");
                        String patient_id = jObj.getString("patient_id");
                        String patient_name = jObj.getString("patient_name");
                        session.setPatientID(patient_id);
                        session.setPatientName(patient_name);
                        // Inserting row in users table
//                        db.addUser(name, email, uid, created_at, patient_id);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getStackTrace());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        VolleyController.getInstance(this).addToRequestQueue(strReq);
//        Volley.newRequestQueue(this).add(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static String encryptPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(password.getBytes());
            byte byteData[] = md.digest();
//            String base64 = Base64.encodeToString(byteData, Base64.DEFAULT);
//            String result = bytesToHex(byteData);
            String result = bytesToHex(byteData);
            return result;
        }
        return password;
    }

    public String convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }

        return hex.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        int len = bytes.length;
        for ( int j = 0; j < len; j++ ) {
            int v = bytes[j] & 0xFF;
            if (j < 64) {
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
        }
        String result = new String(hexChars);
        return result;

//        StringBuffer sb = new StringBuffer();
//        for (byte b : bytes) {
//            sb.append(Integer.toHexString(0xff & b));
//        }
//        return sb.toString();
    }
}
