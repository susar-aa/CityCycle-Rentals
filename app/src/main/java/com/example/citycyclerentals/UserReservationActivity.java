package com.example.citycyclerentals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.adapters.ReservationAdapter;
import com.example.citycyclerentals.models.Reservation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserReservationActivity extends AppCompatActivity {

    private ListView reservationListView;
    private ArrayList<Reservation> reservationList;
    private ReservationAdapter adapter;
    private static final String TAG = "UserReservationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservation);

        // Initialize the ListView
        reservationListView = findViewById(R.id.reservationListView);
        reservationList = new ArrayList<>();

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userIdString = sharedPreferences.getString("id", "");  // Retrieve user ID from SharedPreferences

        // Log the retrieved userId
        Log.d("UserReservationActivity", "Retrieved userId: " + userIdString);

        // Check if the userId is valid
        if (userIdString.isEmpty()) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int userId = Integer.parseInt(userIdString);  // Convert to integer
                Log.d("UserReservationActivity", "Converted userId: " + userId);

                if (userId != -1) {
                    fetchReservations(userId);  // Fetch reservations if userId is valid
                } else {
                    Toast.makeText(this, "Invalid User ID!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Log.e("UserReservationActivity", "Invalid User ID format: " + e.getMessage());
                Toast.makeText(this, "Error processing User ID!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void fetchReservations(int userId) {
        String url = "http://192.168.1.2/getReservations.php?userId=" + userId;

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

                                // Only add the reservation if the status is "paid"
                                if ("paid".equalsIgnoreCase(status) || "canceled".equalsIgnoreCase(status)){
                                    int bikeId = reservationObject.getInt("bike_id");
                                    String bikeImageUrl = reservationObject.getString("bike_image_url");
                                    String bikeName = reservationObject.getString("bike_name");

                                    Log.d("UserReservationActivity", "Bike Image URL: " + bikeImageUrl);

                                    // Create Reservation object
                                    Reservation reservation = new Reservation(reservationId, name, startDate, endDate, totalPrice, status, bikeImageUrl, bikeName, bikeId);
                                    reservationList.add(reservation);
                                }
                            }

                            // Update the adapter with the filtered reservation list
                            adapter = new ReservationAdapter(UserReservationActivity.this, reservationList);
                            reservationListView.setAdapter(adapter);

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
                        Toast.makeText(UserReservationActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

}
