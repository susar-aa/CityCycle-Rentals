package com.example.citycyclerentals;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

public class ManageReservationsActivity extends AppCompatActivity implements AdminReservationAdapter.OnEditClickListener, AdminReservationAdapter.OnDeleteClickListener, AdminReservationAdapter.OnStatusChangeListener {

    private ListView listView;
    private ArrayList<ReservationAdmin> reservationsList;
    private AdminReservationAdapter adapter;
    private int selectedReservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reservations);

        listView = findViewById(R.id.listView);
        reservationsList = new ArrayList<>();
        adapter = new AdminReservationAdapter(this, reservationsList, this, this, this);
        listView.setAdapter(adapter);

        fetchReservations();
    }

    private void fetchReservations() {
        new FetchReservationsTask().execute();
    }

    @Override
    public void onEditClick(ReservationAdmin reservation) {
        selectedReservationId = reservation.getReservationId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditReservationDialogFragment editDialog = EditReservationDialogFragment.newInstance(reservation);
        editDialog.setOnSaveListener(updatedReservation -> new UpdateReservationTask().execute(
                updatedReservation.getName(), updatedReservation.getContactNumber(),
                updatedReservation.getNic(), updatedReservation.getStartDate(),
                updatedReservation.getEndDate(), String.valueOf(updatedReservation.getTotalPrice()),
                String.valueOf(updatedReservation.getDiscount()), updatedReservation.getStatus(),
                updatedReservation.getPaymentMethod()));
        editDialog.show(fragmentManager, "edit_reservation");
    }

    @Override
    public void onDeleteClick(ReservationAdmin reservation) {
        selectedReservationId = reservation.getReservationId();
        new DeleteReservationTask().execute(String.valueOf(selectedReservationId));
    }

    @Override
    public void onStatusChange(ReservationAdmin reservation, String newStatus) {
        selectedReservationId = reservation.getReservationId();
        new UpdateReservationTask().execute(reservation.getName(), reservation.getContactNumber(),
                reservation.getNic(), reservation.getStartDate(), reservation.getEndDate(),
                String.valueOf(reservation.getTotalPrice()), String.valueOf(reservation.getDiscount()),
                newStatus, reservation.getPaymentMethod());
    }

    private class FetchReservationsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.1.2/get_pending_reservations.php");
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
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ManageReservationsActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ManageReservationsActivity.this, "Error fetching reservations", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateReservationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://192.168.1.2/update_reservation.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes("reservation_id=" + selectedReservationId + "&name=" + params[0] +
                        "&contact_number=" + params[1] + "&nic=" + params[2] +
                        "&start_date=" + params[3] + "&end_date=" + params[4] +
                        "&total_price=" + params[5] + "&discount=" + params[6] +
                        "&status=" + params[7] + "&payment_method=" + params[8]);
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

            if (result != null) {
                Toast.makeText(ManageReservationsActivity.this, result, Toast.LENGTH_SHORT).show();
                fetchReservations();
            } else {
                Toast.makeText(ManageReservationsActivity.this, "Error updating reservation", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteReservationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://192.168.1.2/delete_reservation.php");
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

            if (result != null) {
                Toast.makeText(ManageReservationsActivity.this, result, Toast.LENGTH_SHORT).show();
                fetchReservations();
            } else {
                Toast.makeText(ManageReservationsActivity.this, "Error deleting reservation", Toast.LENGTH_SHORT).show();
            }
        }
    }
}