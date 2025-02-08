package com.example.citycyclerentals.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBikeActivity extends AppCompatActivity {

    private static final String TAG = "AddBikeActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Spinner stationSpinner;
    private List<Station> stationList;
    private EditText nameEditText, typeEditText, priceHourlyEditText, priceDailyEditText, priceMonthlyEditText;
    private ImageView bikeImageView;
    private Button uploadImageButton;
    private Bitmap bitmap;
    private String imageString;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

        nameEditText = findViewById(R.id.bike_name);
        typeEditText = findViewById(R.id.bike_type);
        priceHourlyEditText = findViewById(R.id.bike_price_hourly);
        priceDailyEditText = findViewById(R.id.bike_price_daily);
        priceMonthlyEditText = findViewById(R.id.bike_price_monthly);
        bikeImageView = findViewById(R.id.bike_image);
        uploadImageButton = findViewById(R.id.upload_image_button);
        stationSpinner = findViewById(R.id.station_spinner);
        stationList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding bike...");
        progressDialog.setCancelable(false);

        fetchStations();

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBike();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void fetchStations() {
        String url = "http://192.168.1.2/CityCycle%20Rentals/fetch_stations.php";
        Log.d(TAG, "fetchStations: URL - " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: received response");
                        try {
                            if (response.length() > 0 && response.getJSONObject(0).has("error")) {
                                String error = response.getJSONObject(0).getString("error");
                                Toast.makeText(AddBikeActivity.this, error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject stationObject = response.getJSONObject(i);
                                Log.d(TAG, "onResponse: processing station " + stationObject);

                                int id = stationObject.getInt("id");
                                String name = stationObject.getString("name");
                                double latitude = stationObject.getDouble("latitude");
                                double longitude = stationObject.getDouble("longitude");

                                Station station = new Station(id, name, latitude, longitude);
                                stationList.add(station);
                            }
                            ArrayAdapter<Station> adapter = new ArrayAdapter<>(AddBikeActivity.this, android.R.layout.simple_spinner_item, stationList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            stationSpinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: JSON parsing error", e);
                            Toast.makeText(AddBikeActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error fetching stations", error);
                        Toast.makeText(AddBikeActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = handleImageOrientation(filePath);
                bikeImageView.setImageBitmap(bitmap);
                imageString = getStringImage(bitmap);
                Log.d(TAG, "onActivityResult: Image selected and converted to Base64");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: Error processing image", e);
            }
        }
    }

    private Bitmap handleImageOrientation(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        inputStream = getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(inputStream);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        inputStream.close();

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return originalBitmap;
        }

        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void addBike() {
        String name = nameEditText.getText().toString();
        String type = typeEditText.getText().toString();
        String priceHourly = priceHourlyEditText.getText().toString();
        String priceDaily = priceDailyEditText.getText().toString();
        String priceMonthly = priceMonthlyEditText.getText().toString();
        Station selectedStation = (Station) stationSpinner.getSelectedItem();
        String stationId = String.valueOf(selectedStation.getId());

        if (name.isEmpty() || type.isEmpty() || priceHourly.isEmpty() || priceDaily.isEmpty() || priceMonthly.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bitmap == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.2/CityCycle%20Rentals/add_bike.php"; // Adjust URL as needed
        Log.d(TAG, "addBike: URL - " + url);

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
                        Toast.makeText(AddBikeActivity.this, "Bike added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddBikeActivity.this, ManageBikesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Close activity after adding bike
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onErrorResponse: Error adding bike", error);
                        if (error.networkResponse != null) {
                            Log.e(TAG, "onErrorResponse: Status code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "onErrorResponse: Response data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(AddBikeActivity.this, "Error adding bike", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("type", type);
                params.put("price_hourly", priceHourly);
                params.put("price_daily", priceDaily);
                params.put("price_monthly", priceMonthly);
                params.put("station_id", stationId);
                params.put("image", imageString);
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