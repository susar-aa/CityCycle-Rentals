package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.citycyclerentals.BikeDetailActivity;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Bike;

import java.util.ArrayList;
import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeViewHolder> {

    private Context context;
    private List<Bike> bikeList;  // Original bike list
    private List<Bike> filteredBikeList;  // Filtered bike list for search
    private OnBikeClickListener onBikeClickListener;  // Listener for item click

    public BikeAdapter(Context context, List<Bike> bikeList, OnBikeClickListener onBikeClickListener) {
        this.context = context;
        this.bikeList = bikeList;
        this.filteredBikeList = new ArrayList<>(bikeList);  // Initialize filtered list with original bike list
        this.onBikeClickListener = onBikeClickListener;  // Initialize click listener
    }

    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each bike
        View itemView = LayoutInflater.from(context).inflate(R.layout.bike_item, parent, false);
        return new BikeViewHolder(itemView);  // Pass itemView to the BikeViewHolder constructor
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        Bike bike = filteredBikeList.get(position);  // Use the filtered list for displaying bikes

        // Bind the bike data to the views
        holder.bikeName.setText(bike.getName());
        holder.bikePrice.setText(String.format("Hourly Price: %.2f LKR", bike.getPriceHourly()));
        holder.bikeType.setText(bike.getType());
        holder.stationName.setText("Station: " + bike.getStationName());

        // Load bike image using Glide
        Glide.with(context)
                .load(bike.getImageUrl())
                .placeholder(R.drawable.error_placeholder_image)  // Placeholder while loading
                .error(R.drawable.error_placeholder_image)  // Error image if the URL fails
                .into(holder.bikeImage);

        // Set OnClickListener to handle item click
        holder.itemView.setOnClickListener(v -> {
            // Pass all bike details to the next screen using Intent
            onBikeClickListener.onBikeClick(bike);
        });
    }

    @Override
    public int getItemCount() {
        return filteredBikeList.size();  // Return the size of the filtered list
    }

    // ViewHolder class to hold the bike item views
    public static class BikeViewHolder extends RecyclerView.ViewHolder {

        TextView bikeName, bikeType, bikePrice, stationName;
        ImageView bikeImage;

        // Constructor to initialize the views
        public BikeViewHolder(View itemView) {
            super(itemView);

            // Initialize the views
            bikeName = itemView.findViewById(R.id.bikeName);
            bikePrice = itemView.findViewById(R.id.price_hourly);
            bikeType = itemView.findViewById(R.id.bikeType);
            stationName = itemView.findViewById(R.id.stationName);
            bikeImage = itemView.findViewById(R.id.bikeImage);
        }
    }

    // Method to update the bike list and notify the adapter
    public void updateList(List<Bike> newBikes) {
        this.filteredBikeList = new ArrayList<>(newBikes);  // Update the filtered list
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    // Method to filter bikes based on query (bike name, station, or type)
    public void filterBikes(String query) {
        List<Bike> filteredList = new ArrayList<>();

        // Loop through the bike list and filter based on query
        for (Bike bike : bikeList) {
            if (bike.getName().toLowerCase().contains(query.toLowerCase()) ||
                    bike.getStationName().toLowerCase().contains(query.toLowerCase()) ||
                    bike.getType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(bike);  // Add to filtered list if it matches
            }
        }

        // Update the filtered list in the adapter
        updateList(filteredList);
    }

    // Interface to handle bike item clicks
    public interface OnBikeClickListener {
        void onBikeClick(Bike bike);  // Method to be implemented in the activity that uses this adapter
    }
}
