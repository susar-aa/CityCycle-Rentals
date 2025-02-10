package com.example.citycyclerentals.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.AddCardActivity;
import com.example.citycyclerentals.ManagePayment;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.SelectLocationActivity;
import com.example.citycyclerentals.models.Station;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditStationActivity extends AppCompatActivity {

    private static final String TAG = "EditStationActivity";
    private static final int SELECT_LOCATION_REQUEST_CODE = 1;
    private EditText nameEditText, latitudeEditText, longitudeEditText;
    private ProgressDialog progressDialog;
    private Station station;
    private ImageButton btnBack;
    private Button selectLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_station);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        nameEditText = findViewById(R.id.station_name);
        latitudeEditText = findViewById(R.id.station_latitude);
        longitudeEditText = findViewById(R.id.station_longitude);
        selectLocationButton = findViewById(R.id.select_location_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating station...");
        progressDialog.setCancelable(false);

        station = (Station) getIntent().getSerializableExtra("station");
        if (station != null) {
            populateFields();
        }

        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStation();
            }
        });

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditStationActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, SELECT_LOCATION_REQUEST_CODE);
            }
        });
    }

    private void populateFields() {
        nameEditText.setText(station.getName());
        latitudeEditText.setText(String.valueOf(station.getLatitude()));
        longitudeEditText.setText(String.valueOf(station.getLongitude()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_LOCATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);
            latitudeEditText.setText(String.valueOf(latitude));
            longitudeEditText.setText(String.valueOf(longitude));
        }
    }

    private void updateStation() {
        String name = nameEditText.getText().toString();
        String latitude = latitudeEditText.getText().toString();
        String longitude = longitudeEditText.getText().toString();

        if (name.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.2/CityCycle%20Rentals/update_station.php"; // Adjust URL as needed
        Log.d(TAG, "updateStation: URL - " + url);

        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("success")) {
                                Toast.makeText(EditStationActivity.this, "Station updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditStationActivity.this, ManageStationsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Close activity after updating station
                            } else {
                                Toast.makeText(EditStationActivity.this, "Error updating station", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditStationActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onErrorResponse: Error updating station", error);
                        if (error.networkResponse != null) {
                            Log.e(TAG, "onErrorResponse: Status code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "onErrorResponse: Response data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(EditStationActivity.this, "Error updating station", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(station.getId()));
                params.put("name", name);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                Log.d(TAG, "getParams: " + params.toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
}