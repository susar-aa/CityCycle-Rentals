package com.example.citycyclerentals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ReservationDetailActivity extends AppCompatActivity {

    private int reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        Button viewBackButton = findViewById(R.id.backButton);
        viewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the data passed from the previous activity
        reservationId = getIntent().getIntExtra("reservation_id", -1);
        String bikeName = getIntent().getStringExtra("bike_name");
        String startDate = getIntent().getStringExtra("start_date");
        String endDate = getIntent().getStringExtra("end_date");
        double totalPrice = getIntent().getDoubleExtra("total_price", 0);
        String status = getIntent().getStringExtra("status");

        // Display the reservation details in the UI
        TextView reservationIdText = findViewById(R.id.reservationDetailsText);
        reservationIdText.setText("Reservation ID: " + reservationId);

        TextView bikeNameText = findViewById(R.id.bikeName);
        bikeNameText.setText("Bike: " + bikeName);

        TextView startDateText = findViewById(R.id.startDate);
        startDateText.setText("Start Date: " + startDate);

        TextView endDateText = findViewById(R.id.endDate);
        endDateText.setText("End Date: " + endDate);

        TextView totalPriceText = findViewById(R.id.totalPrice);
        totalPriceText.setText("Total Price: $" + totalPrice);

        TextView statusText = findViewById(R.id.status);
        statusText.setText("Status: " + status);

        // Set up the Cancel Reservation Button
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservation(reservationId);
            }
        });
    }

    private void cancelReservation(int reservationId) {
        String url = "http://192.168.1.2/cancel_reservation.php?reservation_id=" + reservationId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ReservationCancel", "Response: " + response); // Log the response to see what is returned

                        if ("success".equals(response.trim())) {
                            Toast.makeText(ReservationDetailActivity.this, "Reservation Canceled", Toast.LENGTH_SHORT).show();
                            // Redirect to DashboardActivity after cancellation
                            Intent intent = new Intent(ReservationDetailActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ReservationDetailActivity.this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ReservationCancel", "Error: " + error.getMessage()); // Log the error message
                        Toast.makeText(ReservationDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

        queue.add(stringRequest);
    }
}
