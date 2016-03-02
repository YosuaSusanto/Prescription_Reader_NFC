package com.example.reico_000.prescriptionreadernfc;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Scan.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Scan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Scan extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView brandNameTextView;
    private TextView genericNameTextView;
    private TextView dosageFormTextView;
    private TextView perDosageTextView;
    private TextView totalDosageTextView;
    private TextView consumptionTimeTextView;

    private ImageView scan_Morning;
    private ImageView scan_Afternoon;
    private ImageView scan_Evening;
    private ImageView scan_Before_Sleep;

    private Button consumeButton;
    private Button resetButton;
    Communicator comm;
    private Inventory.OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Scan.
     */
    // TODO: Rename and change types and number of parameters
    public static Scan newInstance(String param1, String param2) {
        Scan fragment = new Scan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Scan() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug","on create fragment");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        else{
            Log.d("debug","arguments is null");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        brandNameTextView = (TextView) view.findViewById(R.id.scan_Brand_Name);
        genericNameTextView = (TextView) view.findViewById(R.id.scan_Generic_Name);
        dosageFormTextView = (TextView) view.findViewById(R.id.scan_DosageForm);
        perDosageTextView = (TextView) view.findViewById(R.id.scan_PerDosage);
        totalDosageTextView = (TextView) view.findViewById(R.id.scan_TotalDosage);
        consumptionTimeTextView = (TextView) view.findViewById(R.id.scan_ConsumptionTime);

        consumeButton = (Button) view.findViewById(R.id.consumebutton);
        resetButton = (Button) view.findViewById(R.id.resetbutton);

        consumeButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Inventory.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.consumebutton) {
            Log.d("Scan - Consume", "Consume Function In");
            comm.respondConsumeMed();

        } else if (id ==R.id.resetbutton) {

        }
        comm.respondReset();
        ClearTextViews();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void changeText(String brandName, String genericName, String dosageForm, String perDosage, String totalDosage, String consumptionTime) {
        Log.d("MainActivity", "Set Text: " + brandName);
        brandNameTextView.setText(brandName);
        genericNameTextView.setText(genericName);
        dosageFormTextView.setText(dosageForm);
        perDosageTextView.setText(perDosage);
        totalDosageTextView.setText(totalDosage);
        consumptionTimeTextView.setText(consumptionTime);
    }

    public void ClearTextViews() {
        brandNameTextView.setText("");
        genericNameTextView.setText("");
        dosageFormTextView.setText("");
        perDosageTextView.setText("");
        totalDosageTextView.setText("");
        consumptionTimeTextView.setText("");
        scan_Morning.setVisibility(View.INVISIBLE);
        scan_Afternoon.setVisibility(View.INVISIBLE);
        scan_Evening.setVisibility(View.INVISIBLE);
        scan_Before_Sleep.setVisibility(View.INVISIBLE);
    }
}
