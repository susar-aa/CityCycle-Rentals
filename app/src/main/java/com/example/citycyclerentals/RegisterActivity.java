package com.example.citycyclerentals;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtEmail, edtPhone, edtPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show the ProgressBar before starting registration
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE); // Show ProgressBar
                }

                // Automatically set role to "user"
                registerUser(username, email, phone, password);
            }
        });

        // Handle "Login" click to go back to LoginActivity
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(final String username, final String email, final String phone, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.2/register.php",
                new RegisterResponseListener(), new RegisterErrorListener()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("phone", phone);
                params.put("password", password);
                params.put("role", "user");  // Role is automatically set to "user"
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private class RegisterResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            Log.d("Register Response", response);  // Log the response for debugging

            // Hide ProgressBar once the response is received
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE); // Hide ProgressBar
            }

            if (response.equals("success")) {
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));  // Redirect to home screen
                finish();
            } else if (response.equals("error: Email already exists")) {
                Toast.makeText(RegisterActivity.this, "Email Already Exists", Toast.LENGTH_SHORT).show();
            } else if (response.equals("error: Phone number already exists")) {
                Toast.makeText(RegisterActivity.this, "Phone Number Already Exists", Toast.LENGTH_SHORT).show();
            } else {
                // Show the error message from the PHP script
                Toast.makeText(RegisterActivity.this, "Registration failed: " + response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RegisterErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Hide ProgressBar if there's an error
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE); // Hide ProgressBar
            }

            Toast.makeText(RegisterActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
        }
    }
}