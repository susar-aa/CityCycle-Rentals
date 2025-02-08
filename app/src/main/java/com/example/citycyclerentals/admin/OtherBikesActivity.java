package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.citycyclerentals.R;

public class OtherBikesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_bikes); // Ensure this layout file contains the button

        Button otherBikesButton = findViewById(R.id.allbikesbutton); // Ensure the ID matches the one in the XML layout file
        otherBikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherBikesActivity.this, ManageBikesActivity.class);
                startActivity(intent);
            }
        });
    }
}