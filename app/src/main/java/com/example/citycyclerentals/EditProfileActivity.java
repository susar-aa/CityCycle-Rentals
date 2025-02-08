package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPhone;
    Button btnUpdateProfile;
    ImageView imgProfilePic;
    String profileImageUrl;

    private static final int IMAGE_PICKER_REQUEST_CODE = 100;
    private static final String TAG = "EditProfileActivity";

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
        profileImageUrl = prefs.getString("profile_picture", "");

        // Load user data into the EditText fields
        etUsername.setText(username);
        etEmail.setText(email);
        etPhone.setText(phone_number);

        // Load profile picture
        if (!profileImageUrl.isEmpty()) {
            Picasso.get().load(profileImageUrl).into(imgProfilePic);
        } else {
            imgProfilePic.setImageResource(R.drawable.profile_placeholder_image); // Default placeholder image
        }

        // Handle Profile Update Button
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone_number = etPhone.getText().toString().trim();


                // Get user_id from SharedPreferences
                Log.d(TAG, "User ID: " + id);

                if (name.isEmpty() || email.isEmpty() || phone_number.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send updated data to the server
                updateProfileData(id, name, email, phone_number, profileImageUrl);
            }
        });

        // Click on Profile Picture to change it
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfileActivity.this)
                        .crop()                    // Crop image(Optional), Check Customization for more option
                        .compress(1024)            // Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080(Optional)
                        .start(IMAGE_PICKER_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICKER_REQUEST_CODE) {
            Uri fileUri = data.getData();
            imgProfilePic.setImageURI(fileUri);

            // Upload image
            uploadImage(fileUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri fileUri) {
        File file = new File(fileUri.getPath());
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://192.168.1.2/CityCycle%20Rentals/upload_profile_picture.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Image upload failed", e);
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EditProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("error")) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    Toast.makeText(EditProfileActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    Log.e(TAG, "Failed to parse upload response", e);
                                }
                            });
                        } else if (jsonObject.has("url")) {
                            profileImageUrl = "http://192.168.1.2/CityCycle%20Rentals/" + jsonObject.getString("url");
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(EditProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                Picasso.get().load(profileImageUrl).into(imgProfilePic);
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse upload response", e);
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EditProfileActivity.this, "Failed to parse upload response", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    // Method to update profile data
    private void updateProfileData(final String userId, final String name, final String email, final String phone_number, final String profileImageUrl) {
        // Set up the URL for your PHP update script
        String url = "http://192.168.1.2/CityCycle%20Rentals/update_profile.php";  // Update this with the correct URL

        // Create a StringRequest to send data to the server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response); // Log the response
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
                                editor.putString("profile_picture", profileImageUrl);
                                editor.apply();

                                // Create an Intent to return the result
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updatedUsername", name);
                                resultIntent.putExtra("updatedEmail", email);
                                resultIntent.putExtra("updatedPhone", phone_number);
                                resultIntent.putExtra("updatedProfilePicture", profileImageUrl);

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
                params.put("profile_picture", profileImageUrl);

                Log.d(TAG, "Params: " + params.toString()); // Log parameters before sending

                return params;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}