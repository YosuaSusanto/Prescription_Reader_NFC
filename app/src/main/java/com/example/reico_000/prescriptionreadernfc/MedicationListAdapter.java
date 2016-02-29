package com.example.reico_000.prescriptionreadernfc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import helper.DataTransferInterface;

/**
 * Created by Yosua Susanto on 2/2/2016.
 */
public class MedicationListAdapter extends BaseAdapter implements DataTransferInterface {
    private ArrayList<MedicationObject> mainList;
    private Context context;
    private ArrayList<String> checkedTimeList;
    private String[] consumptionTimeArr;
    private MedicationObject medObject;
    private ConsumptionTimeListAdapter consumptionTimeAdapter;
    DataTransferInterface dtInterface;

    public MedicationListAdapter(Context applicationContext,
                                 List<MedicationObject> medList, DataTransferInterface dtInterface) {

        super();
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
                                                                medObject.get_administration());
                                                        String sortedTimeString = getSortedTimeString(checkedTimeList);
                                                        medicationObject2.set_consumptionTime(sortedTimeString);
                                                        ArrayList<MedicationObject> tempList = new ArrayList<MedicationObject>
                                                                (Arrays.asList(medObject, medicationObject2));
                                                        dtInterface.setValues(tempList);
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

    @Override
    public void setValues(ArrayList<?> al) {
        checkedTimeList.remove((String)al.get(0));
        checkedTimeList.add((String) al.get(1));
        consumptionTimeAdapter.notifyDataSetChanged();
    }
}
