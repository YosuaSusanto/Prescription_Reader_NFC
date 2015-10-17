package com.example.reico_000.prescriptionreadernfc;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
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
import java.util.List;

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

    private boolean useNFC = false;
    public String BrandName = "";
    public String GenericName = "";
    public String DosageForm = "";
    public String PerDosage = "";
    public String TotalDosage = "";
    public String ConsumptionTime = "";
    public String PatientID = "43462553";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        medicationDBHandler = MedicationDatabaseSQLiteHandler.getInstance(this);
        session  = new SessionManager(getApplicationContext());

        /**
         * CRUD Operations
         //         * */
//        Inserting Contacts
//        Log.d("Delete", "Deletingggg");
//        db.deleteMedicationObject("Tykerb");
//        db.deleteMedicationObject("Capecitabine");
//        db.deleteMedicationObject("Test Brand Name");
//
//        Log.d("Insert: ", "Inserting ..");
//        db.addMedication(new MedicationObject("Tykerb", "Laptinib", "250mg Pill", "5", "105", "ME", "105440102"));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            PatientID = extras.getString("patient_id");
        }
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<MedicationObject> medObjects = medicationDBHandler.getAllMedications();

        for (MedicationObject medicationObject : medObjects) {
            String log = "UID: " + medicationObject.get_ID() + " ,BrandName: " + medicationObject.get_brandName() + " ," +
                    " GenericName: " + medicationObject.get_genericName() + " DosageForm: " + medicationObject.get_dosageForm()
                    + " PerDosage: " + medicationObject.get_perDosage() + " TotalDosage: " + medicationObject.get_totalDosage()
                    + " ConsumptionTime: " + medicationObject.get_consumptionTime() + " PatientID: " + medicationObject.get_patientID();
            // Writing Contacts to log
            Log.d("Name: ", log);
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
        PatientID = "434562553";
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
        //PatientID = "434562553";
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
        ConsumptionDetailsObject consumptionObject = medicationDBHandler.getConsumptionDetails(PatientID, BrandName, GenericName);
        DosageForm = Integer.toString(consumptionObject.get_medicationID());
        PerDosage = consumptionObject.get_consumedAt();
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
            Cursor cursor = medicationDBHandler.getByNameAndDosageForm(BrandName, DosageForm);
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
                    medicationDBHandler.addConsumptionDetails(PatientID, BrandName, GenericName, getCurrentTimeStamp());
                    medicationDBHandler.updateRow(saved_Id, effectiveTotalDosage);

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
                        medicationDBHandler.deleteMedicationObject(saved_Id);
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
            Cursor cursor = medicationDBHandler.getByNameAndDosageForm(BrandName, DosageForm);
            long saved_Id = -1;
            int saved_TotalDosage = -1;

            if (cursor.moveToFirst()) {
                saved_Id = cursor.getLong(cursor.getColumnIndex("_id"));//MedicationDatabaseSQLiteHandler.COL_ROWID);
                saved_TotalDosage = cursor.getInt(cursor.getColumnIndex("TotalDosage"));//MedicationDatabaseSQLiteHandler.COL_TOTALDOSAGE);
            }
            cursor.close();
            if (saved_Id > -1) {
                int effectiveTotalDosage = saved_TotalDosage + Integer.parseInt(TotalDosage);
                medicationDBHandler.updateRow(saved_Id, effectiveTotalDosage);
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
                medicationDBHandler.addMedication(new MedicationObject(BrandName, GenericName, DosageForm, PerDosage, TotalDosage,
                        ConsumptionTime, PatientID, Administration));
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
        //PatientID = "";
        Administration = "";
    }

    private void populateListViewfromdb() {
        Log.d("Test", "populateListViewfromdb entered");
        Cursor cursor = medicationDBHandler.getAllRows();
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
        Cursor cursor = medicationDBHandler.getRow(idInDB);
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
        Cursor cursor = medicationDBHandler.getRow(idInDB);
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                final long idDB = cursor.getLong(cursor.getColumnIndex("_id"));//MedicationDatabaseSQLiteHandler.COL_ROWID);
                final String info_BrandName = cursor.getString(cursor.getColumnIndex("BrandName"));//MedicationDatabaseSQLiteHandler.COL_BRANDNAME);

                new AlertDialog.Builder(this)
                        .setTitle("Delete Medication?")
                        .setMessage("Delete " + info_BrandName + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                medicationDBHandler.deleteMedicationObject(idDB);
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