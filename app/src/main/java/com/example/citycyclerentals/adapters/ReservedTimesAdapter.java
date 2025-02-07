package com.example.citycyclerentals.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.ReservedTime;

import java.util.List;

public class ReservedTimesAdapter extends RecyclerView.Adapter<ReservedTimesAdapter.ReservedTimeViewHolder> {

    private List<ReservedTime> reservedTimes;

    public ReservedTimesAdapter(List<ReservedTime> reservedTimes) {
        this.reservedTimes = reservedTimes;
    }

    @Override
    public ReservedTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserved_time, parent, false);
        return new ReservedTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservedTimeViewHolder holder, int position) {
        ReservedTime reservedTime = reservedTimes.get(position);
        holder.startDateTextView.setText("Start: " + reservedTime.getStartDate());
        holder.endDateTextView.setText("End: " + reservedTime.getEndDate());
    }

    @Override
    public int getItemCount() {
        return reservedTimes.size();
    }

    public static class ReservedTimeViewHolder extends RecyclerView.ViewHolder {

        TextView startDateTextView, endDateTextView;

        public ReservedTimeViewHolder(View itemView) {
            super(itemView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
        }
    }
}
