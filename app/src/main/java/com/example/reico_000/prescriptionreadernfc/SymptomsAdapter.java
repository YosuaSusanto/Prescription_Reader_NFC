package com.example.reico_000.prescriptionreadernfc;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import helper.DataTransferInterface;

/**
 * Created by Yosua Susanto on 18/4/2016.
 */
public class SymptomsAdapter extends BaseAdapter {
    private ArrayList<SymptomsObject> mainList;
    private Context context;
    private Account account;
    private SymptomsObject symptomsObject;
    private SymptomsAdapter symptomsAdapter;
    private ConsumptionTimeListAdapter consumptionTimeAdapter;
    DataTransferInterface dtInterface;

    //Dialog in other avtivity
    private AlertDialog stopMedicationDialog = null;

    public SymptomsAdapter(Context applicationContext, Account account,
                                       List<SymptomsObject> symptomsList, DataTransferInterface dtInterface) {
        super();
        this.account = account;
        this.context = applicationContext;
        this.dtInterface = dtInterface;
        this.mainList = new ArrayList<SymptomsObject>(symptomsList);
    }

    public SymptomsAdapter() {

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
            convertView = inflater.inflate(R.layout.symptoms_item_layout, null);
        }

        symptomsObject = (SymptomsObject) getItem(position);
        if (symptomsObject != null) {
            TextView symptomsView = (TextView) convertView
                    .findViewById(R.id.list_Symptoms);
            TextView reportedOnView = (TextView) convertView
                    .findViewById(R.id.list_ReportedOn);
            if (symptomsView != null) {
                symptomsView.setText(symptomsObject.get_symptoms());
            }
            if (reportedOnView != null) {
                reportedOnView.setText(symptomsObject.get_reportedOn());
            }
        }
        return convertView;
    }
}
