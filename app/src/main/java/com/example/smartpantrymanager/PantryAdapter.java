package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private ArrayList<PantryItem> pantryItems;

    public PantryAdapter(
            Context context,
            ArrayList<PantryItem> pantryItems
    ) {
        this.context = context;
        this.pantryItems = pantryItems;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.pantry_item,
                        parent,
                        false
                );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position
    ) {
        PantryItem item = pantryItems.get(position);

        holder.ingredientName.setText(
                item.getName()
        );

        holder.ingredientDetails.setText(
                item.getQuantity()
                        + " "
                        + item.getUnit()
                        + getExpiryText(item.getExpiryDate())
        );

        holder.editButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    AddEditIngredientActivity.class
            );

            intent.putExtra(
                    "ingredient_id",
                    item.getId()
            );

            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {

            if (context instanceof PantryActivity) {

                ((PantryActivity) context)
                        .deleteIngredient(item.getId());
            }
        });
    }

    private String getExpiryText(String expiryDate) {

        if (expiryDate == null
                || expiryDate.trim().isEmpty()) {

            return "";
        }

        return "\nExpiry: " + expiryDate;
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView ingredientName;
        TextView ingredientDetails;
        Button editButton;
        Button deleteButton;

        public PantryViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            ingredientName =
                    itemView.findViewById(
                            R.id.ingredientName
                    );

            ingredientDetails =
                    itemView.findViewById(
                            R.id.ingredientDetails
                    );

            editButton =
                    itemView.findViewById(
                            R.id.editButton
                    );

            deleteButton =
                    itemView.findViewById(
                            R.id.deleteButton
                    );
        }
    }
}