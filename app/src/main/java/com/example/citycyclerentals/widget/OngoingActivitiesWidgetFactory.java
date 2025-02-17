package com.example.citycyclerentals.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Reservation;
import com.example.citycyclerentals.network.ApiClient;
import com.example.citycyclerentals.network.ApiService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OngoingActivitiesWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "OngoingActivitiesWidgetFactory";
    private final Context context;
    private List<Reservation> reservations = new ArrayList<>();
    private int userId;
    private boolean isFetchingData = false;

    public OngoingActivitiesWidgetFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("id", "1"); // Default to "1" if not found
        userId = Integer.parseInt(userIdStr);
        Log.d(TAG, "onCreate called with user ID: " + userId);
    }

    @Override
    public void onDataSetChanged() {
        // Fetch data when necessary and not in a loop
        if (!isFetchingData) {
            fetchOngoingActivities();
        }
    }

    @Override
    public void onDestroy() {
        reservations.clear();
        Log.d(TAG, "onDestroy called");
    }

    @Override
    public int getCount() {
        return reservations.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= reservations.size()) {
            Log.d(TAG, "Invalid position: " + position);
            return null;
        }

        Reservation reservation = reservations.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_reservation_item);
        views.setTextViewText(R.id.bike_name, reservation.getBikeName());

        // Calculate countdown
        String countdown = getCountdown(reservation.getEndDate());
        views.setTextViewText(R.id.countdown, countdown);

        // Set status
        views.setTextViewText(R.id.status, reservation.getStatus());

        // Load bike image using Glide
        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.bike_image, views, new int[]{R.id.widget_list_view});
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(reservation.getBikeImageUrl())
                .into(appWidgetTarget);

        Log.d(TAG, "Displaying reservation: " + reservation.getBikeName() + " - " + reservation.getEndDate());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static final long UPDATE_INTERVAL = 10 * 60 * 1000; // 10 minutes
    private long lastUpdateTime = 0;

    private void fetchOngoingActivities() {
        long currentTime = System.currentTimeMillis();

        // Prevent fetching if the last update was less than 10 minutes ago
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL && !isFetchingData) {
            Log.d(TAG, "Skipping fetch, last update was recently done.");
            return;
        }

        isFetchingData = true;
        Log.d(TAG, "Fetching ongoing activities for user ID: " + userId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Reservation>> call = apiService.getOngoingActivities(userId);

        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                isFetchingData = false;
                if (response.isSuccessful() && response.body() != null) {
                    reservations = response.body();
                    lastUpdateTime = System.currentTimeMillis(); // Update the last fetch time

                    Log.d(TAG, "Fetched " + reservations.size() + " reservations");

                    // Notify widget only if data has changed
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    ComponentName thisWidget = new ComponentName(context, OngoingActivitiesWidgetProvider.class);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                } else {
                    reservations = new ArrayList<>();
                    Log.e(TAG, "Failed to fetch reservations: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                isFetchingData = false;
                reservations = new ArrayList<>();
                Log.e(TAG, "Error fetching reservations: " + t.getMessage());
            }
        });
    }

    private String getCountdown(String endDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            long endDate = sdf.parse(endDateStr).getTime();
            long currentTime = System.currentTimeMillis();
            long diff = endDate - currentTime;

            if (diff > 0) {
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;

                return String.format(Locale.getDefault(), "%d days %02d:%02d:%02d Remaining", days, hours, minutes, seconds);
            } else {
                return "Time's up!";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing endDate", e);
            return "Invalid date";
        }
    }
}