package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.citycyclerentals.adapters.ReservedTimesAdapter;
import com.example.citycyclerentals.models.ReservedTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BikeDetailActivity extends AppCompatActivity {

    private ImageView bikeImage;
    private TextView bikeName, bikeType, stationName, priceHourly, priceDaily, priceMonthly, statusText;
    private Button rentButton;  // Declare Rent button

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
        rentButton = findViewById(R.id.rentButton);  // Initialize Rent button

        // Retrieve the data from the Intent
        Intent intent = getIntent();
        final int bikeId = intent.getIntExtra("bike_id", -1);  // Retrieve bike ID
        String name = intent.getStringExtra("bike_name");
        String type = intent.getStringExtra("bike_type");
        String station = intent.getStringExtra("station_name");
        String imageUrl = intent.getStringExtra("image_url");
        double hourly = intent.getDoubleExtra("price_hourly", 0);
        double daily = intent.getDoubleExtra("price_daily", 0);
        double monthly = intent.getDoubleExtra("price_monthly", 0);
        String status = intent.getStringExtra("status");

        // Set data to UI components
        bikeName.setText(name);
        bikeType.setText("Type: " + type);
        stationName.setText("Station: " + station);
        priceHourly.setText("Hourly: Rs: " + hourly);
        priceDaily.setText("Daily: Rs: " + daily);
        priceMonthly.setText("Monthly: Rs: " + monthly);
        statusText.setText("Status: " + status);

        // Load image using Glide
        Glide.with(this).load(imageUrl).into(bikeImage);

        // Load reserved times for the bike
        loadReservedTimes(bikeId);

        // Save the bike_id to SharedPreferences
        SharedPreferences bikePreferences = getSharedPreferences("BikeDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = bikePreferences.edit();
        editor.putInt("bike_id", bikeId);  // Save bike_id
        editor.apply();

        // Rent button click handler
        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Rent button click, navigate to the rental process
                Intent rentIntent = new Intent(BikeDetailActivity.this, ReservationActivity.class);

                // Pass necessary data to ReservationActivity
                rentIntent.putExtra("bike_id", bikeId); // Pass the bike_id dynamically
                rentIntent.putExtra("bike_name", name);
                rentIntent.putExtra("bike_type", type);
                rentIntent.putExtra("station_name", station);
                rentIntent.putExtra("price_hourly", hourly);
                rentIntent.putExtra("price_daily", daily);
                rentIntent.putExtra("price_monthly", monthly);

                // Start ReservationActivity to handle the rental process
                startActivity(rentIntent);
            }
        });
    }

    private void loadReservedTimes(final int bikeId) {
        // Create a list to hold the reserved times
        final List<ReservedTime> reservedTimes = new ArrayList<>();

        // Fetch data from the database (using a background thread or AsyncTask)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.1.2/fetch_reserved_times.php?bike_id=" + bikeId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject reservation = jsonArray.getJSONObject(i);
                                String startDate = reservation.getString("start_date");
                                String endDate = reservation.getString("end_date");

                                // Add the reserved time to the list
                                reservedTimes.add(new ReservedTime(startDate, endDate));
                            }

                            // Set up the RecyclerView adapter with the data
                            ReservedTimesAdapter adapter = new ReservedTimesAdapter(reservedTimes);
                            RecyclerView recyclerView = findViewById(R.id.reservedTimesRecyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(BikeDetailActivity.this));
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
