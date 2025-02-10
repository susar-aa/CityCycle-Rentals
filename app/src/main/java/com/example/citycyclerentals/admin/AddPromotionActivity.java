package com.example.citycyclerentals.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.R;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPromotionActivity extends AppCompatActivity {

    private EditText promoCodeEditText, discountPercentageEditText, startDateEditText, endDateEditText;
    private Button savePromotionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        promoCodeEditText = findViewById(R.id.promoCodeEditText);
        discountPercentageEditText = findViewById(R.id.discountPercentageEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        savePromotionButton = findViewById(R.id.savePromotionButton);

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateEditText);
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateEditText);
            }
        });

        savePromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePromotion();
            }
        });
    }

    private void showDatePickerDialog(final EditText dateEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddPromotionActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        dateEditText.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void savePromotion() {
        final String promoCode = promoCodeEditText.getText().toString().trim();
        final String discountPercentage = discountPercentageEditText.getText().toString().trim();
        final String startDate = startDateEditText.getText().toString().trim();
        final String endDate = endDateEditText.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.2/CityCycle%20Rentals/add_promotion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddPromotionActivity.this, "Promotion added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPromotionActivity.this, ManagePromotionsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPromotionActivity.this, "Failed to add promotion", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("promo_code", promoCode);
                params.put("discount_percentage", discountPercentage);
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}