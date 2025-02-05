package com.example.citycyclerentals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
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

public class HomeActivity extends BaseActivity implements BikeAdapter.OnBikeClickListener {

    private static final String TAG = "HomeActivity";
    private static final String BIKE_URL = "http://192.168.1.2/get_bikes.php";
    private RecyclerView bikeRecyclerView;
    private RequestQueue requestQueue;
    private BikeAdapter bikeAdapter;
    private List<Bike> bikeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bikeRecyclerView = findViewById(R.id.bikerecyclerView);
        requestQueue = Volley.newRequestQueue(this);

        bikeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bikeAdapter = new BikeAdapter(this, bikeList, this); // Pass 'this' as OnBikeClickListener
        bikeRecyclerView.setAdapter(bikeAdapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchBikes();
        setupBottomNavigation();

        // Set up search filter
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBikes(newText);
                return false;
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

    private void fetchBikes() {
        toggleLoader(true);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BIKE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        toggleLoader(false);
                        try {
                            bikeList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject bikeObject = response.getJSONObject(i);
                                int bikeId = bikeObject.getInt("bike_id");
                                String bikeName = bikeObject.getString("bike_name");
                                boolean bikeAvailability = bikeObject.getInt("bike_availability") == 1;
                                String bikeType = bikeObject.getString("bike_type");
                                int stationId = bikeObject.getInt("station_id");
                                String bikeImage = bikeObject.getString("bike_image");
                                double bikePriceHourly = bikeObject.getDouble("price_hourly");
                                double bikePriceDaily = bikeObject.getDouble("price_daily");
                                double bikePriceMonthly = bikeObject.getDouble("price_monthly");
                                String stationName = bikeObject.getString("station_name");
                                String status = "Available";

                                // Creating Bike object
                                Bike bike = new Bike(
                                        bikeId, bikeName, bikeAvailability, bikeType,
                                        stationId, bikeImage, bikePriceHourly,
                                        bikePriceDaily, bikePriceMonthly, stationName, status
                                );

                                bikeList.add(bike);
                            }

                            bikeAdapter.updateList(bikeList); // Refresh adapter with new data
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage(), e);
                            showErrorToast("Error parsing bike data.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toggleLoader(false);
                        Log.e(TAG, "Volley error: " + error.getMessage(), error);
                        showErrorToast("Failed to fetch bikes.");
                    }
                });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void filterBikes(String query) {
        List<Bike> filteredList = new ArrayList<>();

        for (Bike bike : bikeList) {
            if (bike.getName().toLowerCase().contains(query.toLowerCase()) ||
                    bike.getStationName().toLowerCase().contains(query.toLowerCase()) ||
                    bike.getType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(bike);
            }
        }

        bikeAdapter.updateList(filteredList);
    }

    @Override
    public void onBikeClick(Bike bike) {
        Intent intent = new Intent(this, BikeDetailActivity.class);
        intent.putExtra("bike_id", bike.getId());
        intent.putExtra("bike_name", bike.getName());
        intent.putExtra("bike_type", bike.getType());
        intent.putExtra("image_url", bike.getImageUrl());
        intent.putExtra("price_hourly", bike.getPriceHourly());
        intent.putExtra("price_daily", bike.getPriceDaily());
        intent.putExtra("price_monthly", bike.getPriceMonthly());
        intent.putExtra("station_name", bike.getStationName());
        intent.putExtra("status", bike.getStatus());
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
                    return true;
                }else if (item.getItemId() == R.id.nav_dashboard) {
                    startActivity(new Intent(HomeActivity.this, DashboardActivity.class));

                } else if (item.getItemId() == R.id.nav_home) {
                    return true;
                }
                return false;
            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }



}
