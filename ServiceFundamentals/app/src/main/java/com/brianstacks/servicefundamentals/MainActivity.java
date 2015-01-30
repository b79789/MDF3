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
import android.view.Menu;
import android.view.MenuItem;

import com.brianstacks.servicefundamentals.fragments.UIFragment;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager mgr = getFragmentManager();
        UIFragment uiFragment = (UIFragment) mgr.findFragmentByTag(UIFragment.TAG);
        FragmentTransaction trans = mgr.beginTransaction();
        if (uiFragment == null){
            UIFragment fragment = UIFragment.newInstance();
            trans.add(R.id.fragment_container, fragment);
            trans.commit();
        }
    }


}
