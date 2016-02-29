package com.example.reico_000.prescriptionreadernfc;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import helper.DataTransferInterface;

/**
 * Created by Yosua Susanto on 3/2/2016.
 */
public class ConsumptionTimeListAdapter extends BaseAdapter {
    private ArrayList<String> mainList;
    private Context context;
    private FragmentActivity fragmentContext;
    private String oldTime, newTime;
    private TextView timeTextView;
    DataTransferInterface dtInterface;

    public ConsumptionTimeListAdapter(Context applicationContext,
                                 List<String> consumptionTimeList, DataTransferInterface dtInterface) {

        super();
        this.fragmentContext = (FragmentActivity) applicationContext;
        this.context = applicationContext;
        this.dtInterface = dtInterface;
        this.mainList = new ArrayList<String>(consumptionTimeList);
    }

    public ConsumptionTimeListAdapter() {

        super();
//        this.mainList = QuestionForSliderMenu;

    }

    public void setList(List<String> newConsumptionTimeList) {
        this.mainList = new ArrayList<String>(newConsumptionTimeList);
    }
    @Override
    public int getCount() {

        return mainList.size();
    }

    public void setItem(int position, Object item) {
        mainList.set(position, (String) item);
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
            convertView = inflater.inflate(R.layout.consumption_time_picker, null);
        }
        final String time = (String) getItem(position);

        if (time != null) {
            timeTextView = (TextView) convertView
                    .findViewById(R.id.timeTextView);
            Button changeTimingButton = (Button) convertView
                    .findViewById(R.id.changeTimingButton);

            if (timeTextView != null) {
                timeTextView.setText(time);
            }


            try {
//                tv1.setText(" List Item "+ " : " + position);
                changeTimingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.changeTimingButton:
                                int hour = Integer.parseInt(time.substring(0,2));
                                int min = Integer.parseInt(time.substring(3));
                                oldTime = time;
                                DialogFragment newFragment = new TimePickerFragment(mTimeSetListener);
                                Bundle bundle = new Bundle();
                                bundle.putInt("hour", hour);
                                bundle.putInt("minute", min);

                                newFragment.setArguments(bundle);
                                newFragment.show(fragmentContext.getFragmentManager(), "timePicker");
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
    TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(android.widget.TimePicker view,
                                      int hourOfDay, int minute) {
                    Log.d("TimePicker", "onTimeSet in adapter entered...");

                    if (hourOfDay < 10) {
                        newTime = "0" + hourOfDay + ":";
                    } else {
                        newTime = hourOfDay + ":";
                    }
                    if (minute < 10) {
                        newTime += "0" + minute;
                    } else {
                        newTime += minute;
                    }
                    ArrayList<String> tempList = new ArrayList<String>(Arrays.asList(oldTime, newTime));
                    timeTextView.setText(newTime);
                    dtInterface.setValues(tempList);
//                    mainList.remove((String) oldTime);
//                    mainList.add((String) newTime);
                }
            };
//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        // Do something with the time chosen by the user
//        Log.d("TimePicker", "onTimeSet in adapter entered...");
//        newTime = hourOfDay + ":" + minute;
//        mainList.remove((String) oldTime);
//        mainList.add((String) newTime);
//    }
}
