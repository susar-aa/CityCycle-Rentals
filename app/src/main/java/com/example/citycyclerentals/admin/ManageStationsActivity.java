package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.citycyclerentals.adapters.AdminStationAdapter;
import com.example.citycyclerentals.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageStationsActivity extends AppCompatActivity implements AdminStationAdapter.OnStationClickListener {

    private static final String TAG = "ManageStationsActivity";
    private static final int ADD_STATION_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private AdminStationAdapter stationAdapter;
    private List<Station> stationList;
    private Button addStationButton;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stations);
        Log.d(TAG, "onCreate: started");


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stationList = new ArrayList<>();
        stationAdapter = new AdminStationAdapter(this, stationList, this);
        recyclerView.setAdapter(stationAdapter);
        Log.d(TAG, "onCreate: RecyclerView and Adapter are set");

        addStationButton = findViewById(R.id.add_station_button);
        addStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageStationsActivity.this, AddStationActivity.class);
                startActivityForResult(intent, ADD_STATION_REQUEST_CODE);
            }
        });

        fetchStations();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_STATION_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchStations();
            if (data != null) {
                String message = data.getStringExtra("message");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchStations() {
        String url = "http://192.168.1.2/CityCycle%20Rentals/fetch_stations.php"; // Adjust URL as needed
        Log.d(TAG, "fetchStations: URL - " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d(TAG, "fetchStations: RequestQueue created");

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: received response");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            stationList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject stationObject = jsonArray.getJSONObject(i);
                                Log.d(TAG, "onResponse: processing station " + stationObject);

                                int id = stationObject.getInt("id");
                                String name = stationObject.getString("name");
                                double latitude = stationObject.getDouble("latitude");
                                double longitude = stationObject.getDouble("longitude");

                                Station station = new Station(id, name, latitude, longitude);
                                stationList.add(station);
                                Log.d(TAG, "onResponse: added station " + station.getName());
                            }
                            stationAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onResponse: stationAdapter notified");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: JSON parsing error", e);
                            Toast.makeText(ManageStationsActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error fetching stations", error);
                        Toast.makeText(ManageStationsActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(TAG, "fetchStations: Adding request to queue");
        requestQueue.add(stringRequest);
    }

    @Override
    public void onStationClick(Station station) {
        // Handle station click event to edit station details
        Intent intent = new Intent(ManageStationsActivity.this, EditStationActivity.class);
        intent.putExtra("station", station);
        startActivity(intent);
    }

    @Override
    public void onStationDelete(Station station) {
        // Handle station delete event
        new AlertDialog.Builder(this)
                .setTitle("Delete Station")
                .setMessage("Are you sure you want to delete this station?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Code to delete the station
                    deleteStation(station);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteStation(Station station) {
        String url = "http://192.168.1.2/CityCycle%20Rentals/delete_station.php?id=" + station.getId(); // Adjust URL as needed
        Log.d(TAG, "deleteStation: URL - " + url);

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
                                Toast.makeText(ManageStationsActivity.this, "Station deleted successfully", Toast.LENGTH_SHORT).show();
                                stationList.remove(station);
                                stationAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ManageStationsActivity.this, "Error deleting station", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ManageStationsActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error deleting station", error);
                        Toast.makeText(ManageStationsActivity.this, "Error deleting station", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(TAG, "deleteStation: Adding request to queue");
        requestQueue.add(stringRequest);
    }
}