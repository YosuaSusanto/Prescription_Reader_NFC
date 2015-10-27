package com.example.reico_000.prescriptionreadernfc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helper.SessionManager;


public class MainActivity extends FragmentActivity
        implements Scan.OnFragmentInteractionListener,Inventory.OnFragmentInteractionListener,
        Monitoring.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks, Communicator {

    private boolean useNFC = false;
    public String BrandName = "";
    public String GenericName = "";
    public String DosageForm = "";
    public String PerDosage = "";
    public String TotalDosage = "";
    public String ConsumptionTime = "";
    public String PatientID = "";
    public String Administration = "";
    private PendingIntent pendingIntent;
    private PendingIntent pendingIntentAlarm;
    private MedicationDatabaseSQLiteHandler medicationDBHandler;
    private Connection connectionSQL = null;

    private SessionManager session;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Scan mScanFragment;
    private Inventory inventoryFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;

    private AlarmManager manager;

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = MedicationContract.AUTHORITY;
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "prescriptionreadernfc.account";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(this);
        session  = new SessionManager(getApplicationContext());

        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        PatientID = session.getPatientID();
        if (PatientID == "") {
            // Launch main activity
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (useNFC) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if (mNfcAdapter == null) {
                // Stop here, we definitely need NFC
                Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
            }
        }

        handleIntent(getIntent());
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.e(TAG, "addAccountExplicitly fails, account might have already exists...");
            return null;
        }
        return newAccount;
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (!useNFC) {
            mNavigationDrawerFragment.closeDrawer();
            Fragment fragment = new Scan();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, "scanFragment")
                    .commit();
            mNavigationDrawerFragment.selectItem(0);
            String type = intent.getType();
        } else {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

                mNavigationDrawerFragment.closeDrawer();
                Fragment fragment = new Scan();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, "scanFragment")
                        .commit();
                mNavigationDrawerFragment.selectItem(0);

                Toast.makeText(this, "Tag discovered.", Toast.LENGTH_LONG).show();
                String type = intent.getType();
                if (MIME_TEXT_PLAIN.equals(type)) {

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    new NdefReaderTask().execute(tag);

                } else {
                    Log.d(TAG, "Wrong mime type: " + type);
                }
            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

                // In case we would still use the Tech Discovered Intent
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = tag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        new NdefReaderTask().execute(tag);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        if (useNFC) {
            setupForegroundDispatch(this, mNfcAdapter);
        }

        if (session.isLoggedIn()) {
            populateLocalDB(PatientID);
        }

//        getContentResolver().requestSync(mAccount, MedicationContract.AUTHORITY, extras);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if (useNFC) {
            stopForegroundDispatch(this, mNfcAdapter);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link /*BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            if (ndef.getCachedNdefMessage() == null) {
                Toast.makeText(MainActivity.this, "The tag is empty ! ", Toast.LENGTH_SHORT).show();
                return null;
            }


            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        BrandName = readText(records[0]);
                        GenericName = readText(records[1]);
                        DosageForm = readText(records[2]);
                        PerDosage = readText(records[3]);
                        TotalDosage = readText(records[4]);
                        ConsumptionTime = readText(records[5]);
                        //PatientID = readText(records[6]);
                        Administration = readText(records[7]);

                        return BrandName;
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding;
            if ((payload[0] & 128) == 0)
                textEncoding = "UTF-8";
            else
                textEncoding = "UTF-16";
            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                FragmentManager manager = getFragmentManager();

                mScanFragment = (Scan) manager.findFragmentByTag("scanFragment");

                if (mScanFragment != null) {
                    Log.d("debug", "fragment is not null");
                    mScanFragment.changeText(BrandName, GenericName, DosageForm, PerDosage, TotalDosage, ConsumptionTime);
                } else {
                    Log.e("DEBUG", "fragment is NULL");
                }

            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            default:
            case 0:
                fragment = new Scan();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, "scanFragment")
                        .commit();
                respondReset();
                break;
            case 1:
                fragment = new Inventory();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, "invFragment")
                        .commit();

                populateListViewfromdb();
                break;
            case 2:
                fragment = new Monitoring();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, "moniFragment")
                        .commit();
                break;
        }
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                Log.i("MainActivity", "Fragment Switch Title: " + mTitle);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                Log.i("MainActivity", "Fragment Switch Title: " + mTitle);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                Log.i("MainActivity", "Fragment Switch Title: " + mTitle);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            Log.i("MyActivity", "Fragment Title " + mTitle);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_scanItem1) {
            scanItemOne();
            return true;
        } else if (id == R.id.action_scanItem2) {
            scanItemTwo();
            return true;
        } else if (id == R.id.action_getConsumption1) {
            getConsumption(1);
            return true;
        } else if (id == R.id.action_getConsumption2) {
            getConsumption(2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        new AlertDialog.Builder(this)
            .setTitle("Logout Confirmation")
            .setMessage("Do you really want to logout?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    session.setLogin(false);
                    session.setPatientID("");
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            })
            .setNegativeButton(android.R.string.no, null).show();
    }

    ////////// FOR DEBUGGING PURPOSE ///////////
    private void scanItemOne() {
        BrandName = "Tykerb";
        GenericName = "Laptinib";
        DosageForm = "250 mg Pill";
        PerDosage = "5";
        TotalDosage = "105";
        ConsumptionTime = "M";
        //Should the tag include patientID?? Why do we need a unique prescription for each patient?
//        PatientID = "43462553";
        Administration = "Should be taken on an empty stomach: Take at least 1 hr before or 1 hr after a meal. " +
                "Do not eat/drink grapefruit products.";
        updateScanFragment();
    }

    private void scanItemTwo() {
        BrandName = "Capecitabine";
        GenericName = "Xeloda";
        DosageForm = "500 mg Pill";
        PerDosage = "5";
        TotalDosage = "28";
        ConsumptionTime = "ME";
        //Should the tag include patientID?? Why do we need a unique prescription for each patient?
        //PatientID = "43462553";
        Administration = "Should be taken with food: Take w/in ½ hr after meals.";
        updateScanFragment();
    }

    private void getConsumption(int i) {
        if (i == 1) {
            BrandName = "Tykerb";
            GenericName = "Laptinib";
        } else if (i == 2) {
            BrandName = "Capecitabine";
            GenericName = "Xeloda";
        }
        ConsumptionDetailsObject consumptionObject = medicationDBHandler.getConsumptionDetails(BrandName, GenericName, PatientID);
        if (consumptionObject != null){
            DosageForm = Integer.toString(consumptionObject.get_medicationID());
            PerDosage = consumptionObject.get_consumedAt();
        } else {
            BrandName = "";
            GenericName = "";
            DosageForm = "";
            PerDosage = "";
        }
        TotalDosage = "";
        ConsumptionTime = "";
        Administration = "";
        updateScanFragment();
    }

    private void updateScanFragment() {
        FragmentManager manager = getFragmentManager();

//        Can't be implemented, commit will only be executed when the main thread is ready
//        Fragment fragment = new Scan();
//        manager.beginTransaction()
//                .replace(R.id.container, fragment, "scanFragment")
//                .commit();
//        manager.executePendingTransactions();
//        mNavigationDrawerFragment.selectItem(0);

        mScanFragment = (Scan) manager.findFragmentByTag("scanFragment");

        if (mScanFragment != null) {
            Log.d("debug", "fragment is not null");
            mScanFragment.changeText(BrandName, GenericName, DosageForm, PerDosage, TotalDosage, ConsumptionTime);
        } else {
            Log.e("DEBUG", "fragment is NULL");
        }
    }
    //////////////////////////////////////

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.scan_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
//====================================================================
// Communicator Functions
//====================================================================

    public void respondConsumeMed() {
        Log.d("Respond", "ConsumeMedTest Works");

        if ((!BrandName.equals("")) && (!DosageForm.equals(""))) {
            ContentResolver resolver = getContentResolver();
            Uri uri = MedicationContract.Medications.CONTENT_URI;
            String[] projection = new String[]{medicationDBHandler.KEY_ID, medicationDBHandler.KEY_TOTAL_DOSAGE,
                    medicationDBHandler.KEY_BRAND_NAME, medicationDBHandler.KEY_DOSAGE_FORM};
            String selection = medicationDBHandler.KEY_BRAND_NAME + " = ? AND " + medicationDBHandler.KEY_DOSAGE_FORM + " = ?";
            String[] selectionArgs = new String[]{BrandName, DosageForm};
            Cursor cursor =
                    resolver.query(uri,
                            projection,
                            selection,
                            selectionArgs,
                            null);
            if (cursor != null) {
                long saved_Id = -1;
                int saved_TotalDosage = -1;
                if (cursor.moveToFirst()) {
                    saved_Id = cursor.getLong(cursor.getColumnIndex("_id"));//MedicationDatabaseSQLiteHandler.COL_ROWID);
                    saved_TotalDosage = cursor.getInt(cursor.getColumnIndex("TotalDosage"));//MedicationDatabaseSQLiteHandler.COL_TOTALDOSAGE);
                }
                cursor.close();
                Log.e("Consume Func: ", "saved_ID = " + saved_Id);
                Toast.makeText(this, "saved_ID = " + saved_Id, Toast.LENGTH_SHORT);
                int effectiveTotalDosage = saved_TotalDosage - Integer.parseInt(PerDosage);
                if (saved_Id > -1) {
                    ContentValues values = new ContentValues();
                    values.put(medicationDBHandler.KEY_MEDICATION_ID, saved_Id);
                    values.put(medicationDBHandler.KEY_CONSUMED_AT, getCurrentTimeStamp());
                    resolver.insert(MedicationContract.Consumption.CONTENT_URI, values);

                    String where = medicationDBHandler.KEY_ID + " = " + saved_Id;
                    values.clear();
                    values.put(medicationDBHandler.KEY_TOTAL_DOSAGE, effectiveTotalDosage);
                    uri = ContentUris.withAppendedId(MedicationContract.Medications.CONTENT_URI, saved_Id);
//                    uri = ContentUris.withAppendedId(Uri.parse(MedicationContract.Medications.CONTENT_TYPE), saved_Id);
                    long noUpdated = resolver.update(uri, values, where, null);
//                    medicationDBHandler.updateRow(saved_Id, effectiveTotalDosage);

                    // Update online database with updated effectiveTotalDosage
//                    try{
//                            connectionSQL = SQServerConnection.dbConnector();
//                            String query = "INSERT INTO [NFCMedFeedback].[dbo].[NFCMedicationFeedback] (PatientID, PhoneConsumptionTime, " +
//                                    "BrandName, GenericName, RemainingDosage)\n" + "VALUES ( '"+ PatientID+ "' , '"+ getCurrentTimeStamp() +
//                                    "' , '" + BrandName + "' , '"+ GenericName + "' , " + effectiveTotalDosage +" );";
//                        Log.d("Online Server", "Update String: "+ query);
//                        PreparedStatement pst = connectionSQL.prepareStatement(query);
//                            if (pst == null) {
//                                Log.e("respondConsumeMed", "pst is null!");
//                            } else {
//                                Log.d("respondConsumeMed", "pst not null leh");
//                                pst.execute();
//                                pst.close();
//                            }
//                    } catch (Exception ex){
//                        ex.printStackTrace();
//                    }

                    // If effectiveTotal Dosage is <1, delete medicine
                    // else just notify user consumption successful
                    if (effectiveTotalDosage < 1) {
                        long noDeleted = resolver.delete
                                (MedicationContract.Medications.CONTENT_URI,
                                        medicationDBHandler.KEY_ID + " = ? ",
                                        new String[]{String.valueOf(saved_Id)});
                        new AlertDialog.Builder(this)
                                .setTitle("Medication Course Completed")
                                .setMessage("Medication Consumption Completed\n" +
                                        "Medication Deleted from Inventory")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                         new AlertDialog.Builder(this)
                                .setTitle("Consumption Successful!")
                                .setMessage("Medication Consumed!\n" + effectiveTotalDosage + "pills/tablets to go")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }



                    Log.d("Consume Func:", "Row Updated");
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Error!")
                            .setMessage("Medication does not exist in Inventory")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
            cursor.close();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Error: Please Scan Medication")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    public void respondSaveMedication() {
        Log.d("Insert: ", "Saved Medication Inserting ..");
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(this);
        if (!BrandName.equals("")) {
            ContentResolver resolver = getContentResolver();
            Uri uri = MedicationContract.Medications.CONTENT_URI;
            String[] projection = new String[]{medicationDBHandler.KEY_ID, medicationDBHandler.KEY_TOTAL_DOSAGE,
                    medicationDBHandler.KEY_BRAND_NAME, medicationDBHandler.KEY_DOSAGE_FORM};
            String selection = medicationDBHandler.KEY_BRAND_NAME + " = ? AND " + medicationDBHandler.KEY_DOSAGE_FORM + " = ?";
            String[] selectionArgs = new String[]{BrandName, DosageForm};
            Cursor cursor =
                    resolver.query(uri,
                            projection,
                            selection,
                            selectionArgs,
                            null);
            long saved_Id = -1;
            int saved_TotalDosage = -1;

            if (cursor.moveToFirst()) {
                saved_Id = cursor.getLong(cursor.getColumnIndex("_id"));//MedicationDatabaseSQLiteHandler.COL_ROWID);
                saved_TotalDosage = cursor.getInt(cursor.getColumnIndex("TotalDosage"));//MedicationDatabaseSQLiteHandler.COL_TOTALDOSAGE);
            }
            cursor.close();
            ContentValues values = new ContentValues();
            if (saved_Id > -1) {
                int effectiveTotalDosage = saved_TotalDosage + Integer.parseInt(TotalDosage);
                values.clear();
                String where = medicationDBHandler.KEY_ID + " = " + saved_Id;
                values.put(medicationDBHandler.KEY_TOTAL_DOSAGE, effectiveTotalDosage);
                uri = ContentUris.withAppendedId(MedicationContract.Medications.CONTENT_URI, saved_Id);
                long noUpdated = resolver.update(uri, values, where, null);
                new AlertDialog.Builder(this)
                        .setTitle("Additional Dosages added")
                        .setMessage("Medication Exists in Inventory.\nAdditional Dosages added!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                values.clear();
                values.put(medicationDBHandler.KEY_BRAND_NAME, BrandName);
                values.put(medicationDBHandler.KEY_GENERIC_NAME, GenericName);
                values.put(medicationDBHandler.KEY_DOSAGE_FORM, DosageForm);
                values.put(medicationDBHandler.KEY_PER_DOSAGE, PerDosage);
                values.put(medicationDBHandler.KEY_TOTAL_DOSAGE, TotalDosage);
                values.put(medicationDBHandler.KEY_CONSUMPTION_TIME, ConsumptionTime);
                values.put(medicationDBHandler.KEY_PATIENT_ID, PatientID);
                values.put(medicationDBHandler.KEY_ADMINISTRATION, Administration);
                resolver.insert(MedicationContract.Medications.CONTENT_URI, values);

                Log.d("Insert: ", "Saved Medication Successful!");
                new AlertDialog.Builder(this)
                        .setTitle("New Medication")
                        .setMessage("New Medication added to Inventory!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            respondReset();
        } else {
            Log.e("Insert: ", "Saved Medication Failed/BrandName not set");
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Saved Medication Failed/ Save Medication again")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void respondReset() {
        BrandName = "";
        GenericName = "";
        DosageForm = "";
        PerDosage = "";
        TotalDosage = "";
        ConsumptionTime = "";
        Administration = "";
    }

    /**
     * Populate local db with medication data from server
     * */
    private void populateLocalDB(final String patientID) {
        // Tag used to cancel the request
//        String tag_string_req = "req_login";
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(this);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Fetching data from server...");
        pDialog.setCancelable(false);
        pDialog.show();

//        pDialog.setMessage("Logging in ...");
//        showDialog();
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(this);

//        JsonArrayRequest req = new JsonArrayRequest()
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_POPULATE_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        try {
                            ContentResolver resolver = getContentResolver();
                            ContentValues values = new ContentValues();

                            // Parsing json array response
                            // loop through each json object
//                            jsonResponse = "";
                            JSONArray jArr = new JSONArray(response);
                            for (int i = 0; i < jArr.length(); i++) {

                                JSONObject medication = (JSONObject) jArr.get(i);

                                int id = medication.getInt("id");
                                String brand_name = medication.getString("brand_name");
                                String generic_name = medication.getString("generic_name");
                                String dosage_form = medication.getString("dosage_form");
                                String per_dosage = medication.getString("per_dosage");
                                String total_dosage = medication.getString("total_dosage");
                                String consumption_time = medication.getString("consumption_time");
                                String patient_id = medication.getString("patient_id");
                                String administration = medication.getString("administration");

                                MedicationObject medObj = new MedicationObject(id, brand_name, generic_name, dosage_form,
                                        per_dosage, total_dosage, consumption_time, patient_id, administration);

                                values = medObj.getContentValues();
                                if (!medicationDBHandler.CheckIsDataAlreadyInDBorNot(MedicationDatabaseSQLiteHandler.TABLE_MEDICATIONS,
                                        MedicationDatabaseSQLiteHandler.KEY_ID, String.valueOf(id))) {
                                    resolver.insert(MedicationContract.Medications.CONTENT_URI, values);
                                }
                                values.clear();
                            }
//                            txtResponse.setText(jsonResponse);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        pDialog.hide();

//                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.hide();
//                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("patient_id", patientID);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(this).addToRequestQueue(req);
//        Volley.newRequestQueue(this).add(strReq);
    }

    private void populateListViewfromdb() {
        Log.d("Test", "populateListViewfromdb entered");
        ContentResolver resolver = getContentResolver();
        String[] projection = medicationDBHandler.ALL_MED_KEYS;
        Cursor cursor =
                resolver.query(MedicationContract.Medications.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
        Log.d("Test", "populateListViewfromdb entered(after cursor)");
        if (cursor == null) {
            Log.e("Cursor", "EROROOROROOROROROROROOR");
        } else {
            Log.d("Cursor", "Not null?");

        }
//        startManagingCursor(cursor);
        Log.d("Cursor", "After managing cursor");
        String[] fromFieldNames = new String[]{MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME, MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME,
                MedicationDatabaseSQLiteHandler.KEY_PER_DOSAGE, MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM,
                MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE, MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME};
        int[] toViewIDs = new int[]
                {R.id.list_BrandName, R.id.list_GenericName, R.id.list_PerDosage, R.id.list_DosageForm, R.id.List_TotalDosage,
                        R.id.list_ConsumptionTime};

        Log.d("Cursor", "After managing cursor");
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,        // Context
                        R.layout.itemlayout,    // Row layout template
                        cursor,                    // cursor (set of DB records to map)
                        fromFieldNames,            // DB Column names
                        toViewIDs                // View IDs to put information in
//                        0
                );


        FragmentManager manager = getFragmentManager();
        manager.executePendingTransactions();
        inventoryFragment = (Inventory) manager.findFragmentByTag("invFragment");
        if (inventoryFragment != null) {
            Log.d("debug", "fragment is not null");
            inventoryFragment.populateList(myCursorAdapter);
        } else {
            Log.e("DEBUG", "fragment is NULL");
        }
//        cursor.close();
    }

    public void displayToastForId(long idInDB) {
        Log.d("displayToastForId", "Entered");
        ContentResolver resolver = getContentResolver();
        String[] projection = medicationDBHandler.ALL_MED_KEYS;
        String selection = medicationDBHandler.KEY_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(idInDB)};
        Cursor cursor =
                resolver.query(MedicationContract.Medications.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                String info_administration = cursor.getString(cursor.getColumnIndex("Administration"));//MedicationDatabaseSQLiteHandler.COL_ADMINISTRATION);

                new AlertDialog.Builder(this)
                        .setTitle("Administration")
                        .setMessage(info_administration)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("displayToastForId", "Cursor is nullllll");
        }
        cursor.close();
    }

    public void deleteId(long idInDB) {
        Log.d("displayToastForId", "Entered");
        ContentResolver resolver = getContentResolver();
        String[] projection = medicationDBHandler.ALL_MED_KEYS;
        String selection = medicationDBHandler.KEY_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(idInDB)};
        Cursor cursor =
                resolver.query(MedicationContract.Medications.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                final long idDB = cursor.getLong(cursor.getColumnIndex("_id"));//MedicationDatabaseSQLiteHandler.COL_ROWID);
                final String info_BrandName = cursor.getString(cursor.getColumnIndex("BrandName"));//MedicationDatabaseSQLiteHandler.COL_BRANDNAME);

                new AlertDialog.Builder(this)
                        .setTitle("Delete Medication?")
                        .setMessage("Delete " + info_BrandName + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ContentResolver resolver1 = getContentResolver();
                                long noDeleted = resolver1.delete
                                        (MedicationContract.Medications.CONTENT_URI,
                                                medicationDBHandler.KEY_ID + " = ? ",
                                                new String[]{String.valueOf(idDB)});
                                Toast.makeText(MainActivity.this, info_BrandName + " has been deleted.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
            cursor.close();
        } else {
            Log.e("displayToastForId", "Cursor is nullllll");
        }
        cursor.close();
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}