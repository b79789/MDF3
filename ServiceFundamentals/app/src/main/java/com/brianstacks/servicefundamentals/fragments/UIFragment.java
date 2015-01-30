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
    public static final String RC_INTENT = "com.brianstacks..servicefundamentals.RC_INTENT";
    public static final String DATA_REC = "receiver";
    MusicPlayerService musicPlayerService;
    boolean mBound = false;

    public static UIFragment newInstance() {
        UIFragment fragment = new UIFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public UIFragment() {
        // Required empty public constructor
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicPlayerService.BoundServiceBinder binder = (MusicPlayerService.BoundServiceBinder) service;
            musicPlayerService= binder.getService();
            mBound = true;
            Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBound = false;
            Toast.makeText(getActivity(), "Disconnected" + name.toString(), Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

         Intent intent = new Intent(getActivity(), MusicPlayerService.class);

        // get an instance of my xml elements
        Button mStartService = (Button)getActivity().findViewById(R.id.startService);
        Button mPlay= (Button)getActivity().findViewById(R.id.playButton);
        Button mPause= (Button)getActivity().findViewById(R.id.pauseButton);
        Button mStop= (Button)getActivity().findViewById(R.id.stopButton);
        Button mSkipF = (Button)getActivity().findViewById(R.id.skipForward);
        Button mSkipB= (Button)getActivity().findViewById(R.id.skipBack);
        Button mStopService= (Button)getActivity().findViewById(R.id.stopService);
        mStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicPlayerService.class);

                getActivity().startService(intent);
            }
        });
        mStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    getActivity().unbindService(mConnection);
                    getActivity().stopService(getActivity().getIntent());
                }
                Toast.makeText(getActivity(),"Not Bound",Toast.LENGTH_SHORT).show();
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
                if (mBound) {
                    musicPlayerService.onPause();
                }

            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    musicPlayerService.onStop();
                }
            }
        });
        mSkipF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    musicPlayerService.onSkipForward();
                }
            }
        });
        mSkipB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    musicPlayerService.onSkipback();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBound ){
            getActivity().unbindService(mConnection);
            mBound=false;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
         Intent intent = new Intent(getActivity(), MusicPlayerService.class);

        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }



  /*  private final Handler mHandler = new Handler();


    public class DataReceiver extends ResultReceiver {
        public DataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultData != null && resultData.containsKey(DATA_RETURNED)) {
                TextView mTextView=(TextView)getActivity().findViewById(R.id.trackText);
                mTextView.setText(resultData.getString(DATA_RETURNED, "works"));
            }
        }
    }*/





}
