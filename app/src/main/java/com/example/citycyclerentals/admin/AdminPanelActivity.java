package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.citycyclerentals.LoginActivity;
import com.example.citycyclerentals.ManageReservationsActivity;
import com.example.citycyclerentals.R;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);

        Button PendingReservationsButton = findViewById(R.id.pendingreservationbutton);
        PendingReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ManageReservationsActivity.class);
                startActivity(intent);
            }
        });

        Button OtherReservationsButton = findViewById(R.id.otherreservationbutton);
        OtherReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, OtherReservationsActivity.class);
                startActivity(intent);
            }
        });

        Button ManageUsersButton = findViewById(R.id.manageuserbutton);
        ManageUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ManageUsersActivity.class);
                startActivity(intent);
            }
        });

        Button ManageAdminsButton = findViewById(R.id.manageadminbutton);
        ManageAdminsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ManageAdminsActivity.class);
                startActivity(intent);
            }
        });

        Button ManageBikesButton = findViewById(R.id.managebikesbutton);
        ManageBikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ManageBikesActivity.class);
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

                Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears backstack
                startActivity(intent);
                finish();
            }
        });

        Button ManageStationsButton = findViewById(R.id.managestationsbutton);
        ManageStationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ManageStationsActivity.class);
                startActivity(intent);
            }
        });

        Button ManagePromotionsButton = findViewById(R.id.managepromotionsbutton);
        ManagePromotionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ManagePromotionsActivity.class);
                startActivity(intent);
            }
        });
    }
}