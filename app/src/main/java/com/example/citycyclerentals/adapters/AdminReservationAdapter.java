package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.ReservationAdmin;

import java.util.ArrayList;

public class AdminReservationAdapter extends ArrayAdapter<ReservationAdmin> {

    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnStatusChangeListener statusChangeListener;

    public AdminReservationAdapter(Context context, ArrayList<ReservationAdmin> reservations, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener, OnStatusChangeListener statusChangeListener) {
        super(context, 0, reservations);
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
        this.statusChangeListener = statusChangeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReservationAdmin reservation = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_item_reservation, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvcontactnumber = convertView.findViewById(R.id.tvcontactnumber);
        TextView tvStartDate = convertView.findViewById(R.id.tvStartDate);
        TextView tvEndDate = convertView.findViewById(R.id.tvEndDate);
        TextView tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);
        Spinner spStatus = convertView.findViewById(R.id.spStatus);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        tvName.setText(reservation.getName());
        tvStartDate.setText(reservation.getStartDate());
        tvEndDate.setText(reservation.getEndDate());
        tvTotalPrice.setText(String.valueOf(reservation.getTotalPrice()));

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);
        spStatus.setSelection(statusAdapter.getPosition(reservation.getStatus()));

        btnEdit.setOnClickListener(v -> editClickListener.onEditClick(reservation));
        btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(reservation));
        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newStatus = (String) parent.getItemAtPosition(position);
                statusChangeListener.onStatusChange(reservation, newStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return convertView;
    }

    public interface OnEditClickListener {
        void onEditClick(ReservationAdmin reservation);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ReservationAdmin reservation);
    }

    public interface OnStatusChangeListener {
        void onStatusChange(ReservationAdmin reservation, String newStatus);
    }
}