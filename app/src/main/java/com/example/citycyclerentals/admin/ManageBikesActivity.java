package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.adapters.AdminBikeAdapter;
import com.example.citycyclerentals.models.Bike;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageBikesActivity extends AppCompatActivity implements AdminBikeAdapter.OnBikeClickListener {

    private static final String TAG = "ManageBikesActivity";
    private static final int ADD_BIKE_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private AdminBikeAdapter bikeAdapter;
    private List<Bike> bikeList;
    private Button addBikeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bikes);
        Log.d(TAG, "onCreate: started");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bikeList = new ArrayList<>();
        bikeAdapter = new AdminBikeAdapter(this, bikeList, this);
        recyclerView.setAdapter(bikeAdapter);
        Log.d(TAG, "onCreate: RecyclerView and Adapter are set");

        addBikeButton = findViewById(R.id.add_bike_button);
        addBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageBikesActivity.this, AddBikeActivity.class);
                startActivityForResult(intent, ADD_BIKE_REQUEST_CODE);
            }
        });

        fetchBikes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_BIKE_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchBikes();
            if (data != null) {
                String message = data.getStringExtra("message");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchBikes() {
        String url = "http://192.168.1.2/CityCycle%20Rentals/fetch_bikes.php"; // Adjust URL as needed
        Log.d(TAG, "fetchBikes: URL - " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d(TAG, "fetchBikes: RequestQueue created");

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: received response");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            bikeList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject bikeObject = jsonArray.getJSONObject(i);
                                Log.d(TAG, "onResponse: processing bike " + bikeObject);

                                int id = bikeObject.getInt("id");
                                String name = bikeObject.getString("name");
                                boolean isAvailable = bikeObject.optInt("availability", 0) == 1;
                                String type = bikeObject.getString("type");
                                int stationId = bikeObject.getInt("station_id");
                                String imageUrl = bikeObject.getString("image_url");
                                double priceHourly = bikeObject.getDouble("price_hourly");
                                double priceDaily = bikeObject.getDouble("price_daily");
                                double priceMonthly = bikeObject.getDouble("price_monthly");
                                String stationName = bikeObject.getString("station_name");
                                String status = bikeObject.optString("status", "unknown"); // Handle missing status

                                Bike bike = new Bike(id, name, isAvailable, type, stationId, imageUrl, priceHourly, priceDaily, priceMonthly, stationName, status);
                                bikeList.add(bike);
                                Log.d(TAG, "onResponse: added bike " + bike.getName());
                            }
                            bikeAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onResponse: bikeAdapter notified");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: JSON parsing error", e);
                            Toast.makeText(ManageBikesActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error fetching bikes", error);
                        Toast.makeText(ManageBikesActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(TAG, "fetchBikes: Adding request to queue");
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBikeClick(Bike bike) {
        // Handle bike click event to edit bike details
        Intent intent = new Intent(ManageBikesActivity.this, EditBikeActivity.class);
        intent.putExtra("bike", bike);
        startActivity(intent);
    }

    @Override
    public void onBikeDelete(Bike bike) {
        // Handle bike delete event
        new AlertDialog.Builder(this)
                .setTitle("Delete Bike")
                .setMessage("Are you sure you want to delete this bike?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Code to delete the bike
                    deleteBike(bike);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteBike(Bike bike) {
        String url = "http://192.168.1.2/CityCycle%20Rentals/delete_bike.php?id=" + bike.getId(); // Adjust URL as needed
        Log.d(TAG, "deleteBike: URL - " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("success")) {
                                Toast.makeText(ManageBikesActivity.this, "Bike deleted successfully", Toast.LENGTH_SHORT).show();
                                bikeList.remove(bike);
                                bikeAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ManageBikesActivity.this, "Error deleting bike", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ManageBikesActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error deleting bike", error);
                        Toast.makeText(ManageBikesActivity.this, "Error deleting bike", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(TAG, "deleteBike: Adding request to queue");
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBikeAvailabilityChange(Bike bike, boolean isAvailable) {
        // Handle bike availability change
        bike.setAvailable(isAvailable);
        updateBikeAvailability(bike);
    }

    private void updateBikeAvailability(Bike bike) {
        String url = "http://192.168.1.2/CityCycle%20Rentals/update_bike_availability.php?id=" + bike.getId() + "&availability=" + (bike.isAvailable() ? 1 : 0); // Adjust URL as needed
        Log.d(TAG, "updateBikeAvailability: URL - " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("success")) {
                                Toast.makeText(ManageBikesActivity.this, "Bike availability updated successfully", Toast.LENGTH_SHORT).show();
                                bikeAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ManageBikesActivity.this, "Error updating bike availability", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ManageBikesActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error updating bike availability", error);
                        Toast.makeText(ManageBikesActivity.this, "Error updating bike availability", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(TAG, "updateBikeAvailability: Adding request to queue");
        requestQueue.add(stringRequest);
    }
}