package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView txtUsername, txtEmail, txtPhone;
    private ImageView imgProfilePic;
    private Button btnManageProfile, btnManagePayment;
    private String userId; // Retrieve the logged-in user's ID from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI components
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        imgProfilePic = findViewById(R.id.imgProfilePic); // Add ImageView for profile picture
        btnManageProfile = findViewById(R.id.btnManageProfile);
        btnManagePayment = findViewById(R.id.btnManagePayment);

        // Retrieve the logged-in user's data from SharedPreferences
        loadUserProfileData();

        // Show available cards for the logged-in user
        displayUserCards(userId);

        setupBottomNavigation();

        // Button to manage profile (Edit profile)
        btnManageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an activity or dialog to manage profile details
                Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, 100); // Start the activity for result
            }
        });

        // Button to manage payment information
        btnManagePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an activity or dialog to manage payment info
                Intent intent = new Intent(UserProfileActivity.this, ManagePayment.class);
                startActivity(intent);
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);

        // Set OnClickListener for Logout Button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the SharedPreferences and redirect to login screen
                SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears backstack
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to load user profile data from SharedPreferences
    private void loadUserProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        userId = sharedPreferences.getString("id", "");  // Get user ID
        String username = sharedPreferences.getString("username", "");  // Get username
        String email = sharedPreferences.getString("email", "");  // Get email
        String phone_number = sharedPreferences.getString("phone", "");  // Get phone
        String profilePicUrl = sharedPreferences.getString("profile_picture", ""); // Get profile picture URL

        // Set the user details in the TextViews
        txtUsername.setText(username);
        txtEmail.setText(email);
        txtPhone.setText(phone_number);

        // Load the profile picture (you can use Glide or Picasso to load images from URL)
        if (!profilePicUrl.isEmpty()) {
            // You can use libraries like Glide or Picasso to load images
            // Glide.with(this).load(profilePicUrl).into(imgProfilePic);
            // Picasso.get().load(profilePicUrl).into(imgProfilePic);
        } else {
            imgProfilePic.setImageResource(R.drawable.profile_placeholder_image); // Default placeholder image
        }
    }

    // Method to display available cards for the logged-in user based on the userId
    private void displayUserCards(String userId) {
        if (userId != null && !userId.isEmpty()) {
            // Your logic to display user-specific cards
        } else {
            Toast.makeText(this, "No cards available for the logged-in user.", Toast.LENGTH_SHORT).show();
        }
    }

    // Override onActivityResult to reload the profile data when returning from EditProfileActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if we came back from EditProfileActivity and if the result was OK
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Retrieve the updated user data from the Intent
            String updatedUsername = data.getStringExtra("updatedUsername");
            String updatedEmail = data.getStringExtra("updatedEmail");
            String updatedPhone = data.getStringExtra("updatedPhone");

            // Update SharedPreferences with new data
            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", updatedUsername);
            editor.putString("email", updatedEmail);
            editor.putString("phone_number", updatedPhone);
            editor.apply();

            // Reload the user profile data with the updated information
            loadUserProfileData();

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        // Initialize the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        // Set up item selected listener for Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Use if-else instead of switch statement for resource IDs
                if (item.getItemId() == R.id.nav_profile) {
                    // Handle home item click (already on Profile)
                    return true;
                } else if (item.getItemId() == R.id.nav_home) {
                        // Navigate to HomeActivity when Home button is clicked
                        Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                        return true;
                    }else if (item.getItemId() == R.id.nav_dashboard) {
                    startActivity(new Intent(UserProfileActivity.this, DashboardActivity.class));

                }return false;
            }
        });
    }
}
