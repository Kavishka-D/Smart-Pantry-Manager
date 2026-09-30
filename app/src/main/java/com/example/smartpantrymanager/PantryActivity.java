package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PantryActivity extends BaseDrawerActivity {

    private RecyclerView pantryRecyclerView;
    private TextView noItemsMessage;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private ArrayList<PantryItem> pantryItems;
    private PantryAdapter pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_pantry);

        // Toolbar
        Toolbar toolbar =
                findViewById(R.id.mainToolbar);

        setupDrawer(toolbar);

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

        Cursor cursor = database.query(
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
                            cursor.getColumnIndexOrThrow("id")
                    );

            String name =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );

            double quantity =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow("quantity")
                    );

            String unit =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")
                    );

            String expiryDate =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("expiry_date")
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

        checkExpiryAlerts();
    }

    private void checkExpiryAlerts() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "SmartPantryPreferences",
                        MODE_PRIVATE
                );

        boolean alertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts_enabled",
                        true
                );

        if (!alertsEnabled) {
            return;
        }

        Calendar today =
                Calendar.getInstance();

        Calendar sevenDaysFromNow =
                Calendar.getInstance();

        sevenDaysFromNow.add(
                Calendar.DAY_OF_YEAR,
                7
        );

        ArrayList<String> expiringItems =
                new ArrayList<>();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "d/M/yyyy",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        for (PantryItem item : pantryItems) {

            String expiryDate =
                    item.getExpiryDate();

            if (expiryDate == null
                    || expiryDate.trim().isEmpty()) {

                continue;
            }

            try {

                Date expiry =
                        dateFormat.parse(expiryDate);

                if (expiry == null) {
                    continue;
                }

                Calendar expiryCalendar =
                        Calendar.getInstance();

                expiryCalendar.setTime(expiry);

                if (!expiryCalendar.before(today)
                        && !expiryCalendar.after(
                        sevenDaysFromNow
                )) {

                    expiringItems.add(
                            item.getName()
                                    + " - "
                                    + expiryDate
                    );
                }

            } catch (ParseException e) {
                // Ignore invalid expiry dates.
            }
        }

        if (!expiringItems.isEmpty()) {

            StringBuilder message =
                    new StringBuilder();

            message.append(
                    "The following pantry items are expiring within 7 days:\n\n"
            );

            for (String item :
                    expiringItems) {

                message.append("• ")
                        .append(item)
                        .append("\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("Expiring Soon")
                    .setMessage(
                            message.toString()
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();
        }
    }

    public void deleteIngredient(int ingredientId) {

        new AlertDialog.Builder(this)
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