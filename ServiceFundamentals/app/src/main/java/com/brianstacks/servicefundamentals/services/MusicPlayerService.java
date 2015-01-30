/**
 *Created by Brian Stacks
 on 1/27/15
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.brianstacks.servicefundamentals.MainActivity;
import com.brianstacks.servicefundamentals.R;
import com.brianstacks.servicefundamentals.fragments.UIFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{

    private static final String DEBUG_TAG = "MusicPlayerService";
    public static final int NOTIFICATION_ID = 0x01001;
    public MediaPlayer mPlayer;
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

        /*if(intent.hasExtra(UIFragment.DATA_RETURNED)) {
            ResultReceiver receiver = (ResultReceiver)intent.getParcelableExtra(UIFragment.DATA_RETURNED);
            Bundle result = new Bundle();
            result.putString(UIFragment.DATA_RETURNED,"Artist" +" "+"Title:::" );
            receiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        }else*/


        Collections.addAll(trackList, tracks);
        Uri file = Uri.parse(tracks[this.currentTrack]);
        if (mPlayer == null ){
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(this, file);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(this);
                mPlayer.setOnCompletionListener(this);
                mPlayer.setOnErrorListener(this);
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Player failed", e);
            }
        }if(intent.hasExtra(UIFragment.DATA_REC)) {
            ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(UIFragment.DATA_REC);
            Bundle result = new Bundle();
            result.putString(UIFragment.DATA_RETURNED, "Artist" + " " + "Title");
            receiver.send(UIFragment.RESULT_DATA_RETURNED, result);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setAction(Intent.ACTION_MAIN);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.drawable.heavens_small);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.heavens));
            builder.setContentTitle("Artist");
            builder.setContentText("Title");
            builder.setNumber(currentTrack);
            NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle();
            bigStyle.setSummaryText("This expanded notification is brought to you by StacksMobile");
            bigStyle.setBigContentTitle("Artist");
            bigStyle.setSummaryText("Title");
            Bitmap bigPic = BitmapFactory.decodeResource(getResources(), R.drawable.bs);
            bigStyle.bigPicture(bigPic);
            builder.setStyle(bigStyle);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            startForeground(NOTIFICATION_ID, builder.build());
            mManager.notify(NOTIFICATION_ID, builder.build());
            builder.setContentTitle("Artist");
            mManager.notify(NOTIFICATION_ID, builder.build());
        }

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        currentTrack = (currentTrack + 1) % tracks.length;
        Uri nextTrack = Uri.parse(tracks[currentTrack]);
        Log.i("Completion Listener","Song Complete");
        mPlayer.reset();
        try {
            mPlayer.setDataSource(this,nextTrack);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.prepareAsync();

        }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mPlayer=mp;
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.start();
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
        Toast.makeText(getApplicationContext(),"In Skip back",Toast.LENGTH_SHORT).show();

        currentTrack = (currentTrack - 1) % tracks.length;

        if (currentTrack > 0) {
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
    public void showToast() {

        Toast.makeText(this, "We in here!!", Toast.LENGTH_SHORT).show();
    }

    public void showTrackInfo() {
        //TextView trackText = (TextView)

    }


}