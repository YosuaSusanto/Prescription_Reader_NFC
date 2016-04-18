package com.example.reico_000.prescriptionreadernfc;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrescriptionDateList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrescriptionDateList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrescriptionDateList extends Fragment {
    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView PrescriptionDateList;
    Communicator comm;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrescriptionDateList.
     */
    // Rename and change types and number of parameters
    public static PrescriptionDateList newInstance(String param1, String param2) {
        PrescriptionDateList fragment = new PrescriptionDateList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PrescriptionDateList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PrescriptionDateList", "PrescriptionDateList Fragment was created");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pres_date_list, container, false);
        // Inflate the layout for this fragment
        PrescriptionDateList = (ListView) view.findViewById(R.id.presDateList);

        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        registerListClickCallback();
    }
    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_legends).setVisible(false);
        menu.findItem(R.id.action_report).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
        // Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    //    public void populateList(CursorAdapter cursorAdapter){
    public void populateList(PrescriptionDateListAdapter prescriptionDateListAdapter){
        Log.d("Test", "setAdapter entered");

        PrescriptionDateList.setAdapter(prescriptionDateListAdapter);
//        InventoryList.setAdapter(cursorAdapter);
    }

    private void registerListClickCallback() {
        Log.d("Item click", "Entered Function");
        PrescriptionDateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long idInDB) {

                Log.d("Item click", "ID =" + idInDB);
                comm.displayToastForId(idInDB);
                //comm.test();
            }
        });
        PrescriptionDateList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View viewClicked,
                                           int position, long idInDB) {

                Log.d("Item Long Click", "Entered");
                comm.deleteId(idInDB);

                return true;
            }
        });
    }
}
