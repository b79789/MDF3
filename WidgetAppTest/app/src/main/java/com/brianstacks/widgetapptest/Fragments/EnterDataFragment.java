/**
 *Created by Brian Stacks
 on 2/9/15
 for FullSail.edu.
 */
package com.brianstacks.widgetapptest.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.brianstacks.widgetapptest.EnteredData;
import com.brianstacks.widgetapptest.R;


public class EnterDataFragment extends Fragment {
    public static final String TAG = "EnterDataFragment.TAG";
    private OnFragmentInteractionListener mListener;


    public static EnterDataFragment newInstance() {
        return new EnterDataFragment();
    }

    public EnterDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_data, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle _savedInstanceState) {
        super.onActivityCreated(_savedInstanceState);
        Button addButton;
        addButton = (Button) getActivity().findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnteredData enteredData = new EnteredData();
                EditText e1 = (EditText) getActivity().findViewById(R.id.e1);
                EditText e2 = (EditText) getActivity().findViewById(R.id.e2);
                EditText e3 = (EditText) getActivity().findViewById(R.id.e3);
                e1.requestFocus();
                if (e1.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Must enter name");
                    builder1.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else if (e2.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Must enter age");
                    builder1.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else if (e3.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Must enter eye color");
                    builder1.setPositiveButton("Exit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(e1, InputMethodManager.SHOW_IMPLICIT);

                    enteredData.setName(e1.getText().toString());
                    enteredData.setAge(e2.getText().toString());
                    enteredData.setEyeColor(e3.getText().toString());
                    mListener.onFragmentInteraction2(enteredData);
                }
            }
        });
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


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction2(EnteredData enteredData);

    }
}