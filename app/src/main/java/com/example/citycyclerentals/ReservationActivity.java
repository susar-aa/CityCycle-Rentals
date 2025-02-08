package com.example.citycyclerentals;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReservationActivity extends BaseActivity {

    private EditText name, contactNumber, nic, promoCode, startDatetime, endDatetime;
    private Spinner paymentMethod, cardSelectionSpinner;
    private Button reserveButton;

    private Text select_payment;

    private Calendar calendar;
    private ArrayList<String> cardNumbersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize views
        paymentMethod = findViewById(R.id.payment_method);
        cardSelectionSpinner = findViewById(R.id.card_selection_spinner);
        TextView selectCardLabel = findViewById(R.id.selectCardLabel);
        name = findViewById(R.id.name);
        contactNumber = findViewById(R.id.contact_number);
        nic = findViewById(R.id.nic);
        promoCode = findViewById(R.id.promo_code);
        startDatetime = findViewById(R.id.start_datetime);
        endDatetime = findViewById(R.id.end_datetime);
        reserveButton = findViewById(R.id.reserve_button);

        // Initialize calendar instance
        calendar = Calendar.getInstance();

        // Initialize cardNumbers list
        cardNumbersList = new ArrayList<>();

        // Set up the paymentMethod spinner with an ArrayAdapter
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethod.setAdapter(paymentAdapter);

        // Set up the card selection spinner (for the Card option)
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cardNumbersList);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSelectionSpinner.setAdapter(cardAdapter);

        // Set click listener for start datetime field
        startDatetime.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReservationActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        // Show time picker
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                ReservationActivity.this,
                                (timeView, hourOfDay, minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    startDatetime.setText(String.format("%d-%d-%d %d:%d",
                                            year, month + 1, dayOfMonth, hourOfDay, minute));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Set click listener for end datetime field
        endDatetime.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReservationActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        // Show time picker
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                ReservationActivity.this,
                                (timeView, hourOfDay, minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    endDatetime.setText(String.format("%d-%d-%d %d:%d",
                                            year, month + 1, dayOfMonth, hourOfDay, minute));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Set up the Reserve button click listener
        reserveButton.setOnClickListener(v -> {
            String fullName = name.getText().toString().trim();
            String contact = contactNumber.getText().toString().trim();
            String userNic = nic.getText().toString().trim();
            String promo = promoCode.getText().toString().trim(); // Get promo code input
            String payment = paymentMethod.getSelectedItem() != null ? paymentMethod.getSelectedItem().toString() : "Not selected";
            String startDateTime = startDatetime.getText().toString().trim();
            String endDateTime = endDatetime.getText().toString().trim();

            if (fullName.isEmpty() || contact.isEmpty() || userNic.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()) {
                Toast.makeText(ReservationActivity.this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            String userId = sharedPreferences.getString("id", null);

            if (userId == null) {
                Toast.makeText(ReservationActivity.this, "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences bikePreferences = getSharedPreferences("BikeDetails", MODE_PRIVATE);
            int bikeId = bikePreferences.getInt("bike_id", -1);

            if (bikeId == -1) {
                Toast.makeText(ReservationActivity.this, "Error: Bike not selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Duration calculation based on startDateTime and endDateTime
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date startDate = sdf.parse(startDateTime);
                Date endDate = sdf.parse(endDateTime);

                if (startDate != null && endDate != null) {
                    long diffInMillis = endDate.getTime() - startDate.getTime();
                    long diffInHours = diffInMillis / (1000 * 60 * 60); // Calculate hours difference

                    String url = "http://192.168.1.2/CityCycle%20Rentals/insert_reservation.php";  // Replace with your server URL

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                        Log.d("ReservationResponse", "Server Response: " + response);
                        Toast.makeText(ReservationActivity.this, response, Toast.LENGTH_SHORT).show();
                    }, error -> {
                        Log.e("ReservationError", "Error: " + error.getMessage());
                        Toast.makeText(ReservationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", userId);
                            params.put("name", fullName);
                            params.put("contact_number", contact);
                            params.put("nic", userNic);
                            params.put("start_date", startDateTime);
                            params.put("end_date", endDateTime);
                            params.put("payment_method", payment);
                            params.put("promo_code", promo);
                            params.put("bike_id", String.valueOf(bikeId));
                            params.put("duration", String.valueOf(diffInHours)); // Send calculated duration
                            return params;
                        }
                    };

                    Volley.newRequestQueue(ReservationActivity.this).add(stringRequest);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });








        // Set up the item selected listener for the payment method spinner
        paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedPaymentMethod = parentView.getItemAtPosition(position).toString();
                if ("Card".equals(selectedPaymentMethod)) {
                    cardSelectionSpinner.setVisibility(View.VISIBLE);
                    selectCardLabel.setVisibility(View.VISIBLE);
                    fetchUserCards();  // Fetch cards for the logged-in user
                } else {
                    cardSelectionSpinner.setVisibility(View.GONE);  // Hide card selection spinner
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optionally, handle nothing selected if needed
            }
        });
    }

    // Function to fetch user's saved cards from the database
    private void fetchUserCards() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", null);

        if (userId != null) {
            String url = "http://192.168.1.2/CityCycle%20Rentals/get_user_cards.php";  // Replace with your server URL

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                try {
                    JSONArray cardsArray = new JSONArray(response);
                    cardNumbersList.clear();  // Clear the current list
                    for (int i = 0; i < cardsArray.length(); i++) {
                        JSONObject card = cardsArray.getJSONObject(i);
                        String cardNumber = card.getString("card_number");
                        cardNumbersList.add(cardNumber);  // Add card number to list
                    }

                    // Notify the adapter that data has changed
                    ((ArrayAdapter) cardSelectionSpinner.getAdapter()).notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReservationActivity.this, "Error fetching cards", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Log.e("FetchCardsError", "Error: " + error.getMessage());
                Toast.makeText(ReservationActivity.this, "Error fetching cards", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", userId);  // Pass the user ID
                    return params;
                }
            };

            Volley.newRequestQueue(ReservationActivity.this).add(stringRequest);
        }
    }
}
