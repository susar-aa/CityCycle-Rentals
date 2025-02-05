package com.example.citycyclerentals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.adapters.BikeAdapter;
import com.example.citycyclerentals.models.Bike;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String BIKE_URL = "http://192.168.1.2/get_bikes.php";
    private static final String URL = "http://192.168.1.2/get_promotions.php";
    private ViewFlipper promoFlipper;
    private RecyclerView bikeRecyclerView;
    private RequestQueue requestQueue;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        promoFlipper = findViewById(R.id.promoflipper);
        requestQueue = Volley.newRequestQueue(this);

        setupBottomNavigation();
        fetchPromotions();

        Button viewReservationsButton = findViewById(R.id.viewReservationsButton);

        // Set an onClick listener to navigate to UserReservationActivity
        viewReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, UserReservationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
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
                }else if (item.getItemId() == R.id.nav_dashboard) {
                    return true;

                } else if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void fetchPromotions() {
        // Request a string response from the provided URL
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // On success, display the data
                        try {
                            // Convert JSON array to list of Promotion objects
                            List<Promotion> promotions = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject promoObject = response.getJSONObject(i);
                                String promoCode = promoObject.getString("promo_code");
                                String discountPercentage = promoObject.getString("discount_percentage");
                                String endDate = promoObject.getString("end_date");
                                String startDate = promoObject.getString("start_date");

                                // Add each promotion to the list
                                promotions.add(new Promotion(promoCode, discountPercentage, endDate, startDate));
                            }

                            // Update the UI with the fetched promotions (for example, adding them to a ViewFlipper)
                            updatePromoFlipper(promotions);

                        } catch (JSONException e) {
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

        // Add the request to the RequestQueue
        jsonArrayRequest.setTag(TAG); // Tag to cancel requests later if needed
        requestQueue.add(jsonArrayRequest);
    }

    private void updatePromoFlipper(List<Promotion> promotions) {
        // Clear existing views in the flipper
        promoFlipper.removeAllViews();

        // Inflate and add each promotion view
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

}
