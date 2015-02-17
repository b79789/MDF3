package com.brianstacks.widgetapptest;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.brianstacks.widgetapptest.CollectionWidget.CollectionWidgetProvider;
import com.brianstacks.widgetapptest.Fragments.EnterDataFragment;
import com.brianstacks.widgetapptest.Fragments.MyListFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity implements EnterDataFragment.OnFragmentInteractionListener{

    ArrayList<EnteredData> enteredDataArrayList;
    public static final String fileName = "enteredData";
    CustomReceiver mReceiver;
    public static final String ACTION_CUSTOM = "com.brianstacks.android.ACTION_CUSTOM";
    public static final String UPDATE_LIST ="com.brianstacks.android.UPDATE_LIST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (fileExists(this, fileName)){
            FileInputStream fis = null;
            try {
                fis = this.openFileInput(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<EnteredData> simpleClass = null;
            try {
                simpleClass = (ArrayList<EnteredData>) is.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            enteredDataArrayList = simpleClass;
            if (simpleClass != null) {
                Log.v("arraySize", String.valueOf(simpleClass.size()));
            }
            getIntent().putExtra("enteredDataArrayList", enteredDataArrayList);
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            MyListFragment listFrag =  MyListFragment.newInstance(enteredDataArrayList);
            trans.replace(R.id.fragment_container, listFrag, MyListFragment.TAG);
            trans.commit();
            Intent broadcast = new Intent(ACTION_CUSTOM);
            sendBroadcast(broadcast);
        }else {
            MyListFragment myListFragment = new MyListFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, myListFragment, MyListFragment.TAG)
                    .commit();
            enteredDataArrayList = new ArrayList<>();
            getIntent().putExtra("enteredDataArrayList", enteredDataArrayList);
            Intent broadcast = new Intent(ACTION_CUSTOM);
            sendBroadcast(broadcast);
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public  void addDetailsClick(View v) {

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        EnterDataFragment enterDataFragment = EnterDataFragment.newInstance();
        trans.replace(R.id.fragment_container, enterDataFragment, EnterDataFragment.TAG);
        trans.commit();
    }

    @Override
    public void onFragmentInteraction2(EnteredData enteredData) {
        Log.d("Name", enteredData.getName());
        enteredDataArrayList.add(enteredData);
        FileOutputStream fos = null;
        try {
            fos = this.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (os != null) {
                os.writeObject(enteredDataArrayList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //update widget
        Intent broadcast = new Intent(ACTION_CUSTOM);
        sendBroadcast(broadcast);

        MyListFragment listFrag = (MyListFragment) getFragmentManager().findFragmentByTag(MyListFragment.TAG);
        if (listFrag == null) {

            listFrag = MyListFragment.newInstance(enteredDataArrayList);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, listFrag, MyListFragment.TAG)
                    .commit();


        } else {
            Toast.makeText(this, "Listfrag is not null", Toast.LENGTH_SHORT).show();
            DataAdapter dataAdapter = new DataAdapter(this, enteredDataArrayList);
            ListView myList = (ListView) findViewById(R.id.myList);
            myList.setAdapter(dataAdapter);
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return !(file == null || !file.exists());
    }

    public class CustomReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Intent handled here.
            Log.i("TAG", "Intent recieved: " + intent.getAction());

            if (intent.getAction() == ACTION_CUSTOM) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Toast.makeText(getApplicationContext(),"Worked from activity",Toast.LENGTH_SHORT).show();

                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mReceiver = new CustomReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CUSTOM);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }


}
