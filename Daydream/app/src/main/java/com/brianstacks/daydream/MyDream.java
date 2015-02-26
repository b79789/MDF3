package com.brianstacks.daydream;

import android.content.SharedPreferences;
import android.service.dreams.DreamService;
import android.widget.TextView;


/**
 * Created by Brian Stacks
 * on 2/25/15
 * for FullSail.edu.
 */
public class MyDream extends DreamService {

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.dreamout);
        TextView dreamtext =(TextView)findViewById(R.id.myText);
        final SharedPreferences settings = getSharedPreferences(
                SettingsActivity.PREFS_KEY, 0);
        boolean check = settings.getBoolean("changedText", true);
        if (check){
            dreamtext.setText("Hello World it is Brian Stacks");

        }else {
            dreamtext.setText("Hey, you took my World = [");
        }
    }

    @Override
    public void onDreamingStarted (){

    }


}