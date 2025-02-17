package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.adapters.DashboardReservationAdapter;
import com.example.citycyclerentals.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private static final String URL = "http://192.168.1.2/CityCycle%20Rentals/get_promotions.php";
    private ViewFlipper promoFlipper;
    private RequestQueue requestQueue;
    private ListView reservationListView;
    private ArrayList<Reservation> reservationList;
    private DashboardReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        reservationListView = findViewById(R.id.confirmedreservationListView);
        reservationList = new ArrayList<>();

        promoFlipper = findViewById(R.id.promoflipper);
        requestQueue = Volley.newRequestQueue(this);

        setupBottomNavigation();
        fetchPromotions();

        Button viewReservationsButton = findViewById(R.id.viewReservationsButton);
        viewReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, UserReservationActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userIdString = sharedPreferences.getString("id", "");

        Log.d("DashboardActivity", "Retrieved userId: " + userIdString);

        if (userIdString.isEmpty()) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int userId = Integer.parseInt(userIdString);
                Log.d("DashboardActivity", "Converted userId: " + userId);
                if (userId != -1) {
                    fetchReservations(userId);
                } else {
                    Toast.makeText(this, "Invalid User ID!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Log.e("DashboardActivity", "Invalid User ID format: " + e.getMessage());
                Toast.makeText(this, "Error processing User ID!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(DashboardActivity.this, UserProfileActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_dashboard) {
                    return true;
                } else if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchPromotions() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Promotion> promotions = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject promoObject = response.getJSONObject(i);
                                String promoCode = promoObject.getString("promo_code");
                                String discountPercentage = promoObject.getString("discount_percentage");
                                String endDate = promoObject.getString("end_date");
                                String startDate = promoObject.getString("start_date");
                                promotions.add(new Promotion(promoCode, discountPercentage, endDate, startDate));
                            }
                            updatePromoFlipper(promotions);
                        } catch (Exception e) {
                            Log.e(TAG, "JSON Parsing error", e);
                            Toast.makeText(DashboardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: ", error);
                        Toast.makeText(DashboardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        jsonArrayRequest.setTag(TAG);
        requestQueue.add(jsonArrayRequest);
    }

    private void updatePromoFlipper(List<Promotion> promotions) {
        promoFlipper.removeAllViews();
        for (Promotion promo : promotions) {
            LayoutInflater inflater = getLayoutInflater();
            View promoCard = inflater.inflate(R.layout.promo_card, promoFlipper, false);
            TextView promoCodeText = promoCard.findViewById(R.id.promoCode);
            TextView discountText = promoCard.findViewById(R.id.discountPercentage);
            TextView validDateText = promoCard.findViewById(R.id.validdate);
            TextView startdateText = promoCard.findViewById(R.id.startdate);
            promoCodeText.setText(promo.getPromoCode());
            discountText.setText(promo.getDiscountPercentage() + "% Off");
            startdateText.setText("Starting Date : " + promo.getStartDate());
            validDateText.setText("Ending Date : " + promo.getEndDate());
            promoFlipper.addView(promoCard);
        }
    }

    private void fetchReservations(int userId) {
        String url = "http://192.168.1.2/CityCycle%20Rentals/getReservations.php?userId=" + userId;

        Log.d(TAG, "Fetching reservations from URL: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            reservationList.clear();  // Clear previous data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject reservationObject = response.getJSONObject(i);

                                int reservationId = reservationObject.getInt("reservation_id");
                                String name = reservationObject.getString("name");
                                String startDate = reservationObject.getString("start_date");
                                String endDate = reservationObject.getString("end_date");
                                double totalPrice = reservationObject.getDouble("total_price");
                                String status = reservationObject.getString("status");

                                // Only add the reservation if the status is "confirmed" or "pending"
                                if ("confirmed".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                                    int bikeId = reservationObject.getInt("bike_id");
                                    String bikeImageUrl = reservationObject.getString("bike_image_url");
                                    String bikeName = reservationObject.getString("bike_name");
                                    double discount = reservationObject.getDouble("discount");

                                    // Create Reservation object
                                    Reservation reservation = new Reservation(reservationId, name, startDate, endDate, totalPrice, status, bikeImageUrl, bikeName, bikeId, discount);
                                    reservationList.add(reservation);
                                }
                            }

                            // Update the adapter with the filtered reservation list
                            adapter = new DashboardReservationAdapter(DashboardActivity.this, reservationList);
                            reservationListView.setAdapter(adapter);

                            // Set item click listener
                            reservationListView.setOnItemClickListener((parent, view, position, id) -> {
                                Reservation clickedReservation = reservationList.get(position);
                                Intent intent = new Intent(DashboardActivity.this, ReservationDetailActivity.class);
                                intent.putExtra("reservation_id", clickedReservation.getReservationId());
                                intent.putExtra("bike_name", clickedReservation.getBikeName());
                                intent.putExtra("start_date", clickedReservation.getStartDate());
                                intent.putExtra("end_date", clickedReservation.getEndDate());
                                intent.putExtra("total_price", clickedReservation.getTotalPrice());
                                intent.putExtra("status", clickedReservation.getStatus());
                                intent.putExtra("bike_image_url", clickedReservation.getBikeImageUrl());
                                intent.putExtra("discount", clickedReservation.getDiscount());
                                startActivity(intent);
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching reservations: " + error.getMessage());
                        Toast.makeText(DashboardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }
}