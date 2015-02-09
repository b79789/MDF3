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
import android.widget.TextView;
import android.widget.Toast;
import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.services.MusicPlayerService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UIFragment extends Fragment {

    public final String TEXT_KEY = "text";

    public static final String TAG = "UIFragment.TAG";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;
    public static final String RC_INTENT = "com.brianstacks..servicefundamentals.RC_INTENT";
    private final Handler mHandler = new Handler();
    MusicPlayerService musicPlayerService;
    boolean mBound = false;
    TextView mTextView;
    DataReceiver dataReceiver;
    Intent intent;
    SharedPreferences sharedPrefs;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicPlayerService.BoundServiceBinder binder = (MusicPlayerService.BoundServiceBinder) service;
            musicPlayerService= binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBound = false;
            Toast.makeText(getActivity(), "Disconnected" + name.toString(), Toast.LENGTH_SHORT).show();
        }
    };



    public UIFragment() {
        // Required empty public constructor
    }

    public static UIFragment newInstance() {
        UIFragment fragment = new UIFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TEXT_KEY, mTextView.getText());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataReceiver = new DataReceiver();
        intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.putExtra(RC_INTENT,new DataReceiver());
        // Retain this fragment across configuration changes.
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTextView.setText(savedInstanceState.getCharSequence(TEXT_KEY));
            // Do something with value if needed
        }
        return inflater.inflate(R.layout.fragment_ui, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);

        // get an instance of my xml elements
        mTextView=(TextView)getActivity().findViewById(R.id.trackText);
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

                intent = new Intent(getActivity(), MusicPlayerService.class);
                intent.putExtra(RC_INTENT,new DataReceiver());
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
                    musicPlayerService.onSkipback();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mTextView=(TextView)getActivity().findViewById(R.id.trackText);
/*        sharedPrefs = getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);

        sharedPrefs.edit().putString("textViewText", String.valueOf(mTextView.getText())).apply();
        Log.v(TAG, "In frags on onPause"+" " +sharedPrefs.getString("textViewText","text"));*/
        if (mBound ){
            getActivity().unbindService(mConnection);
            mBound=false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.putExtra(RC_INTENT,new DataReceiver());
        if (!mBound){
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }else {
            Log.v(TAG, "In frags on onResume and was still bound");

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
