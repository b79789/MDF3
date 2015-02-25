/**
 * Created by Brian Stacks
 * on 2/23/15
 * for FullSail.edu.
 */
package com.brianstacks.mappingphotos;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class MainActivity extends Activity implements EnterDataFragment.OnFragmentInteractionListener{
    ArrayList<EnteredData> myArrayList;
    public static final String fileName = "mapentereddata";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (fileExists(this, fileName)){
            readFile();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/"+fileName;
            Log.d("Filename",path);
            getIntent().putExtra("arrayList", myArrayList);
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            MapFragment mapFragment = MapFragment.newInstance(myArrayList);
            trans.replace(R.id.layout_container, mapFragment, MapFragment.TAG);
            trans.commit();
        }else {
            myArrayList=new ArrayList<>();
            MapFragment frag = new MapFragment();
            getFragmentManager().beginTransaction().replace(R.id.layout_container, frag).commit();

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

        if (id == R.id.addButton){
            EnterDataFragment enterDataFragment = new EnterDataFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.layout_container, enterDataFragment, EnterDataFragment.TAG)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(EnteredData enteredData) {
        getIntent().putExtra("enteredData", enteredData);
        myArrayList.add(enteredData);
        writeFile();


        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentByTag(MapFragment.TAG);
        if (mapFragment == null){
            Log.d("Brian Stacks","mapFragment == null");

            mapFragment = MapFragment.newInstance(myArrayList);
            getFragmentManager().beginTransaction()
                    .replace(R.id.layout_container, mapFragment,MapFragment.TAG)
                    .commit();
        }else {
            Log.d("Brian Stacks","mapFragment does not == null");

            mapFragment.getFragmentManager().beginTransaction().commit();
        }

    }



    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return !(file == null || !file.exists());
    }

    public void writeFile(){
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
                os.writeObject(myArrayList);
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


    }
    public void readFile(){

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
        if (simpleClass != null) {
            myArrayList = simpleClass;
        }
    }


}
