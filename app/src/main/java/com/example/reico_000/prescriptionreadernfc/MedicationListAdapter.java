package com.example.reico_000.prescriptionreadernfc;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helper.DataTransferInterface;
import helper.VolleyCallback;

/**
 * Created by Yosua Susanto on 2/2/2016.
 */
public class MedicationListAdapter extends BaseAdapter implements DataTransferInterface {
    private ArrayList<MedicationObject> mainList;
    private Context context;
    private Account account;
    private ArrayList<String> checkedTimeList;
    private String[] consumptionTimeArr;
    private MedicationObject medObject;
    private ConsumptionTimeListAdapter consumptionTimeAdapter;
    private String filePath;
    DataTransferInterface dtInterface;

    private static final String siteName = "http://www.onco-informatics.com/medfc/";

    //Dialog in other avtivity
    private AlertDialog stopMedicationDialog = null;

    public MedicationListAdapter(Context applicationContext, Account account,
                                 List<MedicationObject> medList, DataTransferInterface dtInterface) {

        super();
        this.account = account;
        this.context = applicationContext;
        this.dtInterface = dtInterface;
        this.mainList = new ArrayList<MedicationObject>(medList);
    }

    public MedicationListAdapter() {

        super();
//        this.mainList = QuestionForSliderMenu;

    }

    @Override
    public int getCount() {

        return mainList.size();
    }

    @Override
    public Object getItem(int position) {

        return mainList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itemlayout, null);
        }
//        final MedicationObject medicationObject = (MedicationObject) getItem(position);
        medObject = (MedicationObject) getItem(position);
        if (medObject != null) {
            TextView brandNameView = (TextView) convertView
                    .findViewById(R.id.list_BrandName);
            TextView genericNameView = (TextView) convertView
                    .findViewById(R.id.list_GenericName);
            TextView perDosageView = (TextView) convertView
                    .findViewById(R.id.list_PerDosage);
            TextView dosageFormView = (TextView) convertView
                    .findViewById(R.id.list_DosageForm);
            TextView totalDosageView = (TextView) convertView
                    .findViewById(R.id.List_TotalDosage);
            TextView consumptionTimeView = (TextView) convertView
                    .findViewById(R.id.list_ConsumptionTime);
            TextView frequencyView = (TextView) convertView
                    .findViewById(R.id.list_Frequency);
            ImageView imageClick = (ImageView) convertView
                    .findViewById(R.id.action_row_overflow);

            if (brandNameView != null) {
                brandNameView.setText(medObject.get_brandName());
            }
            if (genericNameView != null) {
                genericNameView.setText(medObject.get_genericName());
            }
            if (perDosageView != null) {
                perDosageView.setText(medObject.get_perDosage());
            }
            if (dosageFormView != null) {
                dosageFormView.setText(medObject.get_dosageForm());
            }
            if (totalDosageView != null) {
                totalDosageView.setText(medObject.get_totalDosage());
            }
            if (consumptionTimeView != null) {
                consumptionTimeView.setText(medObject.get_consumptionTime());
            }
            if (frequencyView != null) {
                int frequency = medObject.get_consumptionTime().split(",").length;
                if (frequency == 1) {
                    frequencyView.setText(frequency + " time a day");
                } else {
                    frequencyView.setText(frequency + " times a day");
                }
            }
            consumptionTimeArr = medObject.get_consumptionTime().split(", ");
            checkedTimeList = new ArrayList<String>(Arrays.asList(consumptionTimeArr));

            try {
//                tv1.setText(" List Item "+ " : " + position);
                imageClick.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.action_row_overflow:
                            PopupMenu popup = new PopupMenu(context, v);
                            popup.getMenuInflater().inflate(R.menu.popup_menu,
                                    popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    switch (item.getItemId()) {
                                        case R.id.item_changeReminderTiming:
                                            //Or Some other code you want to put here.. This is just an example.
                                            Toast.makeText(context, " Change reminder timing Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();
                                            final Dialog dialog = new Dialog(context);
                                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(final DialogInterface arg0) {
                                                    // do something
//                                                        mainList.remove(medicationObject);
                                                    medObject = (MedicationObject) getItem(position);

                                                    MedicationObject medicationObject2 = new MedicationObject(medObject.get_id(), medObject.get_brandName(),
                                                            medObject.get_genericName(), medObject.get_dosageForm(), medObject.get_perDosage(),
                                                            medObject.get_totalDosage(), medObject.get_consumptionTime(), medObject.get_patientID(),
                                                            medObject.get_administration(), medObject.get_remarks(), medObject.get_sideEffects(),
                                                            medObject.get_prescriptionDate());
                                                    String sortedTimeString = getSortedTimeString(checkedTimeList);
                                                    medicationObject2.set_consumptionTime(sortedTimeString);
                                                    ArrayList<MedicationObject> tempList = new ArrayList<MedicationObject>
                                                            (Arrays.asList(medObject, medicationObject2));
                                                    dtInterface.setValues("changeTiming", tempList);
//                                                        mainList.add(medicationObject);
                                                }
                                            });
                                            LayoutInflater inflater = (LayoutInflater) context
                                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view = inflater.inflate(R.layout.dialog_list, null);

                                            ListView lv = (ListView) view.findViewById(R.id.custom_list);

                                            // Change MyActivity.this and myListOfItems to your own values
                                            medObject = (MedicationObject) getItem(position);
                                            consumptionTimeArr = medObject.get_consumptionTime().split(", ");
                                            checkedTimeList = new ArrayList<String>(Arrays.asList(consumptionTimeArr));
                                            consumptionTimeAdapter = new ConsumptionTimeListAdapter(context,
                                                    checkedTimeList, MedicationListAdapter.this);

                                            lv.setAdapter(consumptionTimeAdapter);

//                                                lv.setOnItemClickListener(........);

                                            dialog.setContentView(view);

                                            dialog.show();

                                            break;
                                        case R.id.item_stopTakingMedicine:
                                            medObject = (MedicationObject) getItem(position);
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Stop Taking Medication")
                                                    .setMessage("Are you sure you want to stop taking " +
                                                            medObject.get_genericName() + "?" +
                                                            "\nPlease consult your physician before doing so.")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            ContentResolver resolver1 = context.getContentResolver();
                                                            Uri uri = MedicationContract.Medications.CONTENT_URI;
                                                            ContentValues values = new ContentValues();
                                                            String stopRemarks = "Patient has stopped taking this medication.";
                                                            values.put(MedicationDatabaseSQLiteHandler.KEY_REMARKS,
                                                                    "Patient has stopped taking this medication.");
                                                            String selection = MedicationDatabaseSQLiteHandler.KEY_ID + " = ?";
                                                            String[] args = new String[]{String.valueOf(medObject.get_id())};
//                                                            resolver1.update(uri, values, selection, args);
                                                            resolver1.delete(uri, selection, args);
                                                            updateRemarksRemoteDB(medObject.get_id(), stopRemarks,
                                                                    medObject.get_sideEffects());
//                                                            blockMedicationRemoteDB(medObject.get_id());
//                                                                Toast.makeText(context, info_BrandName + " has been deleted.", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            break;
                                        case R.id.item_showMoreInfo:
                                            medObject = (MedicationObject) getItem(position);
                                            getPDFFilePath(String.valueOf(medObject.get_brandName()), new VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    filePath = result;
                                                    File file = new File(context.getExternalFilesDir(null).getAbsolutePath() +
                                                            "/" + medObject.get_brandName() + ".pdf");
                                                    if (file.exists()) {
                                                        // Open the pdf file
                                                        openPDFFile(file);
                                                    } else {
                                                        // Download, then open
                                                        String downloadURL = siteName + filePath;
                                                        downloadURL = downloadURL.replaceAll(" ", "%20");
//                                                        String fileName = filePath.substring(4);
                                                        String fileName = medObject.get_brandName() + ".pdf";
                                                        DownloadManager.Request request = new
                                                                DownloadManager.Request(Uri.parse(downloadURL));
                                                        request.setDescription("Downloading medication informationi file...");
                                                        request.setTitle("Downloading Medication Information");
                                                        // in order for this if to run, you must use the android 3.2 to compile your app
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                                            request.allowScanningByMediaScanner();
                                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                        }

                                                        request.setDestinationInExternalFilesDir(context, null, fileName);

// get download service and enqueue file
                                                        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                                        manager.enqueue(request);

                                                        BroadcastReceiver onComplete=new BroadcastReceiver() {
                                                            public void onReceive(Context ctxt, Intent intent) {
                                                                // your code
                                                                openPDFFile(new File(context.getExternalFilesDir(null).getAbsolutePath() +
                                                                        "/" + medObject.get_brandName() + ".pdf"));
                                                            }
                                                        };

                                                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                                                    }
                                                }
                                            });
                                            break;
                                        case R.id.item_scanMedication:
                                            medObject = (MedicationObject) getItem(position);
                                            dtInterface.setValues("scanMedication", new ArrayList<MedicationObject>
                                                    (Arrays.asList(medObject)));
                                            break;
//                                        case R.id.item_viewOrEditRemarks:
//                                            final EditText input = new EditText(context);
//                                            medObject = (MedicationObject) getItem(position);
//                                            Toast.makeText(context, "Clicked postion " + position, Toast.LENGTH_SHORT).show();
//                                            MedicationObject medicationObject2 = new MedicationObject(medObject.get_id(), medObject.get_brandName(),
//                                                    medObject.get_genericName(), medObject.get_dosageForm(), medObject.get_perDosage(),
//                                                    medObject.get_totalDosage(), medObject.get_consumptionTime(), medObject.get_patientID(),
//                                                    medObject.get_administration(), medObject.get_remarks(), medObject.get_sideEffects(),
//                                                    medObject.get_prescriptionDate());
//
//                                            // Specify the type of input expected;
//                                            input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                                            input.setSingleLine(false);
//                                            input.setText(medicationObject2.get_sideEffects());
//                                            new AlertDialog.Builder(context)
//                                                .setTitle("Symptoms")
//                                                .setMessage("Edit your reported symptoms")
//                                                .setView(input)
//                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
////                                                    Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();
//                                                    ContentResolver resolver1 = context.getContentResolver();
//                                                    Uri uri = MedicationContract.Medications.CONTENT_URI;
//                                                    ContentValues values = new ContentValues();
//                                                    String sideEffects = input.getText().toString().trim();
//                                                    values.put(MedicationDatabaseSQLiteHandler.KEY_SIDEEFFECTS, sideEffects);
//                                                    String selection = MedicationDatabaseSQLiteHandler.KEY_ID + " = ?";
//                                                    String[] args = new String[]{String.valueOf(medObject.get_id())};
//                                                    resolver1.update(uri, values, selection, args);
//                                                    updateRemarksRemoteDB(medObject.get_id(), medObject.get_remarks(),
//                                                            sideEffects);
////                                                    resolver1.delete(uri, selection, args);
////                                                    blockMedicationRemoteDB(medObject.get_id());
//
//                                                }
//                                                })
//                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                                })
//                                                .show();
//                                            break;
                                        default:
                                            break;
                                    }
                                    return true;
                                }
                            });
                            break;
                        default:
                            break;
                    }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return convertView;
    }

    private void openPDFFile(File file) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

//        Intent intent = Intent.createChooser(target, "Open File");
        try {
            context.startActivity(target);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    private String getSortedTimeString (List<String> timeList) {
        ArrayList<String> timeArrayList = new ArrayList<String>(timeList), tempList = new ArrayList<String>();
        String sortedTimeString = "";

        for (String time : timeArrayList) {
            boolean isTimeInserted = false;
            for (int i = 0; i < tempList.size(); i++) {
                if (compareTimeString(time, tempList.get(i)) < 0) {
                    tempList.add(i, time);
                    isTimeInserted = true;
                    break;
                }
            }
            if (!isTimeInserted) {
                 tempList.add(time);
            }
        }

        sortedTimeString += tempList.get(0);
        for (int i = 1; i < tempList.size(); i++) {
            sortedTimeString += ", " + tempList.get(i);
        }

        return sortedTimeString;
    }

    private int compareTimeString (String first, String second) {
        int hour1 = Integer.parseInt(first.substring(0, 2)), minute1 = Integer.parseInt(first.substring(3));
        int hour2 = Integer.parseInt(second.substring(0, 2)), minute2 = Integer.parseInt(second.substring(3));

        if (hour1 < hour2 || (hour1 == hour2 && minute1 < minute2)) {
            return -1;
        } else if (hour1 > hour2 || (hour1 == hour2 && minute1 > minute2)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Update consumption details on remote medication DB
     * */
    private void updateRemarksRemoteDB(final int med_id, final String remarks, final String sideEffects) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("functions", "updateRemarks");
        settingsBundle.putInt("med_id", med_id);
        String combinedRemarks = "";
        if (sideEffects.equals("")) {
            combinedRemarks = remarks.trim();
        } else {
            combinedRemarks = sideEffects.trim() + ". " + remarks.trim();
        }

        settingsBundle.putString("remarks", combinedRemarks.trim());
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(account, MedicationContract.AUTHORITY, settingsBundle);
    }

    /**
     * Get PDF file path
     * */
    private void getPDFFilePath(final String brand_name, final VolleyCallback callback) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_GET_PDF_FILE_PATH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getPDFFilePath", response);

                        try {
                            // Parsing json array response
                            // loop through each json object
                            JSONArray jArr = new JSONArray(response);
                            JSONObject medication = (JSONObject) jArr.get(0);

                            String filePath = medication.getString("pdf_file_path");
                            callback.onSuccess(filePath);
//                            return filePath;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error in downloading file (JSONException): " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context, "Error in downloading file (VolleyError): " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("brand_name", brand_name);

                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance(context).addToRequestQueue(req);
    }

    @Override
    public void setValues(ArrayList<?> al) {
        checkedTimeList.remove((String)al.get(0));
        checkedTimeList.add((String) al.get(1));
        consumptionTimeAdapter.notifyDataSetChanged();
    }

    @Override
    public void setValues(String operation, ArrayList<?> al) {
        checkedTimeList.remove((String)al.get(0));
        checkedTimeList.add((String) al.get(1));
        consumptionTimeAdapter.notifyDataSetChanged();
    }
}
