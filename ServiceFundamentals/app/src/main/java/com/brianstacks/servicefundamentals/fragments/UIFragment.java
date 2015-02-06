/**
 * Created by Brian Stacks
 on 1-27-2015
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

    public static final String TAG = "UIFragment.TAG";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;
    public static final String RC_INTENT = "com.brianstacks..servicefundamentals.RC_INTENT";
    MusicPlayerService musicPlayerService;
    boolean mBound = false;
    TextView mTextView;

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
        Log.v(TAG, "In frags on create view");
        /*        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTextView=(TextView)getActivity().findViewById(R.id.trackText);
        // then you use
        if(mTextView != null)
        {
            String savedText = prefs.getString("TextviewsText", "");
            mTextView.setText(savedText);
        }*/

        return inflater.inflate(R.layout.fragment_ui, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        if (savedInstance != null) {
            Toast.makeText(getActivity(),"Saved Instance is not null",Toast.LENGTH_SHORT).show();
            // Restore last state for checked position.
            /*String testing = String.valueOf(savedInstance.getCharSequence("TextviewsText", ""));
            Log.d("textViewText",testing);*/
        }
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
                intent.putExtra(RC_INTENT,new DataReceiver());
                getActivity().startService(intent);
            }
        });
        mStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicPlayerService.class);
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

        Log.v(TAG, "In frags on onPause");
        mTextView = (TextView)getActivity().findViewById(R.id.trackText);
        if (mTextView.getText() != ""){
            Log.v(TAG, "Text is not blank");
            getActivity().getIntent().putExtra("TextviewsText",mTextView.getText());
        }

        if (mBound ){
            getActivity().unbindService(mConnection);
            mBound=false;

        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "In frags on onResume");
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
         intent.putExtra(RC_INTENT,new DataReceiver());
        mTextView =(TextView)getActivity().findViewById(R.id.trackText);
        if (getActivity().getIntent()!=null){
            Log.v(TAG, String.valueOf(getActivity().getIntent().getCharSequenceExtra("TextviewsText")));
        }
       // mTextView.setText(getActivity().getIntent().getCharSequenceExtra("TextviewsText"));
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "In frags on onDestroy");

    }


    private final Handler mHandler = new Handler();


    public class DataReceiver extends ResultReceiver {
        public DataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultData != null && resultData.containsKey(DATA_RETURNED)) {
                if (getActivity()!=null){
                    mTextView=(TextView)getActivity().findViewById(R.id.trackText);
                    mTextView.setText(resultData.getString(DATA_RETURNED, "works"));
                    resultData.putCharSequence("TextviewsText",mTextView.getText());
                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sharedPrefs.edit().putString("TextviewsText", String.valueOf(mTextView.getText())).apply();
                }else {
                    Log.d("Activity","= null");

                }

            }
        }
    }






}
