/**
 * Created by Brian Stacks
 on 1-27-2015
 for FullSail.edu.
 */
package com.brianstacks.servicefundamentals;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.brianstacks.servicefundamentals.fragments.UIFragment;


public class MainActivity extends ActionBarActivity{

    public UIFragment fragmentSimple;
    public final String SIMPLE_FRAGMENT_TAG = UIFragment.TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            UIFragment uiFragment = new UIFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment_container,uiFragment).commit();
        }
       /* if (savedInstanceState == null) { // saved instance state, fragment may exist
            // look up the instance that already exists by tag

             fragmentSimple = (UIFragment)getFragmentManager().findFragmentByTag(SIMPLE_FRAGMENT_TAG);
        } else if (fragmentSimple == null) {
            // only create fragment if they haven't been instantiated already
            fragmentSimple = new UIFragment();
        }if (!fragmentSimple.isInLayout()) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentSimple, SIMPLE_FRAGMENT_TAG)
                    .commit();
        }*/
       /* FragmentManager mgr = getFragmentManager();
        UIFragment uiFragment = (UIFragment) mgr.findFragmentByTag(UIFragment.TAG);
        FragmentTransaction trans = mgr.beginTransaction();
        if (uiFragment == null){
            Log.d("Fragment went ","null");
            UIFragment fragment = new UIFragment();
            trans.add(R.id.fragment_container, fragment);
            trans.commit();
        }*/
    }

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



}
