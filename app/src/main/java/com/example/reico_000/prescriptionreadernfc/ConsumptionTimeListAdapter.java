package com.example.reico_000.prescriptionreadernfc;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yosua Susanto on 3/2/2016.
 */
public class ConsumptionTimeListAdapter extends BaseAdapter {
    private ArrayList<String> mainList;
    private Context context;
    private FragmentActivity fragmentContext;

    public ConsumptionTimeListAdapter(Context applicationContext,
                                 List<String> consumptionTimeList) {

        super();
        this.fragmentContext = (FragmentActivity) applicationContext;
        this.context = applicationContext;
        this.mainList = new ArrayList<String>(consumptionTimeList);
    }

    public ConsumptionTimeListAdapter() {

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
            convertView = inflater.inflate(R.layout.consumption_time_picker, null);
        }
        final String time = (String) getItem(position);

        if (time != null) {
            TextView timeTextView = (TextView) convertView
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
                                DialogFragment newFragment = new TimePickerFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("hour", Integer.parseInt(time.substring(0,2)));
                                bundle.putInt("minute", Integer.parseInt(time.substring(3)));
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
}
