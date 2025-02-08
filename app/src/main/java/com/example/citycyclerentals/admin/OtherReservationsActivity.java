package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.citycyclerentals.ManageReservationsActivity;
import com.example.citycyclerentals.R;

public class OtherReservationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_reservations);

        Button OtherReservationsButton = findViewById(R.id.pendingreservationbutton);
        OtherReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherReservationsActivity.this, ManageReservationsActivity.class);
                startActivity(intent);
            }
        });

        Button PaidReservationsButton = findViewById(R.id.paidreservationsbutton);
        PaidReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherReservationsActivity.this, PaidReservationsActivity.class);
                startActivity(intent);
            }
        });

        Button EndedReservationsButton = findViewById(R.id.endedreservationsbutton);
        EndedReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherReservationsActivity.this, EndedReservationsActivity.class);
                startActivity(intent);
            }
        });

        Button ConfirmedReservationsButton = findViewById(R.id.confirmedreservationsbutton);
        ConfirmedReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherReservationsActivity.this, ConfirmedReservationActivity.class);
                startActivity(intent);
            }
        });


        Button CanceledReservationsButton = findViewById(R.id.canceledreservationsbutton);
        CanceledReservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherReservationsActivity.this, CanceledReservationsActivity.class);
                startActivity(intent);
            }
        });
    }
}