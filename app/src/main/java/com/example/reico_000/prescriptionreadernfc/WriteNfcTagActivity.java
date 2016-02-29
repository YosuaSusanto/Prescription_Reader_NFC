package com.example.reico_000.prescriptionreadernfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.FormatException;
import android.nfc.NdefRecord;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by Yosua Susanto on 19/1/2016.
 */
public class WriteNfcTagActivity extends Activity {
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(this)
                .setTitle("Please Scan the tag to be deleted")
                .setMessage("Please Scan the tag to be deleted")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
//        setContentView(R.layout.activity_write_nfc_tag);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecyclerView
        //Initializing Views
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

        //Initializing our patients list
//        listPatientsData = new ArrayList<>();

        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC unavailable! Please turn it on!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "NFC Tag Detected!", Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            NdefMessage ndefMessage = createNdefMessage(CardAdapter.encodeUID, CardAdapter.encodePatientName, CardAdapter.encodeNRIC,
//                    CardAdapter.encodeBrandName, CardAdapter.encodeGenericName, CardAdapter.encodeDosageForm,
//                    CardAdapter.encodeDosageStrength, CardAdapter.encodePerDosage, CardAdapter.encodeTotalDosage,
//                    CardAdapter.encodeFrequency, CardAdapter.encodeDateStart, CardAdapter.encodeConsumptionTime);
//            writeNdefMessage(tag, ndefMessage);
            try {
                Ndef ndefTag = Ndef.get(tag);
                ndefTag.connect();
                if (ndefTag.isWritable()) {
                    ndefTag.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
//                    Toast.makeText(this, "NFC Tag has been successfully encoded.", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(this)
                            .setTitle("Erasing tag finished")
                            .setMessage("Erasing tag data finished, you can now remove the tag")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Toast.makeText(this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                }
                ndefTag.close();
            } catch (Exception e) {
                Log.e("writeNdefMessage", e.getMessage());
            }
            new AlertDialog.Builder(this)
                    .setTitle("NFC Tag Cleared")
                    .setMessage("The data inside NFC tag has been cleared!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface arg0) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//                    finish();

        //NFC
            @Override
    protected void onResume() {
        super.onResume();
        enableForeGroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForeGroundDispatchSystem();
    }

    private void enableForeGroundDispatchSystem(){
        Intent intent = new Intent(this, WriteNfcTagActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForeGroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }
}
