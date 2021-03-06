/**
 *Created by Brian Stacks
 on 1/27/15
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals.services;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.brianstacks.servicefundamentals.MainActivity;
import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.fragments.UIFragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MusicPlayerService extends Service implements MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{

    public static final int NOTIFICATION_ID = 0x01001;
    private static final String DEBUG_TAG = "MusicPlayerService";
    final String lineSep = System.getProperty("line.separator");
    final String[] tracks = {"android.resource://com.brianstacks.servicefundamentals/" + R.raw.darkhorse,
            "android.resource://com.brianstacks.servicefundamentals/" + R.raw.turndatide,
            "android.resource://com.brianstacks.servicefundamentals/" + R.raw.trophies,
            "android.resource://com.brianstacks.servicefundamentals/" + R.raw.onemorenight};
    final String[] artist = {"Katy Perry","Arab Muzic","Drake","Adam Levine"};
    final String[] title = {"Dark Horse","Instrumental","Trophies","One More Night"};
    final int[]pics ={R.drawable.katy,R.drawable.araab,R.drawable.trophie,R.drawable.maroon};
    MediaPlayer mPlayer;
    ResultReceiver resultReceiver;
    ResultReceiver progressReciever;
    Bundle result;
    private int currentTrack = 0;
    private int progressStatus = 0;
    Handler handler;
    boolean isRandom = false;
    boolean isActive = true;

    private Runnable r = new Runnable() {

        public void run() {
            if (result != null) {
                result.putInt(UIFragment.MAX_KEY, mPlayer.getDuration());
                result.putInt(UIFragment.PROGRESS_KEY, mPlayer.getCurrentPosition());
                progressReciever.send(UIFragment.RESULT_DATA_RETURNED, result);

            } else {
                Log.d("Player", "is null");
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        resultReceiver = intent.getParcelableExtra(UIFragment.RC_INTENT);
        progressReciever=intent.getParcelableExtra(UIFragment.P_INTENT);
        return new BoundServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        isActive=false;
        handler.removeCallbacks(r);
        if (mPlayer !=null){
            mPlayer.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        ArrayList<String> trackList=new ArrayList<>();
        result = new Bundle();
        resultReceiver= intent.getParcelableExtra(UIFragment.RC_INTENT);
        progressReciever= intent.getParcelableExtra(UIFragment.P_INTENT);
        Collections.addAll(trackList, tracks);
        Uri file = Uri.parse(tracks[this.currentTrack]);
        if (mPlayer == null ){
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(this, file);
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Player failed", e);
            }
            mPlayer.prepareAsync();
            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        }else{
            Log.d("Error ","Media player is not null but should be");
        }
        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
        isActive=true;
        if (currentTrack >= 0 && currentTrack <4) {
            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
            // give the bundle to the results receiver
            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            // Start long running operation in a background thread
            handler=new Handler();
            handler.postDelayed(r, 1000);
            showNotification();
        }
        else {
            Log.d("Error","track is wrong");
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        isActive=false;
        stopForeground(true);
        if (isRandom){
            currentTrack=getRandomNumber(tracks.length);
        }else {
            currentTrack = (currentTrack + 1) % tracks.length;
        }
        if (currentTrack >= 0 && currentTrack <4) {
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
            // put track details in the bundle
            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
            // give the bundle to the results receiver
            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            showNotification();
        }else {
            Log.d("Error onCompletion"," track size error");
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    public void onPause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }else{
            Toast.makeText(this,"The media player isn't playing",Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlay() {

        mPlayer.start();

    }

    public void onStop()  {
        if (mPlayer!=null){
            mPlayer.stop();
        }else{
            Toast.makeText(this,"The media player isn't playing",Toast.LENGTH_SHORT).show();
        }
    }

    public void onSkipForward() {
        if (isRandom){
            currentTrack=getRandomNumber(tracks.length);
        }else {
            currentTrack = (currentTrack + 1);
        }
        if (currentTrack>=0 && currentTrack<4){
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
            // put track details in the bundle
            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
            // give the bundle to the results receiver
            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            showNotification();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);

        }else {
            Toast.makeText(this,"End of track list",Toast.LENGTH_SHORT).show();
        }
    }

    public void onSkipBack() {

        if (isRandom){
            currentTrack=getRandomNumber(tracks.length);
        }else {
            currentTrack = (currentTrack - 1);
        }
        if (currentTrack >= 0  && currentTrack<4) {
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
            // put track details in the bundle
            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
            // give the bundle to the results receiver
            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            showNotification();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        }else {
            Toast.makeText(this,"Beginning of track list",Toast.LENGTH_SHORT).show();
        }
    }

    public int getRandomNumber(int numberOfElements) {
        java.util.Random rnd = new java.util.Random();
        return rnd.nextInt(numberOfElements);
    }

    public void randomPlay() {
        isRandom^=true;
        currentTrack = getRandomNumber(tracks.length);
        Log.d("isRandom", String.valueOf(isRandom));
    }

    public void showNotification(){
        Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
        getActivityIntent.setAction(Intent.ACTION_MAIN);
        getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // create pendingIntent for notification
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
        // create notification and give it's properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_stat_one);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),pics[currentTrack]));
        builder.setContentTitle(artist[currentTrack]);
        builder.setContentText(title[currentTrack]);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    public class BoundServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    public void setReceiver(){
        // put track details in the bundle
        result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
        // give the bundle to the results receiver
        resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
        handler=new Handler();
        handler.postDelayed(r, 1000);
    }
}