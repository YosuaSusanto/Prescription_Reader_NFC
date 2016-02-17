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
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.widget.Toast;

import helper.SessionManager;


public class MainActivity extends FragmentActivity
        implements Scan.OnFragmentInteractionListener,Inventory.OnFragmentInteractionListener,
        Monitoring.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks, Communicator {

    private boolean useNFC = true;
    public String BrandName = "";
    public String GenericName = "";
    public String DosageForm = "";
    public String PerDosage = "";
    public String TotalDosage = "";
    public String ConsumptionTime = "";
    public String PatientID = "";
    public String Administration = "";
    private MedicationDatabaseSQLiteHandler medicationDBHandler;
    private Connection connectionSQL = null;
    private Toast backToast = null;
    private SessionManager session;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Scan mScanFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;

    private AlarmManager manager;

    //Text to speech
    TextToSpeech textToSpeechObject;
    int textToSpeechResult;
    String stringToBeRead;

    // Constants
    // Content provider scheme
    public static final String SCHEME = "content://";
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = MedicationContract.AUTHORITY;
    // Path for the content provider table
    public static final String TABLE_MEDICATION_PATH = "medicationTable";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "prescriptionreadernfc.account";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;

    TableObserver tableObserver;

    public class TableObserver extends ContentObserver {
        private boolean selfChange;
        public TableObserver() {
            super(null);
        }

        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         * This method signature is provided for compatibility with
         * older platforms.
         */
        @Override
        public void onChange(boolean selfChange) {
            /*
             * Invoke the method signature available as of
             * Android platform version 4.1, with a null URI.
             */
            onChange(selfChange, null);
        }

        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         */
        @Override
        public void onChange(boolean selfChange, Uri changeUri) {
            /*
             * Ask the framework to run your sync adapter.
             * To maintain backward compatibility, assume that
             * changeUri is null.*/
            ContentResolver.requestSync(mAccount, AUTHORITY, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(this);
        session  = new SessionManager(getApplicationContext());
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preferences, false);

        // Get the content resolver object for your app
//        mResolver = getContentResolver();
        // Construct a URI that points to the content provider data table
//        mUri = new Uri.Builder()
//                .scheme(SCHEME)
//                .authority(AUTHORITY)
//                .path(TABLE_MEDICATION_PATH)
//                .build();
        /*
         * Create a content observer object.
         * Its code does not mutate the provider, so set
         * selfChange to "false"
         */
//        tableObserver = new TableObserver();
        /*
         * Register the observer for the data table. The table's path
         * and any of its subpaths trigger the observer.
         */
//        mResolver.registerContentObserver(mUri, true, tableObserver);

        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        PatientID = session.getPatientID();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            useNFC = true;
            //Yes NFC available
        }else{
            useNFC = false;
            //Your device doesn't support NFC
        }
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

        //Text to Speech
        textToSpeechObject = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    //Everything run well, set the language
                    Locale current = getResources().getConfiguration().locale;
                    textToSpeechResult = textToSpeechObject.setLanguage(current);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent = new Intent(this, StarterService.class);
        intent.putExtra("account", mAccount);
        intent.putExtra("patientID", PatientID);
        this.startService(intent);

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        getContentResolver().unregisterContentObserver (tableObserver);
    }

    @Override
    public void onBackPressed() {
        Log.d("BackTest", "onBackPressed called!");
        FragmentManager manager = getFragmentManager();
        mScanFragment = (Scan) manager.findFragmentByTag("scanFragment");
        if (mScanFragment != null && mScanFragment.isVisible()) {
            if(backToast != null && backToast.getView().getWindowToken() != null) {
                Log.d("BackTest", "Exiting app...");
                Toast.makeText(this, "Exiting app...", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                backToast = Toast.makeText(this, "Press back to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
        } else {
            //other stuff...
//            super.onBackPressed();
            Log.d("BackTest", "Switching to Scan fragment");
            manager.beginTransaction()
                    .replace(R.id.container, new Scan(), "scanFragment")
                    .commit();
        }
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
//            String accountName = ACCOUNT;
//            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
//            for(Account account : accounts) {
//                if(account.name.equals(accountName)) {
//                    return newAccount;
//                }
//            }
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
                    new NdefReaderTask(this).execute(tag);

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
                        new NdefReaderTask(this).execute(tag);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        backToast = null;
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        if (useNFC) {
            setupForegroundDispatch(this, mNfcAdapter);
        }
        PatientID = session.getPatientID();
        if (!PatientID.equals("")) {
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

        if (textToSpeechObject != null) {
            textToSpeechObject.shutdown();
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
        setIntent(intent);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            String text = extras.getString("text");
            if (text != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Medication Reminder")
                        .setMessage(text)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

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
        private Context mContext;
        public NdefReaderTask (Context context){
            mContext = context;
        }
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
                        ConsumptionTime = readText(records[7]);
//                        PatientID = readText(records[6]);
//                        Administration = readText(records[7]);

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
                populateLocalDB(PatientID);

                FragmentManager manager = getFragmentManager();

                mScanFragment = (Scan) manager.findFragmentByTag("scanFragment");

                if (mScanFragment != null) {
                    Log.d("debug", "fragment is not null");
                    mScanFragment.changeText(BrandName, GenericName, DosageForm, PerDosage, TotalDosage, ConsumptionTime);
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                    Toast.makeText(mContext, "toggle is " + prefs.getBoolean("pref_textToSpeechToggle", false),
                            Toast.LENGTH_SHORT).show();
                    if (prefs.getBoolean("pref_textToSpeechToggle", false)) {
                        readOutMedicationInfo();
                    }
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
            Fragment fragment;
            FragmentManager fragmentManager = getFragmentManager();
            fragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, "settingsFragment" )
                    .addToBackStack(null)
                    .commit();

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
        } else if (id == R.id.action_scanItem3) {
            scanItemThree();
            return true;
        } else if (id == R.id.action_scanItem4) {
            scanItemFour();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent intent = new Intent(this, StarterService.class);
        intent.putExtra("account", mAccount);
        intent.putExtra("patientID", PatientID);
        this.startService(intent);
    }

    ////////// FOR DEBUGGING PURPOSE ///////////
    private void scanItemOne() {
        BrandName = "Tykerb";
        GenericName = "Laptinib";
        DosageForm = "Tablet";
        PerDosage = "5";
        TotalDosage = "105";
        ConsumptionTime = "07:00";
        PatientID = "G1159974K";
        Administration = "Should be taken on an empty stomach: Take at least 1 hr before or 1 hr after a meal. " +
                "Do not eat/drink grapefruit products.";
        updateScanFragment();
    }

    private void scanItemTwo() {
        BrandName = "Xeloda";
        GenericName = "Capecitabine";
        DosageForm = "Tablet";
        PerDosage = "5";
        TotalDosage = "55";
        ConsumptionTime = "07:00, 12:30, 17:45";
        PatientID = "G1159974K";
        Administration = "Should be taken with food: Take w/in ½ hr after meals.";
        updateScanFragment();
    }

    private void scanItemThree() {
        Intent intent = new Intent(this, NotificationBarAlarm.class);
        sendBroadcast(intent);
    }

    private void scanItemFour() {
        new AlertDialog.Builder(this)
                .setTitle("Medication Course Completed")
                .setMessage("Medication Consumption Completed\n" +
                        "Medication Deleted from Inventory\n" +
                        "Do you want to erase the NFC tag data?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent(MainActivity.this, WriteNfcTagActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
        int saved_TotalDosage = -1;
        if (!session.getPatientID().equals(PatientID)) {
            session.setPatientID(PatientID);
            populateLocalDB(PatientID);
            Log.d("Test", "Local Database populated");
            Intent intent = new Intent(this, DefaultConsumptionReceiver.class);
            intent.putExtra("account", mAccount);
            intent.putExtra("patientID", PatientID);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendBroadcast(intent);
        }

        ContentResolver resolver = getContentResolver();
        Uri uri = MedicationContract.Medications.CONTENT_URI;
        String[] projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_ID, MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE,
                MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME, MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM};
        String selection = MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME + " = ? AND " +
                MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM + " = ?";
        String[] selectionArgs = new String[]{BrandName, DosageForm};
        Cursor cursor =
                resolver.query(uri,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                saved_TotalDosage = cursor.getInt(cursor.getColumnIndex("TotalDosage"));//MedicationDatabaseSQLiteHandler.COL_TOTALDOSAGE);
            }
            cursor.close();
        }
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
            mScanFragment.changeText(BrandName, GenericName, DosageForm, PerDosage, Integer.toString(saved_TotalDosage), ConsumptionTime);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getBoolean("pref_textToSpeechToggle", false)) {
                readOutMedicationInfo();
            }
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
            String[] projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_ID, MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID,
                    MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE, MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME,
                    MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM};
            String selection = MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID + " = ? AND " +
                    MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME + " = ? AND " +
                    MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM + " = ?";
            String[] selectionArgs = new String[]{session.getPatientID(), BrandName, DosageForm};
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
                Log.d("Consume Func: ", "saved_ID = " + saved_Id);
                int effectiveTotalDosage = saved_TotalDosage - Integer.parseInt(PerDosage);
                if (saved_Id > -1) {
                    ContentValues values = new ContentValues();

                    uri = MedicationContract.Consumption.CONTENT_URI;
                    projection = new String[]{MedicationDatabaseSQLiteHandler.KEY_ID, MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID,
                            MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT, MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN,
                            MedicationDatabaseSQLiteHandler.KEY_REMAINING_DOSAGE};
                    selection = MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID + " = ? AND " +
                            MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN + " = ? AND " +
                            MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " >= date('now', 'localtime') AND " +
                            MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " < date('now', 'localtime', '+1 day') AND " +
                            MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + " < datetime('now', 'localtime')";
                    selectionArgs = new String[]{String.valueOf(saved_Id), "No"};
                    String sortOrder = "(julianday('now') - julianday(" + MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT + ")) ASC";
                    Cursor cursor2 =
                            resolver.query(uri,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    sortOrder);
                    if (cursor2 != null) {
                        if (cursor2.moveToFirst()) {
                            long consumption_Id = cursor2.getLong(cursor2.getColumnIndex("_id"));//MedicationDatabaseSQLiteHandler.COL_ROWID);
                            String curTime = getCurrentTimeStamp();
                            selection = MedicationDatabaseSQLiteHandler.KEY_ID + " = ?";
                            selectionArgs = new String[]{String.valueOf(consumption_Id)};
                            values.clear();
                            values.put(MedicationDatabaseSQLiteHandler.KEY_MEDICATION_ID, saved_Id);
                            values.put(MedicationDatabaseSQLiteHandler.KEY_CONSUMED_AT, curTime);
                            values.put(MedicationDatabaseSQLiteHandler.KEY_IS_TAKEN, "Yes");
                            values.put(MedicationDatabaseSQLiteHandler.KEY_REMAINING_DOSAGE, effectiveTotalDosage);
                            resolver.update(MedicationContract.Consumption.CONTENT_URI, values, selection, selectionArgs);
                            Toast.makeText(this, "saved_Id: " + saved_Id + ", curTime: " + curTime + ", totalDosage: " + effectiveTotalDosage,
                                    Toast.LENGTH_LONG).show();
                            updateConsumptionsRemoteDB((int)saved_Id, curTime, "Yes", String.valueOf(effectiveTotalDosage));
                        }
                        cursor2.close();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Medication Already Taken")
                                .setMessage("You have already taken your medication for today")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                    selection = MedicationDatabaseSQLiteHandler.KEY_ID + " = " + saved_Id;
                    values.clear();
                    values.put(MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE, effectiveTotalDosage);
                    uri = ContentUris.withAppendedId(MedicationContract.Medications.CONTENT_URI, saved_Id);
                    resolver.update(uri, values, selection, null);
                    updateDosageRemoteDB((int) saved_Id, String.valueOf(effectiveTotalDosage));
                    // If effectiveTotal Dosage is <1, delete medicine
                    // else just notify user consumption successful
                    if (effectiveTotalDosage < 1) {
                        resolver.delete
                                (MedicationContract.Medications.CONTENT_URI,
                                        MedicationDatabaseSQLiteHandler.KEY_ID + " = ? ",
                                        new String[]{String.valueOf(saved_Id)});
                        new AlertDialog.Builder(this)
                                .setTitle("Medication Course Completed")
                                .setMessage("Medication Consumption Completed\n" +
                                        "Medication Deleted from Inventory\n" +
                                        "Do you want to erase the NFC tag data?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        Intent intent = new Intent(MainActivity.this, WriteNfcTagActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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

    public void respondReset() {
        BrandName = "";
        GenericName = "";
        DosageForm = "";
        PerDosage = "";
        TotalDosage = "";
        ConsumptionTime = "";
        Administration = "";
    }

    public void displayToastForId(long idInDB) {
        Log.d("displayToastForId", "Entered");
        ContentResolver resolver = getContentResolver();
        String[] projection = MedicationDatabaseSQLiteHandler.ALL_MED_KEYS;
        String selection = MedicationDatabaseSQLiteHandler.KEY_ID + " = ?";
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
            cursor.close();
        } else {
            Log.e("displayToastForId", "Cursor is nullllll");
        }
    }

    public void deleteId(long idInDB) {
        Log.d("displayToastForId", "Entered");
        ContentResolver resolver = getContentResolver();
        String[] projection = MedicationDatabaseSQLiteHandler.ALL_MED_KEYS;
        String selection = MedicationDatabaseSQLiteHandler.KEY_ID + " = ?";
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
                                resolver1.delete
                                        (MedicationContract.Medications.CONTENT_URI,
                                                MedicationDatabaseSQLiteHandler.KEY_ID + " = ? ",
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
    }

    /**
     * Populate local db with medication data from server
     * */
    private void populateLocalDB(String patientID) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("functions", "populateLocalDB");
        settingsBundle.putString("patient_id", patientID);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    /**
     * Update dosage on remote medication DB
     * */
    private void updateDosageRemoteDB(int med_id, String effective_dosage) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("functions", "updateDosage");
        settingsBundle.putInt("med_id", med_id);
        settingsBundle.putString("effective_dosage", effective_dosage);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    /**
     * Update consumption details on remote medication DB
     * */
    private void updateConsumptionsRemoteDB(final int med_id, final String consumption_time,
                                            final String is_taken, final String remaining_dosage) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("functions", "updateConsumption");
        settingsBundle.putInt("med_id", med_id);
        settingsBundle.putString("consumption_time", consumption_time);
        settingsBundle.putString("is_taken", "Yes");
        settingsBundle.putString("remaining_dosage", remaining_dosage);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    private void populateListViewfromdb() {
        Inventory inventoryFragment;
        List<MedicationObject> medList = new ArrayList<MedicationObject>();
        Log.d("Test", "populateListViewfromdb entered");
        ContentResolver resolver = getContentResolver();
        String[] projection = MedicationDatabaseSQLiteHandler.ALL_MED_KEYS;
        String selection = MedicationDatabaseSQLiteHandler.KEY_PATIENT_ID + " = ?";
        String[] selectionArgs = new String[]{PatientID};
        Cursor cursor =
                resolver.query(MedicationContract.Medications.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        Log.d("Test", "populateListViewfromdb entered(after cursor)");
        if (cursor.moveToFirst()) {
            Log.d("Cursor", "Not null?");
            do {
                MedicationObject medicationObject = MedicationObject.fromCursor(cursor);
                medList.add(medicationObject);
            } while (cursor.moveToNext());
        } else {
            Log.e("Cursor", "cursor is empty!!");
        }
//        startManagingCursor(cursor);
//        Log.d("Cursor", "After managing cursor");
//        String[] fromFieldNames = new String[]{};
//                {MedicationDatabaseSQLiteHandler.KEY_BRAND_NAME, MedicationDatabaseSQLiteHandler.KEY_GENERIC_NAME,
//                MedicationDatabaseSQLiteHandler.KEY_PER_DOSAGE, MedicationDatabaseSQLiteHandler.KEY_DOSAGE_FORM,
//                MedicationDatabaseSQLiteHandler.KEY_TOTAL_DOSAGE, MedicationDatabaseSQLiteHandler.KEY_CONSUMPTION_TIME};
//        int[] toViewIDs = new int[] {};
//                {R.id.list_BrandName, R.id.list_GenericName, R.id.list_PerDosage, R.id.list_DosageForm, R.id.List_TotalDosage,
//                        R.id.list_ConsumptionTime};

//        Log.d("Cursor", "After managing cursor");
//        SimpleCursorAdapter myCursorAdapter =
//                new SimpleCursorAdapter(
//                        this,        // Context
//                        R.layout.itemlayout,    // Row layout template
//                        cursor,                    // cursor (set of DB records to map)
//                        fromFieldNames,            // DB Column names
//                        toViewIDs                // View IDs to put information in
//                        0
//                );


        FragmentManager manager = getFragmentManager();
        manager.executePendingTransactions();
        inventoryFragment = (Inventory) manager.findFragmentByTag("invFragment");
        if (inventoryFragment != null) {
            Log.d("debug", "fragment is not null");
            MedicationListAdapter mAdapter = new MedicationListAdapter(this, medList);

//            inventoryFragment.populateList(myCursorAdapter);
            inventoryFragment.populateList(mAdapter);
        } else {
            Log.e("DEBUG", "fragment is NULL");
        }
//        cursor.close();
    }

//  When the patient tap the tag, the phone should read out: 1) Drug name,
//  2) dosage form, 3) dosage strength, 4) dosage taken, 5) frequency, 6) consumption time
    public void readOutMedicationInfo(){
        String[] consumptionTimeArr = ConsumptionTime.split(", ");
        int frequency = consumptionTimeArr.length;
        String consumptionTimes = getTimeInAMPM(consumptionTimeArr[0]);
        String timeOrTimes = " time a day at ";

        if (frequency > 1) {
            timeOrTimes = " times a day at ";
            if (frequency == 2) {
                consumptionTimes += " and " + getTimeInAMPM(consumptionTimeArr[1]);
            } else {
                for (int i = 1; i < consumptionTimeArr.length - 1; i++) {
                    consumptionTimes += ", " + getTimeInAMPM(consumptionTimeArr[i]);
                }
                consumptionTimes += " and " +
                        getTimeInAMPM(consumptionTimeArr[consumptionTimeArr.length-1]);
            }
        }


        if(textToSpeechResult == TextToSpeech.LANG_NOT_SUPPORTED || textToSpeechResult == TextToSpeech.LANG_MISSING_DATA){
            Toast.makeText(getApplicationContext(),
                    "Feature not supported in your device", Toast.LENGTH_SHORT).show();

        }else{
            stringToBeRead = "This is " + BrandName + ". Take " + PerDosage + " "
                    + DosageForm + " for " + frequency + timeOrTimes + consumptionTimes;
            float rate = (float) 0.85;
            textToSpeechObject.setSpeechRate(rate);
            textToSpeechObject.speak(stringToBeRead, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // Convert time in 24 hours format into 12 hours format
    public static String getTimeInAMPM(String time) {
        String hour = time.substring(0, 2);
        int intHour = Integer.parseInt(hour);
        String min = time.substring(3);
        int intMin = Integer.parseInt(min);

        if (intHour >= 12) {
            if (intHour > 12) {
                intHour -= 12;
            }
            hour = intHour + " ";
            if (intMin == 0) {
                hour += "PM";
            } else {
                hour += intMin + "PM";
            }
        } else {
            if (intHour == 0) {
                intHour = 12;
            }
            hour = intHour + " ";
            if (intMin == 0) {
                hour += "AM";
            } else {
                hour += intMin + "AM";
            }
        }

        return hour;
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