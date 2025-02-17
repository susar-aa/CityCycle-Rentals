package com.example.citycyclerentals.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.SplashActivity;

public class OngoingActivitiesWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "OngoingActivitiesWidgetProvider";
    private static final String ACTION_RELOAD = "com.example.citycyclerentals.ACTION_RELOAD";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate called");

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ongoing_activities);

            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // Manual reload button
            Intent reloadIntent = new Intent(context, OngoingActivitiesWidgetProvider.class);
            reloadIntent.setAction(ACTION_RELOAD);
            PendingIntent reloadPendingIntent = PendingIntent.getBroadcast(context, 0, reloadIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.reload_button, reloadPendingIntent);

            Intent serviceIntent = new Intent(context, OngoingActivitiesWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_RELOAD.equals(intent.getAction())) {
            Log.d(TAG, "Reloading widget");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, OngoingActivitiesWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

            // Trigger data reload
            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ongoing_activities);
                Intent serviceIntent = new Intent(context, OngoingActivitiesWidgetService.class);
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    public static void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, OngoingActivitiesWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ongoing_activities);

            Intent serviceIntent = new Intent(context, OngoingActivitiesWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}