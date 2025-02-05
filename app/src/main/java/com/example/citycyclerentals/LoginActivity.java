package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView txtCreateAccount; // New TextView for Create Account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        txtCreateAccount = findViewById(R.id.txtCreateAccount); // Initialize Create Account TextView

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // Check if email or password is empty
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);  // Show progress bar during login
                loginUser(email, password);
            }
        });

        // Handle "Create Account" click
        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loginUser(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.2/login.php", // Adjust URL
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);  // Hide progress bar after response

                        try {
                            // Log the response for debugging
                            System.out.println("Response: " + response);
                            JSONObject userData = new JSONObject(response);

                            if (userData.has("error")) {
                                // Handle invalid credentials or role
                                Toast.makeText(LoginActivity.this, userData.getString("error"), Toast.LENGTH_SHORT).show();
                            } else {
                                // Store user data in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String userId = userData.getString("id"); // Get user ID from the response
                                Log.d("UserID", "Logged in user ID: " + userId);

                                // Store the received user data in SharedPreferences
                                editor.putString("id", userData.getString("id"));
                                editor.putString("username", userData.getString("username"));
                                editor.putString("email", userData.getString("email"));
                                editor.putString("phone", userData.optString("phone", "")); // Handle null phone gracefully
                                editor.putString("role", userData.getString("role"));
                                editor.apply();

                                // Navigate based on user role
                                String role = userData.getString("role");
                                if ("admin".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                }

                                finish();  // Close LoginActivity
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);  // Hide progress bar if error occurs
                        Toast.makeText(LoginActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password); // Ensure this is securely handled
                return params;
            }
        };

        // Add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
