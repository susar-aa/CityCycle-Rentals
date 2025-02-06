package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPhone;
    Button btnUpdateProfile;
    ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize SharedPreferences to fetch user details
        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        imgProfilePic = findViewById(R.id.imgProfilePic);

        // Get user data from SharedPreferences (or a default value)
        String username = prefs.getString("username", "");
        String id = prefs.getString("id", "");
        String email = prefs.getString("email", "");
        String phone_number = prefs.getString("phone_number", "");

        // Load user data into the EditText fields
        etUsername.setText(username);
        etEmail.setText(email);
        etPhone.setText(phone_number);

        // Handle Profile Update Button
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone_number = etPhone.getText().toString().trim();

                // Get user_id from SharedPreferences
                Log.d("UpdateProfile", "User ID: " + id);

                if (name.isEmpty() || email.isEmpty() || phone_number.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send updated data to the server
                updateProfileData(id, name, email, phone_number);
            }
        });

        // Click on Profile Picture to change it (Optional)
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "Change Profile Picture (Feature Coming Soon)", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous screen or ProfileActivity
                onBackPressed();  // This is for going back to the previous screen
            }
        });
    }

    // Method to update profile data
    private void updateProfileData(final String userId, final String name, final String email, final String phone_number) {
        // Set up the URL for your PHP update script
        String url = "http://192.168.1.2/update_profile.php";  // Update this with the correct URL

        // Create a StringRequest to send data to the server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ServerResponse", "Response: " + response); // Log the response
                        try {
                            // Try to parse the response
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();

                                // Save updated user data to SharedPreferences
                                SharedPreferences.Editor editor = getSharedPreferences("UserDetails", MODE_PRIVATE).edit();
                                editor.putString("username", name);
                                editor.putString("email", email);
                                editor.putString("phone_number", phone_number);
                                editor.apply();

                                // Create an Intent to return the result
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updatedUsername", name);
                                resultIntent.putExtra("updatedEmail", email);
                                resultIntent.putExtra("updatedPhone", phone_number);

                                // Set the result to OK and send the updated data
                                setResult(RESULT_OK, resultIntent);

                                // Finish the activity and return to UserProfileActivity
                                finish();
                            } else {
                                String errorMsg = jsonResponse.getString("error");
                                Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Create a map of parameters to send
                Map<String, String> params = new HashMap<>();
                params.put("id", userId);
                params.put("username", name);
                params.put("email", email);
                params.put("phone_number", phone_number);

                Log.d("UpdateProfile", "Params: " + params.toString()); // Log parameters before sending

                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
