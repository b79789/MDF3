package com.brianstacks.widgetassignment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class DetailsFragment extends Fragment {

    public static final String TAG = "DetailsFrag.TAG";
    private static final String ARG_Name = "name";
    private static final String ARG_Age = "age";
    private static final String ARG_Eye = "eye";

    private String mName;
    private String mAge;


    public static DetailsFragment newInstance() {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();



        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_Name);
            mAge = getArguments().getString(ARG_Age);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }


    @Override
    public void onActivityCreated(final Bundle _savedInstanceState) {
        super.onActivityCreated(_savedInstanceState);
        Intent intent =getActivity().getIntent();
        intent.getExtras();
        EnteredData enteredData = (EnteredData) intent.getSerializableExtra(ARG_Name);
        if (enteredData != null) {

            setDisplayInfo(enteredData.getName(), enteredData.getAge(),enteredData.getEyeColor());


        } else {

            setDisplayInfo("No", "data","!");
        }
        Button closeButton = (Button)getActivity().findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = getFragmentManager().getBackStackEntryCount();

                if (count == 0) {
                    getActivity().onBackPressed();
                    //additional code
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    public void setDisplayInfo(String name, String age,String eye) {



        // Get our TextView and set some text to it.
        TextView t1 = (TextView) getActivity().findViewById(R.id.text1);
        TextView t2 = (TextView) getActivity().findViewById(R.id.text2);
        TextView t3 = (TextView) getActivity().findViewById(R.id.text3);

        t1.setText(name);
        t2.setText(age);
        t3.setText(eye);

    }

}
