package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Station;

import java.util.List;

public class AdminStationAdapter extends RecyclerView.Adapter<AdminStationAdapter.StationViewHolder> {

    private Context context;
    private List<Station> stationList;
    private OnStationClickListener listener;

    public AdminStationAdapter(Context context, List<Station> stationList, OnStationClickListener listener) {
        this.context = context;
        this.stationList = stationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        Station station = stationList.get(position);
        holder.nameTextView.setText(station.getName());
        holder.coordinatesTextView.setText("Lat: " + station.getLatitude() + ", Lng: " + station.getLongitude());

        holder.editButton.setOnClickListener(v -> listener.onStationClick(station));
        holder.deleteButton.setOnClickListener(v -> listener.onStationDelete(station));
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    public interface OnStationClickListener {
        void onStationClick(Station station);
        void onStationDelete(Station station);
    }

    public static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView coordinatesTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.station_name);
            coordinatesTextView = itemView.findViewById(R.id.station_coordinates);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}