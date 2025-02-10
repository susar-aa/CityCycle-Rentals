package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.citycyclerentals.models.Reservation;
import com.example.citycyclerentals.R;

import java.util.ArrayList;

public class DashboardReservationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Reservation> reservationList;

    public DashboardReservationAdapter(Context context, ArrayList<Reservation> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }

    @Override
    public int getCount() {
        return reservationList.size();
    }

    @Override
    public Object getItem(int position) {
        return reservationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.confirmed_reservation_item, parent, false);
        }

        Reservation reservation = reservationList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.nameText);
        nameTextView.setText(reservation.getName());



        TextView totalPriceTextView = convertView.findViewById(R.id.totalPriceTextView);
        totalPriceTextView.setText(String.format("Rs: %.2f", reservation.getTotalPrice()));

        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        statusTextView.setText(reservation.getStatus());

        // Add TextView for Bike Name
        TextView bikeNameTextView = convertView.findViewById(R.id.bikeNameTextView);
        bikeNameTextView.setText(reservation.getBikeName()); // Set bike name

        ImageView bikeImageView = convertView.findViewById(R.id.bikeImageView);

        String imageUrl = reservation.getBikeImageUrl();
        Log.d("ReservationAdapter", "Bike Image URL: " + imageUrl);

        // Load the image using Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.error_placeholder_image) // Ensure this exists in drawable
                .error(R.drawable.error_placeholder_image) // Ensure this exists in drawable
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bikeImageView);

        return convertView;
    }



}
