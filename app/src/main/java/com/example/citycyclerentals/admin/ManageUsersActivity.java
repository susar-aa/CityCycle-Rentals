package com.example.citycyclerentals.admin;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycyclerentals.R;
import com.example.citycyclerentals.adapters.UserAdapter;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import com.example.citycyclerentals.admin.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManageUsersActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private static final String TAG = "ManageUsersActivity";
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private Button addUserButton;
    private static final int IMAGE_PICKER_REQUEST_CODE = 100;
    private String profileImageUrl;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapter = new UserAdapter(this, userList, this);
        recyclerView.setAdapter(adapter);
        addUserButton = findViewById(R.id.add_user_button);

        fetchUsers();

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });
    }

    private void fetchUsers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/get_users.php"); // Update with your server URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    JSONArray users = new JSONArray(response.toString());

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        int id = user.getInt("id");
                        String username = user.getString("username");
                        String email = user.getString("email");
                        String phoneNumber = user.getString("phone_number");
                        String role = user.getString("role");
                        String profilePicture = user.getString("profile_picture");
                        userList.add(new User(id, username, email, phoneNumber, role, profilePicture));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error fetching users", e);
                }
            }
        }).start();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        builder.setView(view);

        final EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        final EditText emailEditText = view.findViewById(R.id.email_edit_text);
        final EditText phoneNumberEditText = view.findViewById(R.id.phone_number_edit_text);
        final EditText passwordEditText = view.findViewById(R.id.password_edit_text);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                addUser(username, email, phoneNumber, password);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addUser(final String username, final String email, final String phoneNumber, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/add_user.php"); // Update with your server URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("username=" + username + "&email=" + email + "&phone_number=" + phoneNumber + "&password=" + password);
                    writer.flush();
                    writer.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final String response = reader.readLine();
                    reader.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ManageUsersActivity.this, response, Toast.LENGTH_LONG).show();
                            if (response.equals("New user created successfully")) {
                                fetchUsers();
                            } else {
                                Log.e(TAG, "Error adding user: " + response);
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error adding user", e);
                }
            }
        }).start();
    }

    private void showEditUserDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        builder.setView(view);

        final EditText usernameEditText = view.findViewById(R.id.username_edit_text);
        final EditText emailEditText = view.findViewById(R.id.email_edit_text);
        final EditText phoneNumberEditText = view.findViewById(R.id.phone_number_edit_text);
        profileImageView = view.findViewById(R.id.profile_image_view);
        final Button uploadImageButton = view.findViewById(R.id.upload_image_button);

        final User user = userList.get(position);

        usernameEditText.setText(user.getUsername());
        emailEditText.setText(user.getEmail());
        phoneNumberEditText.setText(user.getPhoneNumber());

        // Load the profile picture
        if (!user.getProfilePicture().isEmpty()) {
            Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.profile_placeholder_image).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile_placeholder_image);
        }

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ManageUsersActivity.this)
                        .crop()                    // Crop image(Optional), Check Customization for more option
                        .compress(1024)            // Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080(Optional)
                        .start(IMAGE_PICKER_REQUEST_CODE);
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String profilePicture = profileImageUrl; // Use the uploaded image URL

                editUser(user.getId(), newUsername, email, phoneNumber, profilePicture, position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void editUser(final int userId, final String username, final String email, final String phoneNumber, final String profilePicture, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/edit_user.php"); // Update with your server URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("id=" + userId + "&username=" + username + "&email=" + email + "&phone_number=" + phoneNumber + "&profile_picture=" + profilePicture);
                    writer.flush();
                    writer.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final String response = reader.readLine();
                    reader.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ManageUsersActivity.this, response, Toast.LENGTH_LONG).show();
                            if (response.equals("Record updated successfully")) {
                                userList.set(position, new User(userId, username, email, phoneNumber, userList.get(position).getRole(), profilePicture));
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "Error updating user: " + response);
                                Toast.makeText(ManageUsersActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error updating user", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ManageUsersActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void deleteUser(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/delete_user.php"); // Update with your server URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("id=" + userList.get(position).getId());
                    writer.flush();
                    writer.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final String response = reader.readLine();
                    reader.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ManageUsersActivity.this, response, Toast.LENGTH_LONG).show();
                            if (response.equals("Record deleted successfully")) {
                                userList.remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "Error deleting user: " + response);
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error deleting user", e);
                }
            }
        }).start();
    }

    private void changePassword(final int userId, final String newPassword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/change_password.php"); // Update with your server URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("id=" + userId + "&new_password=" + newPassword);
                    writer.flush();
                    writer.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final String response = reader.readLine();
                    reader.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ManageUsersActivity.this, response, Toast.LENGTH_LONG).show();
                            if (!response.equals("Password changed successfully")) {
                                Log.e(TAG, "Error changing password: " + response);
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error changing password", e);
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICKER_REQUEST_CODE) {
            Uri fileUri = data.getData();
            profileImageView.setImageURI(fileUri);

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

        Request request = new Request.Builder()
                .url("http://192.168.1.2/CityCycle%20Rentals/upload_image.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Image upload failed", e);
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(ManageUsersActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("error")) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    Toast.makeText(ManageUsersActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    Log.e(TAG, "Failed to parse upload response", e);
                                }
                            });
                        } else if (jsonObject.has("url")) {
                            profileImageUrl = "http://192.168.1.2/CityCycle%20Rentals/" + jsonObject.getString("url");
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(ManageUsersActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse upload response", e);
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(ManageUsersActivity.this, "Failed to parse upload response", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        showUserOptionsDialog(position);
    }

    private void showUserOptionsDialog(final int position) {
        CharSequence[] options = {"Edit", "Delete", "Change Password"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(userList.get(position).getUsername());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showEditUserDialog(position);
                        break;
                    case 1:
                        deleteUser(position);
                        break;
                    case 2:
                        showChangePasswordDialog(position);
                        break;
                }
            }
        });
        builder.show();
    }

    private void showChangePasswordDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        final EditText newPasswordEditText = view.findViewById(R.id.new_password_edit_text);

        builder.setTitle("Change Password");
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = newPasswordEditText.getText().toString();
                changePassword(userList.get(position).getId(), newPassword);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}