package com.example.citycyclerentals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.adapters.PromotionsAdapter;
import com.example.citycyclerentals.models.Promotion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ManagePromotionsActivity extends AppCompatActivity {

    private RecyclerView promotionsRecyclerView;
    private Button addPromotionButton;
    private List<Promotion> promotionList;
    private PromotionsAdapter promotionsAdapter;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_promotions);


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        promotionsRecyclerView = findViewById(R.id.promotionsRecyclerView);
        addPromotionButton = findViewById(R.id.addPromotionButton);

        promotionList = new ArrayList<>();
        promotionsAdapter = new PromotionsAdapter(promotionList, this);

        promotionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        promotionsRecyclerView.setAdapter(promotionsAdapter);

        addPromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePromotionsActivity.this, AddPromotionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPromotions(); // Refresh data when the activity resumes
    }

    private void loadPromotions() {
        promotionList.clear(); // Clear the list before loading new data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.1.2/CityCycle%20Rentals/fetch_promotions.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray promotionsArray = new JSONArray(response);
                            for (int i = 0; i < promotionsArray.length(); i++) {
                                JSONObject promotionObject = promotionsArray.getJSONObject(i);
                                int promoId = promotionObject.getInt("promo_id");
                                String promoCode = promotionObject.getString("promo_code");
                                double discountPercentage = promotionObject.getDouble("discount_percentage");
                                String startDate = promotionObject.getString("start_date");
                                String endDate = promotionObject.getString("end_date");

                                Promotion promotion = new Promotion(promoId, promoCode, discountPercentage, startDate, endDate);
                                promotionList.add(promotion);
                            }

                            promotionsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}