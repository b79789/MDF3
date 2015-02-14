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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brianstacks.servicefundamentals.MainActivity;
import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.services.MusicPlayerService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UIFragment extends Fragment  {

    public static final String TAG = "UIFragment.TAG";
    public static final String MAX_KEY = "MaxDuration";
    public static final String PROGRESS_KEY = "CurrentProgress";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;
    public static final String RC_INTENT = "com.brianstacks..servicefundamentals.RC_INTENT";
    public static final String P_INTENT = "com.brianstacks..servicefundamentals.P_INTENT";
    private static final String KEY_CURRENT_PROGRESS = "current_progress";
    private static final String KEY_TEXT = "percent_progress";
    TextView mTextView;
    boolean mBound = false;
    MusicPlayerService musicPlayerService;
    Intent intent;
    private SeekBar mProgress;
    private final Handler mHandler = new Handler();



    public UIFragment() {
        // Required empty public constructor
    }


    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicPlayerService.BoundServiceBinder binder = (MusicPlayerService.BoundServiceBinder) service;
            musicPlayerService= binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBound = false;
            Toast.makeText(getActivity().getApplicationContext(), "Disconnected" + name.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "In On Create");
        intent = new Intent(getActivity().getApplicationContext(), MusicPlayerService.class);
        intent.putExtra(RC_INTENT,new DataReceiver());
        intent.putExtra(P_INTENT,new progressReceiver());
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
        mProgress=(SeekBar)getActivity().findViewById(R.id.myProgress);
        Button mStartService = (Button)getActivity().findViewById(R.id.startService);
        Button mPlay= (Button)getActivity().findViewById(R.id.playButton);
        Button mPause= (Button)getActivity().findViewById(R.id.pauseButton);
        Button mStop= (Button)getActivity().findViewById(R.id.stopButton);
        Button mSkipF = (Button)getActivity().findViewById(R.id.skipForward);
        Button mSkipB= (Button)getActivity().findViewById(R.id.skipBack);
        Button mStopService= (Button)getActivity().findViewById(R.id.stopService);
        ToggleButton randomButton = (ToggleButton)getActivity().findViewById(R.id.randButton);
        if (musicPlayerService != null){
            musicPlayerService.setReceiver();
        }

        //mProgress.setMax();
        mStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(intent);
            }
        });
        mStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().stopService(intent);
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
                    musicPlayerService.onSkipBack();
                }
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlayerService != null) {
                    // The toggle is enabled
                    musicPlayerService.randomPlay();
                } else {
                    Log.d(" randomButton", " musicPlayerService == null");
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
    public void onResume() {
        super.onResume();
        if (!mBound) {

            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        } else {
            Log.v("error", "In frags on onResume and was still bound");

        }

    }




    public class progressReceiver extends ResultReceiver {
        public progressReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultData != null && resultData.containsKey(DATA_RETURNED)) {
                if (mProgress!=null){
                    mProgress.setMax(resultData.getInt(MAX_KEY));
                    mProgress.setProgress(resultData.getInt(PROGRESS_KEY));
                }


            }
        }
    }

    public class DataReceiver extends ResultReceiver {
        public DataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultData != null && resultData.containsKey(DATA_RETURNED)) {
                if (mTextView!=null){
                    mTextView.setText(resultData.getString(DATA_RETURNED, ""));

                }else {
                    Log.d("TextView","= null");

                }
            }
        }
    }

}
