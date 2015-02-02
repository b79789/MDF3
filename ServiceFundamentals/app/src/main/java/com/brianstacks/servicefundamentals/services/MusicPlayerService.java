/**
 *Created by Brian Stacks
 on 1/27/15
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals.services;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.fragments.UIFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MusicPlayerService extends Service implements MediaPlayer.OnErrorListener{

    private static final String DEBUG_TAG = "MusicPlayerService";
    public static final int NOTIFICATION_ID = 0x01001;
    MediaPlayer mPlayer;
    ArrayList<String> trackList=new ArrayList<>();
    NotificationManager mManager;
    private int currentTrack = 0;
    final String lineSep = System.getProperty("line.separator");
    final String uri1 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.darkhorse;
    final String uri2 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.turndatide;
    final String uri3 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.trophies;
    final String uri4 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.onemorenight;
    final String[] artistText={"Katy Perry"+lineSep+"Dark Horse","Arab Muziac"+lineSep+"TurnDaTide","Drake"+lineSep+"Trophies","Adam Levine"+lineSep+"One More Night"};
    final String[] tracks = {uri1, uri2, uri3, uri4};


    public class BoundServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(DEBUG_TAG, "In onBind with intent=" + intent.getAction());
        if(intent.hasExtra(UIFragment.RC_INTENT)) {
            Toast.makeText(getApplicationContext(),"WE hit the receiver",Toast.LENGTH_SHORT).show();
            ResultReceiver receiver = intent.getParcelableExtra(UIFragment.RC_INTENT);
            Bundle result = new Bundle();
            result.putString(UIFragment.DATA_RETURNED,"BOUND" );
            receiver.send(UIFragment.RESULT_DATA_RETURNED, result);
        }

        return new BoundServiceBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {

        return false;
    }


    public void onCreate() {
        super.onCreate();
        Log.d("LOG", "Service Started!");
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("Artist");
        builder.setContentText("Title");

        startForeground(NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "In onDestroy.");
        mPlayer.release();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Collections.addAll(trackList, tracks);
        Uri file = Uri.parse(tracks[this.currentTrack]);
        if (mPlayer == null ){
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(this, file);
                mPlayer.prepareAsync();
                if(intent.hasExtra(UIFragment.RC_INTENT)) {
                    Toast.makeText(getApplicationContext(),"WE hit the receiver",Toast.LENGTH_SHORT).show();
                    ResultReceiver receiver = intent.getParcelableExtra(UIFragment.RC_INTENT);
                    Bundle result = new Bundle();
                    result.putString(UIFragment.DATA_RETURNED,"Artist" +" "+"Title:::" );
                    receiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                }
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mPlayer=mp;
                        mPlayer.start();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        currentTrack = (currentTrack + 1) % tracks.length;
                        if (currentTrack >= 0) {

                            Uri nextTrack = Uri.parse(tracks[currentTrack]);
                            mPlayer.reset();
                            try {
                                mPlayer.setDataSource(getApplicationContext(), nextTrack);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mPlayer.prepareAsync();

                        }
                    }
                });
                mPlayer.setOnErrorListener(this);

            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Player failed", e);
            }
        }else{

            mPlayer.setOnErrorListener(this);
        }

      if(intent.hasExtra(UIFragment.RC_INTENT)) {
            Toast.makeText(getApplicationContext(),"WE hit the receiver",Toast.LENGTH_SHORT).show();
            ResultReceiver receiver = intent.getParcelableExtra(UIFragment.RC_INTENT);
            Bundle result = new Bundle();
            result.putString(UIFragment.DATA_RETURNED,"Artist" +" "+"Title:::" );
            receiver.send(UIFragment.RESULT_DATA_RETURNED, result);
        }

        return START_STICKY;
    }



    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    public void onPause() {
        mPlayer.pause();
    }

    public void onPlay() {

        mPlayer.start();
        }

    public void onStop()  {
        mPlayer.reset();
    }

    public void onSkipForward() {
        currentTrack = (currentTrack + 1) % tracks.length;
        if (currentTrack>=0){
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
        }else {
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(),Uri.parse(tracks[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
        }
    }

    public void onSkipback() {
        currentTrack = (currentTrack - 1) % tracks.length;
        if (currentTrack >= 0) {
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
        }
    }

}