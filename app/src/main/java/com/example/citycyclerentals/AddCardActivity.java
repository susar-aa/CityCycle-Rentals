package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class AddCardActivity extends AppCompatActivity {

    private EditText etCardNumber, etExpiryDate, etCardHolder, etCVV;
    private Button btnSaveCard;
    private static final String TAG = "AddCardActivity";
    private static final String SAVE_CARD_URL = "http://192.168.1.2/CityCycle%20Rentals/save_card.php";  // Define the URL here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etCardHolder = findViewById(R.id.etCardHolder);
        etCVV = findViewById(R.id.etCVV);
        btnSaveCard = findViewById(R.id.btnSaveCard);

        // Set the OnClickListener for the button
        btnSaveCard.setOnClickListener(v -> saveCard());
    }

    private void saveCard() {
        // Retrieve userId from SharedPreferences (correct file name: "UserDetails")
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", "");  // Use "id" as in LoginActivity

        Log.d(TAG, "Retrieved userId: " + userId);  // Check if userId is being retrieved properly

        if (userId.isEmpty()) {
            Toast.makeText(AddCardActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String cardNumber = etCardNumber.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String cardHolder = etCardHolder.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();

        if (cardNumber.isEmpty() || expiryDate.isEmpty() || cardHolder.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(AddCardActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send data to the PHP script using Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SAVE_CARD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle success response
                        Log.d(TAG, "Response: " + response);
                        Toast.makeText(AddCardActivity.this, "Card saved successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to ManagePaymentActivity
                        Intent intent = new Intent(AddCardActivity.this, ManagePayment.class);
                        startActivity(intent);
                        finish();  // Optional: to ensure that the user can't return to this activity via back button
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(AddCardActivity.this, "Error saving card", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Add parameters to be sent in the POST request
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("userId", userId);  // Send user ID from SharedPreferences
                params.put("cardNumber", cardNumber);
                params.put("expiryDate", expiryDate);
                params.put("cardHolder", cardHolder);
                params.put("cvv", cvv);
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }
}
