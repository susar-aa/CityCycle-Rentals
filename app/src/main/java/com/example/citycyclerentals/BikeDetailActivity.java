package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

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
}
