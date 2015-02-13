/**
 * Created by Brian Stacks
 on 1-27-2015
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brianstacks.servicefundamentals.fragments.UIFragment;
import com.brianstacks.servicefundamentals.services.MusicPlayerService;

import static com.brianstacks.servicefundamentals.fragments.UIFragment.*;


public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener{



    TextView mTextView;
    boolean mBound = false;
    MusicPlayerService musicPlayerService;
    Intent intent;
    private SeekBar mProgress;
    private final Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            UIFragment uiFragment = new UIFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment_container,uiFragment).commit();
        }
        mTextView= (TextView) findViewById(R.id.trackText);
        intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.putExtra(RC_INTENT,new DataReceiver());
        intent.putExtra(P_INTENT,new progressReceiver());


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
            Toast.makeText(getApplicationContext(), "Disconnected" + name.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        mTextView=(TextView)findViewById(R.id.trackText);
        if (mBound ){
            unbindService(mConnection);
            mBound=false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!mBound){
            intent = new Intent(getApplicationContext(), MusicPlayerService.class);
            intent.putExtra(RC_INTENT,new DataReceiver());
            intent.putExtra(P_INTENT,new progressReceiver());
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }else {
            Log.v("error", "In frags on onResume and was still bound");

        }




    }

    @Override
    public void onFragmentInteraction() {
        // get an instance of my xml elements
        mTextView=(TextView)findViewById(R.id.trackText);
        Button mStartService = (Button)findViewById(R.id.startService);
        Button mPlay= (Button)findViewById(R.id.playButton);
        Button mPause= (Button)findViewById(R.id.pauseButton);
        Button mStop= (Button)findViewById(R.id.stopButton);
        Button mSkipF = (Button)findViewById(R.id.skipForward);
        Button mSkipB= (Button)findViewById(R.id.skipBack);
        Button mStopService= (Button)findViewById(R.id.stopService);
        ToggleButton randomButton = (ToggleButton)findViewById(R.id.randButton);
        mProgress=(SeekBar)findViewById(R.id.myProgress);

        //mProgress.setMax();
        mStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(getApplicationContext(), MusicPlayerService.class);
                intent.putExtra(RC_INTENT,new DataReceiver());
                intent.putExtra(P_INTENT,new progressReceiver());
                startService(intent);

            }
        });
        mStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(intent);
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

    public class progressReceiver extends ResultReceiver {
        public progressReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultData != null && resultData.containsKey(DATA_RETURNED)) {
                mProgress.setMax(resultData.getInt(MAX_KEY));
                mProgress.setProgress(resultData.getInt(PROGRESS_KEY));

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
