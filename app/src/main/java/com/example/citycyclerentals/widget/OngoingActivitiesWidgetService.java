package com.example.citycyclerentals.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class OngoingActivitiesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new OngoingActivitiesWidgetFactory(this.getApplicationContext(), intent);
    }
}