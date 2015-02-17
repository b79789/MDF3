/**
 *Created by Brian Stacks
 on 2/9/15
 for FullSail.edu.
 */
package com.brianstacks.widgetapptest.CollectionWidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CollectionWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CollectionWidgetFactory(getApplicationContext());
    }
}