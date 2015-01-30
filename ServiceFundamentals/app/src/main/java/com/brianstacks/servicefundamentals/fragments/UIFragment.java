/**
 * Created by Brian Stacks
 on 1-27-2015
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals.fragments;


import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brianstacks.servicefundamentals.MainActivity;
import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.services.MusicPlayerService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UIFragment extends Fragment {

    public static final String TAG = "UIFragment.TAG";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;

    Button mPlay;
    Button mPause;
    Button mStop;
    Button mSkipF;
    Button mSkipB;
    Button mStartService;
    Button mStopService;
    MusicPlayerService musicPlayerService;

    public static  ServiceConnection mConnection;
    boolean mBound = false;
    TextView mTextView;
    DataReceiver resultReceiver;


    public UIFragment() {
        // Required empty public constructor
    }


    public static UIFragment newInstance() {
        UIFragment fragment = new UIFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultReceiver = new DataReceiver();
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.putExtra("receiver", resultReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ui, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        // get an instance of my xml elements
        mStartService = (Button)getActivity().findViewById(R.id.startService);
        mPlay= (Button)getActivity().findViewById(R.id.playButton);
        mPause= (Button)getActivity().findViewById(R.id.pauseButton);
        mStop= (Button)getActivity().findViewById(R.id.stopButton);
        mSkipF = (Button)getActivity().findViewById(R.id.skipForward);
        mSkipB= (Button)getActivity().findViewById(R.id.skipBack);
        mStopService= (Button)getActivity().findViewById(R.id.stopService);
        mTextView=(TextView)getActivity().findViewById(R.id.trackText);

        // create an intent
        final Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.putExtra(DATA_RETURNED, new DataReceiver());

        mStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
        mStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicPlayerService.class);
                getActivity().unbindService(mConnection);
                musicPlayerService.stopService(intent);
                Toast.makeText(getActivity(), "Stopped it", Toast.LENGTH_SHORT).show();

            }
        });
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    //musicPlayerService.showToast();
                    musicPlayerService.onPlay();
                }

            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayerService.onPause();

            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayerService.onStop();
            }
        });
        mSkipF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayerService.onSkipForward();
            }
        });
        mSkipB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayerService.onSkipback();
            }
        });
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                MusicPlayerService.BoundServiceBinder binder = (MusicPlayerService.BoundServiceBinder) service;
                musicPlayerService= binder.getService();
                mBound = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // Bind to LocalService
        Intent intent = new Intent(getActivity().getApplicationContext(), MusicPlayerService.class);
        intent.putExtra("receiver", resultReceiver);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }


    private final Handler mHandler = new Handler();

    public class DataReceiver extends ResultReceiver {
        public DataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultData != null && resultData.containsKey(DATA_RETURNED)) {
                mTextView.setText(resultData.getString(DATA_RETURNED, "works"));
            }
        }
    }



}
