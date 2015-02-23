/**
 * Created by Brian Stacks
 * on 2/23/15
 * for FullSail.edu.
 */
package com.brianstacks.mappingphotos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapFragment extends com.google.android.gms.maps.MapFragment implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, LocationListener {

    public static final String TAG = "MapFragment.TAG";
    GoogleMap mMap;
    EnteredData enteredData;
    private static final int REQUEST_ENABLE_GPS = 0x02001;
    Button mapButt;
    LocationManager mManager;
    Button addButton;
    Button viewButton;
    Button mapButton;
    ArrayList<EnteredData> myArrayList;





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
        if (getArguments() != null) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = (LocationManager)getActivity().getSystemService(MainActivity.LOCATION_SERVICE);
        //myArrayList = (ArrayList<EnteredData>) args.getSerializable("myArrayList");
        enteredData = (EnteredData)getActivity().getIntent().getSerializableExtra("enteredData");

        if (null == enteredData){

            enteredData = new EnteredData();
            enableGps();
            enteredData.setName("No Info Entered");
        }

        mMap = getMap();
        mMap.addMarker(new MarkerOptions().position(new LatLng(enteredData.getLat(),enteredData.getLon())).title(enteredData.getName()));
        mMap.setInfoWindowAdapter(new MarkerAdapter());
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(enteredData.getLat(), enteredData.getLon()), 12));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.v("onMapLongClick LatLong", String.valueOf(latLng));
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                EnterDataFragment enterDataFragment = EnterDataFragment.newInstance();
                trans.replace(R.id.layout_container, enterDataFragment, EnterDataFragment.TAG);
                trans.commit();
            }
        });
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

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.v("onMapClick LatLong", String.valueOf(latLng));

    }

    private class MarkerAdapter implements GoogleMap.InfoWindowAdapter {

        TextView mText;

        public MarkerAdapter() {
            mText = new TextView(getActivity());
        }

        @Override
        public View getInfoContents(Marker marker) {
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

}
