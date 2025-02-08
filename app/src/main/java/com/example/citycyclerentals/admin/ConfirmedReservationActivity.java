package com.example.citycyclerentals.admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.citycyclerentals.EditReservationDialogFragment;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.adapters.AdminReservationAdapter;
import com.example.citycyclerentals.models.ReservationAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ConfirmedReservationActivity extends AppCompatActivity implements AdminReservationAdapter.OnEditClickListener, AdminReservationAdapter.OnDeleteClickListener, AdminReservationAdapter.OnStatusChangeListener {

    private static final String TAG = "ConfirmedReservationActivity";

    private ListView listView;
    private SearchView searchView;
    private ArrayList<ReservationAdmin> reservationsList;
    private ArrayList<ReservationAdmin> filteredReservationsList;
    private AdminReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_reservations);

        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchView);
        reservationsList = new ArrayList<>();
        filteredReservationsList = new ArrayList<>();
        adapter = new AdminReservationAdapter(this, filteredReservationsList, this, this, this);
        listView.setAdapter(adapter);

        fetchReservations();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterReservations(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterReservations(newText);
                return true;
            }
        });
    }

    private void fetchReservations() {
        new FetchReservationsTask().execute();
    }

    private void filterReservations(String query) {
        filteredReservationsList.clear();
        for (ReservationAdmin reservation : reservationsList) {
            if (reservation.getName().toLowerCase().contains(query.toLowerCase()) ||
                    reservation.getNic().toLowerCase().contains(query.toLowerCase()) ||
                    reservation.getContactNumber().toLowerCase().contains(query.toLowerCase())) {
                filteredReservationsList.add(reservation);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(ReservationAdmin reservation) {
        Log.d(TAG, "onEditClick: Reservation ID " + reservation.getReservationId());
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditReservationDialogFragment editDialog = EditReservationDialogFragment.newInstance(reservation);
        editDialog.setOnSaveListener(updatedReservation -> new UpdateReservationTask().execute(
                updatedReservation.getReservationId(), updatedReservation.getName(), updatedReservation.getContactNumber(),
                updatedReservation.getNic(), updatedReservation.getStartDate(),
                updatedReservation.getEndDate(), String.valueOf(updatedReservation.getTotalPrice()),
                String.valueOf(updatedReservation.getDiscount()), updatedReservation.getStatus(),
                updatedReservation.getPaymentMethod()));
        editDialog.show(fragmentManager, "edit_reservation");
    }

    @Override
    public void onDeleteClick(ReservationAdmin reservation) {
        Log.d(TAG, "onDeleteClick: Reservation ID " + reservation.getReservationId());
        new DeleteReservationTask().execute(reservation.getReservationId());
    }

    @Override
    public void onStatusChange(ReservationAdmin reservation, String newStatus) {
        // Check if the new status is different from the current status
        if (!reservation.getStatus().equals(newStatus)) {
            Log.d(TAG, "onStatusChange: Reservation ID " + reservation.getReservationId() + ", New Status " + newStatus);
            new UpdateReservationTask().execute(
                    reservation.getReservationId(), reservation.getName(), reservation.getContactNumber(),
                    reservation.getNic(), reservation.getStartDate(), reservation.getEndDate(),
                    String.valueOf(reservation.getTotalPrice()), String.valueOf(reservation.getDiscount()),
                    newStatus, reservation.getPaymentMethod());
        } else {
            Log.d(TAG, "onStatusChange: No change in status for Reservation ID " + reservation.getReservationId());
        }
    }

    private class FetchReservationsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/confirmed_reservations.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    reservationsList.clear();
                    filteredReservationsList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int reservationId = jsonObject.getInt("reservation_id");
                        String name = jsonObject.getString("name");
                        String contactNumber = jsonObject.getString("contact_number");
                        String nic = jsonObject.getString("nic");
                        String startDate = jsonObject.getString("start_date");
                        String endDate = jsonObject.getString("end_date");
                        double totalPrice = jsonObject.getDouble("total_price");
                        double discount = jsonObject.getDouble("discount");
                        String status = jsonObject.getString("status");
                        String paymentMethod = jsonObject.getString("payment_method");
                        String bikeImageUrl = jsonObject.optString("bike_image_url", "");
                        String bikeName = jsonObject.optString("bike_name", "");
                        int bikeId = jsonObject.optInt("bike_id", 0);

                        ReservationAdmin reservation = new ReservationAdmin(reservationId, name, contactNumber, nic, startDate, endDate, totalPrice, discount, status, paymentMethod, bikeImageUrl, bikeName, bikeId);
                        reservationsList.add(reservation);
                        filteredReservationsList.add(reservation);
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ConfirmedReservationActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConfirmedReservationActivity.this, "Error fetching reservations", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateReservationTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            try {
                URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/update_reservation.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes("reservation_id=" + params[0] + "&name=" + params[1] +
                        "&contact_number=" + params[2] + "&nic=" + params[3] +
                        "&start_date=" + params[4] + "&end_date=" + params[5] +
                        "&total_price=" + params[6] + "&discount=" + params[7] +
                        "&status=" + params[8] + "&payment_method=" + params[9]);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "UpdateReservationTask result: " + result);
            if (result != null) {
                if (result.trim().equalsIgnoreCase("Reservation updated successfully")) {
                    Toast.makeText(ConfirmedReservationActivity.this, result, Toast.LENGTH_SHORT).show();
                    fetchReservations();
                } else {
                    Toast.makeText(ConfirmedReservationActivity.this, "Error updating reservation: " + result, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConfirmedReservationActivity.this, "Error updating reservation", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteReservationTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            try {
                URL url = new URL("http://192.168.1.2/CityCycle%20Rentals/delete_reservation.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes("reservation_id=" + params[0]);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "DeleteReservationTask result: " + result);
            if (result != null) {
                if (result.trim().equalsIgnoreCase("Reservation deleted successfully")) {
                    Toast.makeText(ConfirmedReservationActivity.this, result, Toast.LENGTH_SHORT).show();
                    fetchReservations();
                } else {
                    Toast.makeText(ConfirmedReservationActivity.this, "Error deleting reservation: " + result, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConfirmedReservationActivity.this, "Error deleting reservation", Toast.LENGTH_SHORT).show();
            }
        }
    }
}