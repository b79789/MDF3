/**
 * Created by Brian Stacks
 * on 2/23/15
 * for FullSail.edu.
 */
package com.brianstacks.mappingphotos;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class MapFragment extends com.google.android.gms.maps.MapFragment implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, LocationListener {

    public static final String TAG = "MapFragment.TAG";
    GoogleMap mMap;
    EnteredData enteredData;
    private static final int REQUEST_ENABLE_GPS = 0x02001;
    LocationManager mManager;
    ArrayList<EnteredData> myArrayList;
    HashMap <String, EnteredData> mMarkers = new HashMap<>();

    public static MapFragment newInstance(ArrayList<EnteredData> arrayList) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable("myArrayList",arrayList);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        mManager = (LocationManager)getActivity().getSystemService(MainActivity.LOCATION_SERVICE);
        if(mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            Location loc = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double lati = loc.getLatitude();
            double longi = loc.getLongitude();
            mMap=getMap();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = (LocationManager)getActivity().getSystemService(MainActivity.LOCATION_SERVICE);
        if (fileExists(getActivity(), MainActivity.fileName)){
            readFile();
            if (getArguments()!=null){
                myArrayList= (ArrayList<EnteredData>) getArguments().getSerializable("myArrayList");
            }
            for (int i = 0;i<myArrayList.size();i++){
                getActivity().getIntent().getExtras().putSerializable("myArrayListObject", myArrayList.get(i));
                mMap = getMap();

                MarkerOptions mo = new MarkerOptions()
                        .position(new LatLng(myArrayList.get(i).getLat(), myArrayList.get(i).getLon())).title(myArrayList.get(i).getName());
                Marker marker = mMap.addMarker(mo);
                mMap.addMarker(mo);
                mMarkers.put(marker.getId(),myArrayList.get(i));
                mMap.setInfoWindowAdapter(new MarkerAdapter());
                mMap.setOnInfoWindowClickListener(this);
                mMap.setOnMapClickListener(this);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myArrayList.get(i).getLat(),myArrayList.get(i).getLon()), 12));
                final int finalI = i;
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        Double lat = latLng.latitude;
                        Double lon = latLng.longitude;
                        mMap.addMarker(new MarkerOptions().position(latLng).title(myArrayList.get(finalI).getName()));
                        FragmentTransaction trans = getFragmentManager().beginTransaction();
                        EnterDataFragment enterDataFragment = EnterDataFragment.newInstance(lat,lon);
                        trans.replace(R.id.layout_container, enterDataFragment, EnterDataFragment.TAG);
                        trans.commit();
                    }
                });
            }
        }else {
            myArrayList=new ArrayList<>();
            if (enteredData == null){
                enteredData = new EnteredData();
                enableGps();
                enteredData.setName("No Info Entered");
                enteredData.setLat(-87.5);
                enteredData.setLon(35.4);
            }
            myArrayList.add(enteredData);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Get extra data with marker ID
        EnteredData marker_data = mMarkers.get(marker.getId());
        getActivity().getIntent().putExtra("marker_data",marker_data);
        InfoViewFragment infoViewFragment = InfoViewFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.layout_container, infoViewFragment, InfoViewFragment.TAG)
                .addToBackStack(MapFragment.TAG)
                .commit();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private class MarkerAdapter implements GoogleMap.InfoWindowAdapter {

        TextView mText;
        public MarkerAdapter() {
            mText = new TextView(getActivity());
        }

        @Override
        public View getInfoContents(Marker marker) {
            mText.setBackgroundColor(Color.BLACK);
            mText.setText(marker.getTitle());
            return mText;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            return null;
        }
    }

    private void enableGps() {
        if(mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            Location loc = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null) {
                enteredData.setLat(loc.getLatitude());
                enteredData.setLon(loc.getLongitude());
            }

        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("GPS Unavailable")
                    .setMessage("Please enable GPS in the system settings.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(settingsIntent, REQUEST_ENABLE_GPS);
                        }

                    })
                    .show();
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return !(file == null || !file.exists());
    }

    public void readFile(){

        FileInputStream fis = null;
        try {
            fis = getActivity().openFileInput(MainActivity.fileName);
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
