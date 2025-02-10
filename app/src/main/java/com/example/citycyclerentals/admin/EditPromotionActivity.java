package com.example.citycyclerentals.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class EditPromotionActivity extends AppCompatActivity {

    private EditText promoCodeEditText, discountPercentageEditText, startDateEditText, endDateEditText;
    private Button updatePromotionButton, deletePromotionButton;
    private int promoId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_promotions);


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        promoCodeEditText = findViewById(R.id.promoCodeEditText);
        discountPercentageEditText = findViewById(R.id.discountPercentageEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        updatePromotionButton = findViewById(R.id.updatePromotionButton);
        deletePromotionButton = findViewById(R.id.deletePromotionButton);

        Intent intent = getIntent();
        promoId = intent.getIntExtra("promo_id", -1);
        String promoCode = intent.getStringExtra("promo_code");
        double discountPercentage = intent.getDoubleExtra("discount_percentage", 0);
        String startDate = intent.getStringExtra("start_date");
        String endDate = intent.getStringExtra("end_date");

        promoCodeEditText.setText(promoCode);
        discountPercentageEditText.setText(String.valueOf(discountPercentage));
        startDateEditText.setText(startDate);
        endDateEditText.setText(endDate);

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

        updatePromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePromotion();
            }
        });

        deletePromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePromotion();
            }
        });
    }

    private void showDatePickerDialog(final EditText dateEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditPromotionActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        dateEditText.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updatePromotion() {
        final String promoCode = promoCodeEditText.getText().toString().trim();
        final String discountPercentage = discountPercentageEditText.getText().toString().trim();
        final String startDate = startDateEditText.getText().toString().trim();
        final String endDate = endDateEditText.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.2/CityCycle%20Rentals/update_promotion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditPromotionActivity.this, "Promotion updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPromotionActivity.this, ManagePromotionsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditPromotionActivity.this, "Failed to update promotion", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("promo_id", String.valueOf(promoId));
                params.put("promo_code", promoCode);
                params.put("discount_percentage", discountPercentage);
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void deletePromotion() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.2/CityCycle%20Rentals/delete_promotion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditPromotionActivity.this, "Promotion deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPromotionActivity.this, ManagePromotionsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditPromotionActivity.this, "Failed to delete promotion", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("promo_id", String.valueOf(promoId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}