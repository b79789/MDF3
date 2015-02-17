/**
 *Created by Brian Stacks
 on 2/9/15
 for FullSail.edu.
 */
package com.brianstacks.widgetapptest.Fragments;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.brianstacks.widgetapptest.CollectionWidget.CollectionWidgetProvider;
import com.brianstacks.widgetapptest.DataAdapter;
import com.brianstacks.widgetapptest.DetailActivity;
import com.brianstacks.widgetapptest.EnteredData;
import com.brianstacks.widgetapptest.MainActivity;
import com.brianstacks.widgetapptest.R;

import java.util.ArrayList;
import java.util.Date;

public class MyListFragment extends Fragment {
    public static final String TAG = "MyListFragment.TAG";
    private static final String ARG_LIST = "LIST";

    ArrayList<EnteredData> enteredDataArrayList;
    public static int deletePos;

    public static MyListFragment newInstance(ArrayList<EnteredData> list) {
        MyListFragment fragment = new MyListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST,list);
        fragment.setArguments(args);
        return fragment;
    }

    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_list, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle _savedInstanceState) {
        super.onActivityCreated(_savedInstanceState);
        Bundle args = getArguments();
        Intent i = getActivity().getIntent();
        enteredDataArrayList = (ArrayList<EnteredData>) i.getSerializableExtra("enteredDataArrayList");
        if (args != null && args.containsKey(ARG_LIST)) {
            enteredDataArrayList = (ArrayList<EnteredData>)args.getSerializable(ARG_LIST);
            final DataAdapter dataAdapter = new DataAdapter(getActivity(), enteredDataArrayList);
            final ListView listView = (ListView) getActivity().findViewById(R.id.myList);
            listView.setAdapter(dataAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EnteredData enteredData1 = (EnteredData) parent.getItemAtPosition(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("name", enteredData1);
                    startActivity(intent);
                    deletePos = position;
                    parent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                            //Get your item here with the position
                            enteredDataArrayList.remove(position);

                            DataAdapter arrayAdapter = (DataAdapter) listView.getAdapter();
                            arrayAdapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                }
            });
            Button resetButton = (Button) getActivity().findViewById(R.id.resetList);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enteredDataArrayList.clear();
                    dataAdapter.notifyDataSetChanged();
                }
            });
        }


    }


}
