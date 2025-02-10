package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.admin.EditPromotionActivity;
import com.example.citycyclerentals.models.Promotion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.PromotionViewHolder> {

    private List<Promotion> promotionList;
    private Context context;

    public PromotionsAdapter(List<Promotion> promotionList, Context context) {
        this.promotionList = promotionList;
        this.context = context;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        Promotion promotion = promotionList.get(position);
        holder.promoCodeTextView.setText(promotion.getPromoCode());
        holder.discountPercentageTextView.setText(String.valueOf(promotion.getDiscountPercentage()) + "%");
        holder.startDateTextView.setText(promotion.getStartDate());
        holder.endDateTextView.setText(promotion.getEndDate());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditPromotionActivity.class);
                intent.putExtra("promo_id", promotion.getPromoId());
                intent.putExtra("promo_code", promotion.getPromoCode());
                intent.putExtra("discount_percentage", promotion.getDiscountPercentage());
                intent.putExtra("start_date", promotion.getStartDate());
                intent.putExtra("end_date", promotion.getEndDate());
                context.startActivity(intent);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePromotion(promotion.getPromoId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {

        TextView promoCodeTextView, discountPercentageTextView, startDateTextView, endDateTextView;
        Button editButton, deleteButton;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            promoCodeTextView = itemView.findViewById(R.id.promoCodeTextView);
            discountPercentageTextView = itemView.findViewById(R.id.discountPercentageTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void deletePromotion(int promoId, int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.2/CityCycle%20Rentals/delete_promotion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Promotion deleted successfully", Toast.LENGTH_SHORT).show();
                        promotionList.remove(position);
                        notifyItemRemoved(position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to delete promotion", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("promo_id", String.valueOf(promoId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }
}