package com.example.citycyclerentals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton; // Import AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView;
import com.example.citycyclerentals.R;
import com.example.citycyclerentals.models.Card;
import com.example.citycyclerentals.utils.FetchData;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private List<Card> cardList;

    // Constructor accepting both Context and List<Card>
    public CardAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.cardNumberTextView.setText(card.getCardNumber());
        holder.cardHolderTextView.setText(card.getCardHolder());

        // Set delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            deleteCardFromDatabase(card.getCardNumber(), position);  // Delete the card from the database and remove from list
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    // Method to update the card list in the adapter
    public void updateCardList(List<Card> newCardList) {
        this.cardList = newCardList;
        notifyDataSetChanged();  // Notify the adapter about the change
    }

    // ViewHolder class to hold the item view for each card
    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumberTextView;
        TextView cardHolderTextView;
        AppCompatImageButton btnDelete;  // Change to AppCompatImageButton

        public CardViewHolder(View itemView) {
            super(itemView);
            cardNumberTextView = itemView.findViewById(R.id.cardNumber);
            cardHolderTextView = itemView.findViewById(R.id.cardHolder);
            btnDelete = itemView.findViewById(R.id.deleteCardButton); // Assuming there's a delete button in card_item.xml
        }
    }

    // Method to delete a card from the database
    // Method to delete a card from the database
    private void deleteCardFromDatabase(String cardNumber, int position) {
        // Send the card number to the server for deletion
        String url = "http://192.168.1.2/delete_card.php?card_number=" + cardNumber;  // URL to delete card from the database
        new FetchData(url, response -> {
            if (response != null && response.equals("success")) {
                if (position >= 0 && position < cardList.size()) {  // Ensure the position is valid
                    // Remove the card from the list and notify the adapter
                    cardList.remove(position);
                    notifyItemRemoved(position);
                }
            } else {
                // Show an error message
                // You can use Toast or other methods to display the error
            }
        }).execute();
    }

}
