package com.brianstacks.widgetapptest.CollectionWidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Brian Stacks
 * on 1/19/15
 * for FullSail.edu.
 */
public class CollectionWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CollectionWidgetFactory(getApplicationContext());
    }
}