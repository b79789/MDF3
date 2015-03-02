package com.technotalkative.daydreamexample;

import android.graphics.Color;
import android.service.dreams.DreamService;
import android.widget.TextView;

public class MyDreamService extends DreamService {
    @Override
   public void onAttachedToWindow() {
       super.onAttachedToWindow();
       // Allow user touch
       setInteractive(true);
       // Hide system UI
       setFullscreen(true);
       // Set the dream layout
       
       TextView txtView = new TextView(this);
       setContentView(txtView);
       txtView.setText("Hello DayDream world from TechnoTalkative.com !!");
       txtView.setTextColor(Color.rgb(184, 245, 0));
       txtView.setTextSize(30);
      
   }
}
