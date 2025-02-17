package com.example.citycyclerentals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.citycyclerentals.adapters.FeedbackAdapter;
import com.example.citycyclerentals.adapters.ReservedTimesAdapter;
import com.example.citycyclerentals.models.Feedback;
import com.example.citycyclerentals.models.ReservedTime;
import com.example.citycyclerentals.admin.ViewLocationActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BikeDetailActivity extends AppCompatActivity {

    private ImageView bikeImage;
    private TextView bikeName, bikeType, stationName, priceHourly, priceDaily, priceMonthly, statusText, priceHourlyAmount, priceDailyAmount, priceMonthlyAmount;
    private Button rentButton, viewLocationButton, addFeedbackButton;
    private EditText feedbackInput;
    private static final String TAG = "BikeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_detail);

        // Initialize UI components
        bikeImage = findViewById(R.id.bikeImage);
        bikeName = findViewById(R.id.bikeName);
        bikeType = findViewById(R.id.bikeType);
        stationName = findViewById(R.id.stationName);
        priceHourly = findViewById(R.id.priceHourly);
        priceDaily = findViewById(R.id.priceDaily);
        priceMonthly = findViewById(R.id.priceMonthly);
        statusText = findViewById(R.id.statusText);
        rentButton = findViewById(R.id.rentButton);
        viewLocationButton = findViewById(R.id.locationButton);
        priceHourlyAmount = findViewById(R.id.priceHourlyAmount);
        priceDailyAmount = findViewById(R.id.priceDailyAmount);
        priceMonthlyAmount = findViewById(R.id.priceMonthlyAmount);
        feedbackInput = findViewById(R.id.feedbackInput);
        addFeedbackButton = findViewById(R.id.addFeedbackButton);

        // Retrieve the bike ID from the Intent
        Intent intent = getIntent();
        final int bikeId = intent.getIntExtra("bike_id", -1);

        // Fetch bike details from the server
        fetchBikeDetails(bikeId);

        // Rent button click handler
        rentButton.setOnClickListener(v -> {
            // Handle Rent button click, navigate to the rental process
            Intent rentIntent = new Intent(BikeDetailActivity.this, ReservationActivity.class);
            rentIntent.putExtra("bike_id", bikeId);
            startActivity(rentIntent);
        });

        // Add Feedback button click handler
        addFeedbackButton.setOnClickListener(v -> {
            String feedbackText = feedbackInput.getText().toString();
            if (!feedbackText.isEmpty()) {
                addFeedback(bikeId, feedbackText);
            }
        });
    }

    private void fetchBikeDetails(final int bikeId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://192.168.1.2/CityCycle%20Rentals/fetch_bike_details.php?bike_id=" + bikeId,
                response -> {
                    try {
                        JSONObject bike = new JSONObject(response);
                        String name = bike.optString("name", "N/A");
                        String type = bike.optString("type", "N/A");
                        String station = bike.optString("station_name", "N/A");
                        String imageUrl = bike.optString("image_url", "");
                        double hourly = bike.optDouble("price_hourly", 0);
                        double daily = bike.optDouble("price_daily", 0);
                        double monthly = bike.optDouble("price_monthly", 0);
                        String status = bike.optString("status", "Unavailable");
                        final double latitude = bike.optDouble("latitude", 0);
                        final double longitude = bike.optDouble("longitude", 0);

                        // Set data to UI components
                        bikeName.setText(name);
                        bikeType.setText("Type: " + type);
                        stationName.setText("Location: " + station);
                        priceHourly.setText("Hourly");
                        priceDaily.setText("Daily");
                        priceMonthly.setText("Monthly");
                        statusText.setText("Status: " + status);
                        priceHourlyAmount.setText("Rs: " + hourly);
                        priceDailyAmount.setText("Rs: " + daily);
                        priceMonthlyAmount.setText("Rs: " + monthly);

                        // Load image using Glide
                        Glide.with(BikeDetailActivity.this).load(imageUrl).into(bikeImage);

                        // View Location button click handler
                        viewLocationButton.setOnClickListener(v -> {
                            Log.d(TAG, "View Location button clicked");
                            // Handle View Location button click, navigate to the map view
                            Intent mapIntent = new Intent(BikeDetailActivity.this, ViewLocationActivity.class);
                            mapIntent.putExtra("latitude", latitude);
                            mapIntent.putExtra("longitude", longitude);
                            startActivity(mapIntent);
                            Log.d(TAG, "startActivity called for ViewLocationActivity");
                        });

                        // Load reserved times for the bike
                        loadReservedTimes(bikeId);

                        // Load feedbacks for the bike
                        loadFeedbacks(bikeId);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e(TAG, "Volley error: " + error.getMessage());
                });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void loadReservedTimes(final int bikeId) {
        final List<ReservedTime> reservedTimes = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://192.168.1.2/CityCycle%20Rentals/fetch_reserved_times.php?bike_id=" + bikeId,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject reservation = jsonArray.getJSONObject(i);
                            String startDate = reservation.getString("start_date");
                            String endDate = reservation.getString("end_date");

                            reservedTimes.add(new ReservedTime(startDate, endDate));
                        }

                        ReservedTimesAdapter adapter = new ReservedTimesAdapter(reservedTimes);
                        RecyclerView recyclerView = findViewById(R.id.reservedTimesRecyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(BikeDetailActivity.this));
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e(TAG, "Volley error: " + error.getMessage());
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void loadFeedbacks(final int bikeId) {
        final List<Feedback> feedbacks = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://192.168.1.2/CityCycle%20Rentals/fetch_feedbacks.php?bike_id=" + bikeId,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject feedbackObject = jsonArray.getJSONObject(i);
                            String user = feedbackObject.getString("user");
                            String feedbackText = feedbackObject.getString("feedback");

                            feedbacks.add(new Feedback(user, feedbackText));
                        }

                        FeedbackAdapter adapter = new FeedbackAdapter(feedbacks);
                        RecyclerView recyclerView = findViewById(R.id.feedbackRecyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(BikeDetailActivity.this));
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e(TAG, "Volley error: " + error.getMessage());
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void addFeedback(final int bikeId, final String feedbackText) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://192.168.1.2/CityCycle%20Rentals/add_feedback.php",
                response -> {
                    Log.d(TAG, "Feedback added: " + response);
                    feedbackInput.setText("");
                    loadFeedbacks(bikeId);
                },
                error -> {
                    error.printStackTrace();
                    Log.e(TAG, "Volley error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("bike_id", String.valueOf(bikeId));
                params.put("feedback", feedbackText);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}