package com.example.citycyclerentals.adapters;// Inside your ReservationConfirmedAdapter class

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Reservation;

import org.json.JSONArray;

import java.util.List;

public class ReservationConfirmedAdapter extends ArrayAdapter<Reservation> {
    private Context context;
    private List<Reservation> reservationList;

    public ReservationConfirmedAdapter(Context context, List<Reservation> reservations) {
        super(context, 0, reservations);
        this.context = context;
        this.reservationList = reservations;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reservation_item, parent, false);
        }

        final Reservation reservation = reservationList.get(position);
        TextView reservationName = convertView.findViewById(R.id.nameTextView);
        TextView reservationStatus = convertView.findViewById(R.id.statusTextView);

        reservationName.setText(reservation.getName());
        reservationStatus.setText(reservation.getStatus());

        // Set OnClickListener for reservation item
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the PopupMenu based on reservation status
                showPopupMenu(v, reservation);
            }
        });

        return convertView;
    }

    private void showPopupMenu(View view, Reservation reservation) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        // Show options based on reservation status
        if ("pending".equalsIgnoreCase(reservation.getStatus())) {
            popupMenu.getMenu().add("Cancel Reservation");
        } else if ("confirmed".equalsIgnoreCase(reservation.getStatus())) {
            popupMenu.getMenu().add("End Reservation");
        }

        // Set click listener for the menu options
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if ("Cancel Reservation".equals(item.getTitle())) {
                    updateReservationStatus(reservation.getReservationId(), "canceled");
                } else if ("End Reservation".equals(item.getTitle())) {
                    updateReservationStatus(reservation.getReservationId(), "ended");
                }
                return true;
            }
        });

        popupMenu.show();
    }

    private void updateReservationStatus(int reservationId, String newStatus) {
        String url = "http://192.168.1.2/updateReservationStatus.php?reservationId=" + reservationId + "&status=" + newStatus;

        // Make a request to update the reservation status
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle response (e.g., update list and notify the adapter)
                        Toast.makeText(context, "Reservation status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Reservation", "Error updating status", error);
                        Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }
}
