package com.example.reico_000.prescriptionreadernfc;

import android.app.Dialog;
import android.content.Context;
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

/**
 * Created by Yosua Susanto on 2/2/2016.
 */
public class MedicationListAdapter extends BaseAdapter {
    private ArrayList<MedicationObject> mainList;
    private Context context;

    public MedicationListAdapter(Context applicationContext,
                                 List<MedicationObject> medList) {

        super();
        this.context = applicationContext;
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
        MedicationObject medicationObject = (MedicationObject) getItem(position);

        if (medicationObject != null) {
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
                brandNameView.setText(medicationObject.get_brandName());
            }
            if (genericNameView != null) {
                genericNameView.setText(medicationObject.get_genericName());
            }
            if (perDosageView != null) {
                perDosageView.setText(medicationObject.get_perDosage());
            }
            if (dosageFormView != null) {
                dosageFormView.setText(medicationObject.get_dosageForm());
            }
            if (totalDosageView != null) {
                totalDosageView.setText(medicationObject.get_totalDosage());
            }
            if (consumptionTimeView != null) {
                consumptionTimeView.setText(medicationObject.get_consumptionTime());
            }
            final String[] consumptionTimeArray = medicationObject.get_consumptionTime().split(", ");
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
                                                LayoutInflater inflater = (LayoutInflater) context
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View view = inflater.inflate(R.layout.dialog_list, null);

                                                ListView lv = (ListView) view.findViewById(R.id.custom_list);

                                                // Change MyActivity.this and myListOfItems to your own values
                                                ConsumptionTimeListAdapter consumptionTimeAdapter =
                                                        new ConsumptionTimeListAdapter(context, new ArrayList<String>(Arrays.asList(consumptionTimeArray)));

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
}
