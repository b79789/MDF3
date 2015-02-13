/**
 * Created by Brian Stacks
 on 1-27-2015
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.services.MusicPlayerService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UIFragment extends Fragment  {

    private OnFragmentInteractionListener mListener;
    public static final String TAG = "UIFragment.TAG";
    public static final String MAX_KEY = "MaxDuration";
    public static final String PROGRESS_KEY = "CurrentProgress";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;
    public static final String RC_INTENT = "com.brianstacks..servicefundamentals.RC_INTENT";
    public static final String P_INTENT = "com.brianstacks..servicefundamentals.P_INTENT";
    TextView mTextView;







    public UIFragment() {
        // Required empty public constructor
    }

    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("onSaveInstanceState","In On onSaveInstanceState");

        // Remember the current text, to restore if we later restart.
        outState.putCharSequence("text",mTextView.getText());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","In On Create");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreateView","In On onCreateView");
        return inflater.inflate(R.layout.fragment_ui, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d("onActivityCreated","In On onActivityCreated");
        mTextView=(TextView)getActivity().findViewById(R.id.trackText);

        if (savedInstanceState != null) {
            // Do something with value if needed
            mTextView.setText(savedInstanceState.getCharSequence("text"));
        }

        mListener.onFragmentInteraction();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause","In On onPause");


    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("onResume","In On onResume");

    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("onAttach","In On onAttach");
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction( );

    }



}
