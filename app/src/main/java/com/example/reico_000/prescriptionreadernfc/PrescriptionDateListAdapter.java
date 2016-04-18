package com.example.reico_000.prescriptionreadernfc;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import helper.DataTransferInterface;

/**
 * Created by Yosua Susanto on 25/3/2016.
 */
public class PrescriptionDateListAdapter extends BaseAdapter {
    private ArrayList<MedicationObject> mainList;
    private Context context;
    private Account account;
    private ArrayList<String> checkedTimeList;
    private String[] consumptionTimeArr;
    private MedicationObject medObject;
    private ConsumptionTimeListAdapter consumptionTimeAdapter;
    private String filePath;
    DataTransferInterface dtInterface;

    private static final String siteName = "http://www.onco-informatics.com/medadherence/";

    //Dialog in other avtivity
    private AlertDialog stopMedicationDialog = null;

    public PrescriptionDateListAdapter(Context applicationContext, Account account,
                                 List<MedicationObject> medList, DataTransferInterface dtInterface) {

        super();
        this.account = account;
        this.context = applicationContext;
        this.dtInterface = dtInterface;
        this.mainList = new ArrayList<MedicationObject>(medList);
    }

    public PrescriptionDateListAdapter() {

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
            convertView = inflater.inflate(R.layout.prescription_date_item_layout, null);
        }
//        final MedicationObject medicationObject = (MedicationObject) getItem(position);
        medObject = (MedicationObject) getItem(position);
        if (medObject != null) {
            TextView brandNameView = (TextView) convertView
                    .findViewById(R.id.list_BrandName);
            TextView genericNameView = (TextView) convertView
                    .findViewById(R.id.list_GenericName);
            TextView prescriptionDateView = (TextView) convertView
                    .findViewById(R.id.list_PrescriptionDate);
            if (brandNameView != null) {
                brandNameView.setText(medObject.get_brandName());
            }
            if (genericNameView != null) {
                genericNameView.setText(medObject.get_genericName());
            }
            if (prescriptionDateView != null) {
                String datetime = medObject.get_prescriptionDate();
                int deleteFrom = datetime.indexOf(" at ");
                String date = datetime.substring(0, deleteFrom);
                String month = (date.split(" "))[1];
                date = date.replace(month, month.substring(0, 3));
                prescriptionDateView.setText(date);
            }
        }
        return convertView;
    }
}