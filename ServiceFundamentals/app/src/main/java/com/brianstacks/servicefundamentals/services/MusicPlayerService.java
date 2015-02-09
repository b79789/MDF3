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


public class MusicPlayerService extends Service implements MediaPlayer.OnErrorListener{

    public static final int NOTIFICATION_ID = 0x01001;
    private static final String DEBUG_TAG = "MusicPlayerService";
    final int Player_Idle = 0;
    final int Player_Initialized = 1;
    final int Player_Prepairing = 2;
    final int Player_Prepared = 3;
    final int Player_Completed = 4;
    final String lineSep = System.getProperty("line.separator");
    final String uri1 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.darkhorse;
    final String uri2 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.turndatide;
    final String uri3 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.trophies;
    final String uri4 = "android.resource://com.brianstacks.servicefundamentals/" + R.raw.onemorenight;
    final String[] tracks = {uri1, uri2, uri3, uri4};
    final String[] artist = {"Katy Perry","Arab Muzic","Drake","Adam Levine"};
    final String[] title = {"Dark Horse","Instrumental","Trophies","One More Night"};
    final int[]pics ={R.drawable.katy,R.drawable.araab,R.drawable.trophie,R.drawable.maroon};
    MediaPlayer mPlayer;
    ArrayList<String> trackList=new ArrayList<>();
    NotificationManager mManager;
    ResultReceiver resultReceiver;
    Bundle result;
    private int currentTrack = 0;
    private int mCurrentState;

    @Override
    public IBinder onBind(Intent intent) {
        resultReceiver = intent.getParcelableExtra(UIFragment.RC_INTENT);
        result=new Bundle();
        result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
        resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
        return new BoundServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {


        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mPlayer !=null){
            mPlayer.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        result = new Bundle();
        resultReceiver= intent.getParcelableExtra(UIFragment.RC_INTENT);
        Collections.addAll(trackList, tracks);
        Uri file = Uri.parse(tracks[this.currentTrack]);

        if (mPlayer == null ){
            mPlayer = new MediaPlayer();
            mCurrentState=Player_Idle;
            try {
                mPlayer.setDataSource(this, file);
                mCurrentState=Player_Initialized;
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Player failed", e);
            }
            mPlayer.prepareAsync();
            mCurrentState=Player_Prepairing;
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mCurrentState=Player_Prepared;
                    mPlayer.start();
                    if (currentTrack >= 0 && currentTrack !=4) {
                        // put track details in the bundle
                        result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
                        // give the bundle to the results receiver
                        resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                        // create intent for the main activity set the action and cat.
                        Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                        getActivityIntent.setAction(Intent.ACTION_MAIN);
                        getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        // create pendingIntent for notification
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                        // create notification and give it's properties
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentIntent(pendingIntent);
                        builder.setSmallIcon(R.drawable.ic_stat_one);
                        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                        builder.setContentTitle(artist[currentTrack]);
                        builder.setContentText(title[currentTrack]);
                        builder.setAutoCancel(false);
                        builder.setOngoing(true);
                        startForeground(NOTIFICATION_ID, builder.build());
                    }
                    else {
                        Log.d("Error","track is wrong");
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopForeground(true);
                    mCurrentState=Player_Completed;
                    currentTrack = (currentTrack + 1) % tracks.length;
                    if (currentTrack >= 0 && currentTrack !=4) {
                        Uri nextTrack = Uri.parse(tracks[currentTrack]);
                        mPlayer.reset();
                        try {
                            mPlayer.setDataSource(getApplicationContext(), nextTrack);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.prepareAsync();
                        if (currentTrack >= 0) {
                            // put track details in the bundle
                            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
                            // give the bundle to the results receiver
                            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                            // create intent for the main activity set the action and cat.
                            Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                            getActivityIntent.setAction(Intent.ACTION_MAIN);
                            getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            // create pendingIntent for notification
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                            // create notification and give it's properties
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.ic_stat_one);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                            builder.setContentTitle(artist[currentTrack]);
                            builder.setContentText(title[currentTrack]);
                            builder.setAutoCancel(false);
                            builder.setOngoing(true);
                            startForeground(NOTIFICATION_ID, builder.build());

                        }
                    }else {
                        Log.d("Error onCompletion"," track size error");
                    }

                }
            });
            mPlayer.setOnErrorListener(this);
        }else{
            Log.d("Error ","Media player is not null but should be");
        }
        return START_STICKY;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    public void onPause() {
        if (mPlayer!=null) {
            mPlayer.pause();
        }else{
            Toast.makeText(this,"The media player isn't playing",Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlay() {

        if (mCurrentState==Player_Prepared){
            mPlayer.start();
        }else{
            Toast.makeText(this,"The media player isn't prepared",Toast.LENGTH_SHORT).show();
        }
    }

    public void onStop()  {
        if (mPlayer!=null){
            mPlayer.reset();
        }else{
            Toast.makeText(this,"The media player isn't playing",Toast.LENGTH_SHORT).show();
        }
    }

    public void onSkipForward() {
        currentTrack = (currentTrack + 1);
        if (currentTrack>=0 && currentTrack<=3){
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mCurrentState=Player_Prepared;
                    mPlayer.start();
                    if (currentTrack >= 0 && currentTrack !=4) {
                        // put track details in the bundle
                        result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
                        // give the bundle to the results receiver
                        resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                        // create intent for the main activity set the action and cat.
                        Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                        getActivityIntent.setAction(Intent.ACTION_MAIN);
                        getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        // create pendingIntent for notification
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                        // create notification and give it's properties
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentIntent(pendingIntent);
                        builder.setSmallIcon(R.drawable.ic_stat_one);
                        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                        builder.setContentTitle(artist[currentTrack]);
                        builder.setContentText(title[currentTrack]);
                        builder.setAutoCancel(false);
                        builder.setOngoing(true);
                        startForeground(NOTIFICATION_ID, builder.build());
                    }
                    else {
                        Log.d("Error","track is wrong");
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopForeground(true);
                    mCurrentState=Player_Completed;
                    currentTrack = (currentTrack + 1) % tracks.length;
                    if (currentTrack >= 0 && currentTrack !=4) {
                        Uri nextTrack = Uri.parse(tracks[currentTrack]);
                        mPlayer.reset();
                        try {
                            mPlayer.setDataSource(getApplicationContext(), nextTrack);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.prepareAsync();
                        if (currentTrack >= 0) {
                            // put track details in the bundle
                            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
                            // give the bundle to the results receiver
                            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                            // create intent for the main activity set the action and cat.
                            Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                            getActivityIntent.setAction(Intent.ACTION_MAIN);
                            getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            // create pendingIntent for notification
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                            // create notification and give it's properties
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.ic_stat_one);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                            builder.setContentTitle(artist[currentTrack]);
                            builder.setContentText(title[currentTrack]);
                            builder.setAutoCancel(false);
                            builder.setOngoing(true);
                            startForeground(NOTIFICATION_ID, builder.build());

                        }
                    }else {
                        Log.d("Error onCompletion"," track size error");
                    }

                }
            });
            mPlayer.setOnErrorListener(this);

        }else {
            Toast.makeText(this,"End of track list",Toast.LENGTH_SHORT).show();
        }
    }

    public void onSkipback() {

        currentTrack = (currentTrack - 1);
        if (currentTrack >= 0  && currentTrack<=3) {
            Uri nextTrack = Uri.parse(tracks[currentTrack]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mCurrentState=Player_Prepared;
                    mPlayer.start();
                    if (currentTrack >= 0 && currentTrack !=4) {
                        // put track details in the bundle
                        result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
                        // give the bundle to the results receiver
                        resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                        // create intent for the main activity set the action and cat.
                        Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                        getActivityIntent.setAction(Intent.ACTION_MAIN);
                        getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        // create pendingIntent for notification
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                        // create notification and give it's properties
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentIntent(pendingIntent);
                        builder.setSmallIcon(R.drawable.ic_stat_one);
                        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                        builder.setContentTitle(artist[currentTrack]);
                        builder.setContentText(title[currentTrack]);
                        builder.setAutoCancel(false);
                        builder.setOngoing(true);
                        startForeground(NOTIFICATION_ID, builder.build());
                    }
                    else {
                        Log.d("Error","track is wrong");
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopForeground(true);
                    mCurrentState=Player_Completed;
                    currentTrack = (currentTrack + 1) % tracks.length;
                    if (currentTrack >= 0 && currentTrack !=4) {
                        Uri nextTrack = Uri.parse(tracks[currentTrack]);
                        mPlayer.reset();
                        try {
                            mPlayer.setDataSource(getApplicationContext(), nextTrack);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.prepareAsync();
                        if (currentTrack >= 0) {
                            // put track details in the bundle
                            result.putString(UIFragment.DATA_RETURNED, artist[currentTrack] + lineSep + title[currentTrack]);
                            // give the bundle to the results receiver
                            resultReceiver.send(UIFragment.RESULT_DATA_RETURNED, result);
                            // create intent for the main activity set the action and cat.
                            Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                            getActivityIntent.setAction(Intent.ACTION_MAIN);
                            getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            // create pendingIntent for notification
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                            // create notification and give it's properties
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.ic_stat_one);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                            builder.setContentTitle(artist[currentTrack]);
                            builder.setContentText(title[currentTrack]);
                            builder.setAutoCancel(false);
                            builder.setOngoing(true);
                            startForeground(NOTIFICATION_ID, builder.build());

                        }
                    }else {
                        Log.d("Error onCompletion"," track size error");
                    }

                }
            });
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
        if (currentTrack >= 0 && currentTrack <= 3) {
            Uri nextTrack = Uri.parse(tracks[getRandomNumber(tracks.length)]);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(getApplicationContext(), nextTrack);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopForeground(true);
                    mCurrentState=Player_Completed;
                    if (currentTrack >= 0 && currentTrack !=4) {
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
                            // create intent for the main activity set the action and cat.
                            Intent getActivityIntent = new Intent(getApplication(), MainActivity.class);
                            getActivityIntent.setAction(Intent.ACTION_MAIN);
                            getActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            // create pendingIntent for notification
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), NOTIFICATION_ID, getActivityIntent, 0);
                            // create notification and give it's properties
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.ic_stat_one);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_one));
                            builder.setContentTitle(artist[currentTrack]);
                            builder.setContentText(title[currentTrack]);
                            builder.setAutoCancel(false);
                            builder.setOngoing(true);
                            startForeground(NOTIFICATION_ID, builder.build());
                    }else {
                        Log.d("Error onCompletion"," track size error");
                    }

                }
            });

        }
    }

    public class BoundServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

}