package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.citycyclerentals.admin.AdminPanelActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button nextButton;
    private int currentVideo = 1;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getString("id", null) != null;

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.videoView);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setVisibility(View.GONE); // Initially hide the button

        playVideo1();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo2();
                nextButton.setVisibility(View.GONE);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (currentVideo == 1 && isLoggedIn) {
                    navigateToHome();
                } else if (currentVideo == 2) {
                    navigateToLogin();
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void playVideo1() {
        currentVideo = 1;
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_in);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    private void playVideo2() {
        currentVideo = 2;
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_out);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    private void navigateToHome() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "user");

        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(SplashActivity.this, AdminPanelActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}