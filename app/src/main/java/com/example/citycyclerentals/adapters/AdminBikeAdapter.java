package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Bike;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminBikeAdapter extends RecyclerView.Adapter<AdminBikeAdapter.BikeViewHolder> {

    private static final String TAG = "AdminBikeAdapter";
    private Context context;
    private List<Bike> bikeList;
    private OnBikeClickListener onBikeClickListener;

    public AdminBikeAdapter(Context context, List<Bike> bikeList, OnBikeClickListener onBikeClickListener) {
        this.context = context;
        this.bikeList = bikeList;
        this.onBikeClickListener = onBikeClickListener;
        Log.d(TAG, "AdminBikeAdapter: created with " + bikeList.size() + " bikes");
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bike, parent, false);
        Log.d(TAG, "onCreateViewHolder: view created");
        return new BikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        Bike bike = bikeList.get(position);
        holder.nameTextView.setText(bike.getName());
        holder.typeTextView.setText("Type: " + bike.getType());
        holder.stationNameTextView.setText("Location: " + bike.getStationName());
        holder.hourlyPriceTextView.setText("Hourly Price: Rs:" + bike.getPriceHourly());
        holder.dailyPriceTextView.setText("Daily Price: Rs:" + bike.getPriceDaily());
        holder.monthlyPriceTextView.setText("Monthly Price: Rs:" + bike.getPriceMonthly());
        holder.statusTextView.setText("Status: " + (bike.isAvailable() ? "Available" : "Not Available"));

        Picasso.get().load(bike.getImageUrl()).into(holder.bikeImageView);
        Log.d(TAG, "onBindViewHolder: bound bike " + bike.getName());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBikeClickListener.onBikeClick(bike);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBikeClickListener.onBikeDelete(bike);
            }
        });

        holder.availabilitySpinner.setSelection(bike.isAvailable() ? 0 : 1);
        holder.availabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isAvailable = (position == 0);
                if (isAvailable != bike.isAvailable()) {
                    onBikeClickListener.onBikeAvailabilityChange(bike, isAvailable);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + bikeList.size());
        return bikeList.size();
    }

    public interface OnBikeClickListener {
        void onBikeClick(Bike bike);

        void onBikeDelete(Bike bike);

        void onBikeAvailabilityChange(Bike bike, boolean isAvailable);
    }

    static class BikeViewHolder extends RecyclerView.ViewHolder {

        ImageView bikeImageView;
        TextView nameTextView, typeTextView, stationNameTextView, hourlyPriceTextView, dailyPriceTextView, monthlyPriceTextView, statusTextView;
        TextView editButton, deleteButton;
        Spinner availabilitySpinner;

        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);
            bikeImageView = itemView.findViewById(R.id.bike_image);
            nameTextView = itemView.findViewById(R.id.bike_name);
            typeTextView = itemView.findViewById(R.id.bike_type);
            stationNameTextView = itemView.findViewById(R.id.bike_station_name);
            hourlyPriceTextView = itemView.findViewById(R.id.bike_hourly_price);
            dailyPriceTextView = itemView.findViewById(R.id.bike_daily_price);
            monthlyPriceTextView = itemView.findViewById(R.id.bike_monthly_price);
            statusTextView = itemView.findViewById(R.id.bike_status);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            availabilitySpinner = itemView.findViewById(R.id.availability_spinner);
            Log.d(TAG, "BikeViewHolder: view holder created");
        }
    }
}