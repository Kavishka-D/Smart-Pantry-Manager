package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemUnit;

    private LinearLayout pantryList;
    private TextView emptyMessage;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        itemName = findViewById(R.id.itemName);
        itemQuantity = findViewById(R.id.itemQuantity);
        itemUnit = findViewById(R.id.itemUnit);

        Button addItemButton = findViewById(R.id.addItemButton);

        pantryList = findViewById(R.id.pantryList);
        emptyMessage = findViewById(R.id.emptyMessage);

        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        addItemButton.setOnClickListener(v -> addPantryItem());

        loadPantryItems();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void addPantryItem() {

        String name = itemName.getText().toString().trim();
        String quantityText = itemQuantity.getText().toString().trim();
        String unit = itemUnit.getText().toString().trim();

        if (name.isEmpty()) {
            itemName.setError("Enter an ingredient name");
            itemName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            itemQuantity.setError("Enter a quantity");
            itemQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            itemUnit.setError("Enter a unit");
            itemUnit.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            itemQuantity.setError("Enter a valid number");
            itemQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            itemQuantity.setError("Quantity must be greater than 0");
            itemQuantity.requestFocus();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.putNull("expiry_date");

        long result = database.insert("pantry", null, values);

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Item added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            itemName.setText("");
            itemQuantity.setText("");
            itemUnit.setText("");

            loadPantryItems();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add item",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void loadPantryItems() {

        pantryList.removeAllViews();

        android.database.Cursor cursor = database.query(
                "pantry",
                new String[]{"id", "name", "quantity", "unit"},
                null,
                null,
                null,
                null,
                "name ASC"
        );

        if (cursor.getCount() == 0) {

            emptyMessage.setVisibility(TextView.VISIBLE);

        } else {

            emptyMessage.setVisibility(TextView.GONE);

            while (cursor.moveToNext()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                TextView itemView = new TextView(this);

                itemView.setText(
                        name + " - " + quantity + " " + unit
                );

                itemView.setTextSize(18);
                itemView.setPadding(16, 16, 16, 16);

                pantryList.addView(itemView);
            }
        }

        cursor.close();
    }
}