/**
 *Created by Brian Stacks
 on 2/9/15
 for FullSail.edu.
 */
package com.brianstacks.widgetapptest.CollectionWidget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.brianstacks.widgetapptest.EnteredData;
import com.brianstacks.widgetapptest.MainActivity;
import com.brianstacks.widgetapptest.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class CollectionWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final int ID_CONSTANT = 0x0101010;

    private ArrayList<EnteredData> enteredDataArray;
    private Context mContext;

    public CollectionWidgetFactory(Context context) {
        mContext = context;
        enteredDataArray = new ArrayList<EnteredData>();
    }

    @Override
    public void onCreate() {

        FileInputStream fis = null;
        try {
            fis = mContext.openFileInput(MainActivity.fileName);
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
        enteredDataArray = simpleClass;
    }

    @Override
    public int getCount() {
        return enteredDataArray.size();
    }

    @Override
    public long getItemId(int position) {
        return ID_CONSTANT + position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        EnteredData article = enteredDataArray.get(position);

        RemoteViews itemView = new RemoteViews(mContext.getPackageName(), R.layout.article_item);

        itemView.setTextViewText(R.id.title, article.getName());


        Intent intent = new Intent();
        intent.putExtra(CollectionWidgetProvider.EXTRA_ITEM, article);
        itemView.setOnClickFillInIntent(R.id.article_item, intent);

        return itemView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        // Heavy lifting code can go here without blocking the UI.
        // You would update the data in your collection here as well.
        FileInputStream fis = null;
        try {
            fis = mContext.openFileInput(MainActivity.fileName);
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
        enteredDataArray = simpleClass;

    }

    @Override
    public void onDestroy() {
        enteredDataArray.clear();
    }

}