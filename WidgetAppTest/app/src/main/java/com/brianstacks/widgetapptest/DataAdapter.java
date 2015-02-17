/**
 *Created by Brian Stacks
 on 2/9/15
 for FullSail.edu.
 */
package com.brianstacks.widgetapptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class DataAdapter extends BaseAdapter {
    private static final long ID_CONSTANT = 0x010101010L;
    private Context context;
    private ArrayList<EnteredData> dataList;

    public DataAdapter(Context context, ArrayList<EnteredData> objects){
        this.context = context;
        this.dataList =objects;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ID_CONSTANT + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflator = (LayoutInflater)context.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view;
        view = inflator.inflate(R.layout.item_place,null);
        EnteredData enteredData = dataList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(enteredData.getName());

        TextView tv2 = (TextView) view.findViewById(R.id.textView2);
        tv2.setText(enteredData.getAge());
        return  view;
    }
}
