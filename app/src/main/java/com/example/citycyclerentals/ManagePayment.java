package com.example.citycyclerentals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycyclerentals.adapters.CardAdapter;
import com.example.citycyclerentals.models.Card;
import com.example.citycyclerentals.utils.FetchData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManagePayment extends AppCompatActivity {

    // UI Elements
    private RecyclerView recyclerCards;
    private CardAdapter cardAdapter;
    private ArrayList<Card> cardList;
    private Button btnAddCard;
    private ImageButton btnBack;

    // Constants for URL and SharedPreferences
    private static final String BASE_URL = "http://192.168.1.2/CityCycle%20Rentals"; // Base URL for API
    private static final String FETCH_CARDS_URL = BASE_URL + "fetch_cards.php"; // URL to fetch cards
    private static final String USER_PREFS = "UserDetails";
    private static final String USER_ID_KEY = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payment);

        Log.d("ManagePayment", "onCreate called");

        // Initialize UI elements
        btnBack = findViewById(R.id.btnBack);
        btnAddCard = findViewById(R.id.btnAddCard);
        recyclerCards = findViewById(R.id.recycler_view); // Correct RecyclerView ID for cards

        // Initialize Lists and Adapter
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(this, cardList);  // Use the context and the list

        // Set the LayoutManager for RecyclerView
        recyclerCards.setLayoutManager(new LinearLayoutManager(this));

        // Set Adapter for RecyclerView
        recyclerCards.setAdapter(cardAdapter);

        // Button Listeners
        btnBack.setOnClickListener(v -> finish());
        btnAddCard.setOnClickListener(v -> startActivity(new Intent(ManagePayment.this, AddCardActivity.class)));

        // Load Cards for the Logged-in User (testing with sample data for now)
        loadUserCards();
    }

    // Load user cards by fetching data from the server
    private void loadUserCards() {
        String userId = getUserId(); // Get user ID from SharedPreferences
        if (userId.isEmpty()) {
            showToast("User not logged in.");
            return;
        }

        // You can replace this URL with your server's URL for testing purposes
        String url = FETCH_CARDS_URL + "?user_id=" + userId;
        fetchData(url);
    }

    // Fetch data from the server and update the card list
    private void fetchData(String url) {
        new FetchData(url, response -> {
            // Check if the response is valid
            if (response == null || response.isEmpty()) {
                Log.e("ManagePayment", "Response is null or empty");
                showToast("Failed to fetch data.");
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() == 0) {
                    showToast("No cards found.");
                    return;
                }

                cardList.clear(); // Clear previous card data
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    cardList.add(new Card(
                            obj.getString("card_number"),
                            obj.getString("expiry_date"),
                            obj.getString("card_holder")
                    ));
                }
                cardAdapter.updateCardList(cardList); // Update the adapter with new card data
            } catch (Exception e) {
                Log.e("ManagePayment", "Error parsing data: ", e);
                showToast("Failed to parse response.");
            }
        }).execute();
    }

    // Get user ID from SharedPreferences
    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID_KEY, "");
    }

    // Show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
