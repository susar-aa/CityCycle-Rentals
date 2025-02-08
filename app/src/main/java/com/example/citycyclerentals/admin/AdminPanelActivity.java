package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
                Intent intent = new Intent(AdminPanelActivity.this, OtherBikesActivity.class);
                startActivity(intent);
            }
        });
    }
}