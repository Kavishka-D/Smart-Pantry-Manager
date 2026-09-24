package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.database.DatabaseHelper;

import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    private RecyclerView pantryRecyclerView;
    private TextView noItemsMessage;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private ArrayList<PantryItem> pantryItems;
    private PantryAdapter pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        pantryRecyclerView =
                findViewById(R.id.pantryRecyclerView);

        noItemsMessage =
                findViewById(R.id.noItemsMessage);

        Button addPantryButton =
                findViewById(R.id.addPantryButton);

        Button backToHomeButton =
                findViewById(R.id.backToHomeButton);

        databaseHelper =
                new DatabaseHelper(this);

        database =
                databaseHelper.getWritableDatabase();

        pantryItems =
                new ArrayList<>();

        pantryAdapter =
                new PantryAdapter(
                        this,
                        pantryItems
                );

        pantryRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryRecyclerView.setAdapter(
                pantryAdapter
        );

        addPantryButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        backToHomeButton.setOnClickListener(v -> {
            finish();
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database != null) {
            loadPantryItems();
        }
    }

    private void loadPantryItems() {

        pantryItems.clear();

        Cursor cursor =
                database.query(
                        "pantry",
                        new String[]{
                                "id",
                                "name",
                                "quantity",
                                "unit",
                                "expiry_date"
                        },
                        null,
                        null,
                        null,
                        null,
                        "name ASC"
                );

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            String name =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "name"
                            )
                    );

            double quantity =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "quantity"
                            )
                    );

            String unit =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            String expiryDate =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "expiry_date"
                            )
                    );

            PantryItem item =
                    new PantryItem(
                            id,
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            pantryItems.add(item);
        }

        cursor.close();

        pantryAdapter.notifyDataSetChanged();

        if (pantryItems.isEmpty()) {

            noItemsMessage.setVisibility(
                    TextView.VISIBLE
            );

        } else {

            noItemsMessage.setVisibility(
                    TextView.GONE
            );
        }
    }

    public void deleteIngredient(int ingredientId) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            int result =
                                    database.delete(
                                            "pantry",
                                            "id = ?",
                                            new String[]{
                                                    String.valueOf(
                                                            ingredientId
                                                    )
                                            }
                                    );

                            if (result > 0) {

                                Toast.makeText(
                                        this,
                                        "Ingredient deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadPantryItems();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to delete ingredient",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }
}