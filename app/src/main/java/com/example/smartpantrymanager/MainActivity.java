package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

        long result = database.insert(
                "pantry",
                null,
                values
        );

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

        Cursor cursor = database.query(
                "pantry",
                new String[]{
                        "id",
                        "name",
                        "quantity",
                        "unit"
                },
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

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                LinearLayout itemLayout =
                        new LinearLayout(this);

                itemLayout.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                itemLayout.setPadding(
                        16,
                        16,
                        16,
                        16
                );

                TextView itemView =
                        new TextView(this);

                itemView.setText(
                        name + " - " + quantity + " " + unit
                );

                itemView.setTextSize(18);

                LinearLayout.LayoutParams textParams =
                        new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );

                itemView.setLayoutParams(textParams);

                Button editButton =
                        new Button(this);

                editButton.setText("Edit");

                editButton.setOnClickListener(
                        v -> showEditDialog(
                                id,
                                name,
                                quantity,
                                unit
                        )
                );

                Button deleteButton =
                        new Button(this);

                deleteButton.setText("Delete");

                deleteButton.setOnClickListener(
                        v -> showDeleteConfirmation(
                                id,
                                name
                        )
                );

                itemLayout.addView(itemView);
                itemLayout.addView(editButton);
                itemLayout.addView(deleteButton);

                pantryList.addView(itemLayout);
            }
        }

        cursor.close();
    }

    private void showEditDialog(
            int id,
            String name,
            double quantity,
            String unit
    ) {

        LinearLayout editLayout =
                new LinearLayout(this);

        editLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        editLayout.setPadding(
                32,
                16,
                32,
                16
        );

        EditText editName =
                new EditText(this);

        editName.setHint("Ingredient name");
        editName.setText(name);

        EditText editQuantity =
                new EditText(this);

        editQuantity.setHint("Quantity");

        editQuantity.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        editQuantity.setText(
                String.valueOf(quantity)
        );

        EditText editUnit =
                new EditText(this);

        editUnit.setHint("Unit");
        editUnit.setText(unit);

        editLayout.addView(editName);
        editLayout.addView(editQuantity);
        editLayout.addView(editUnit);

        new AlertDialog.Builder(this)
                .setTitle("Edit Pantry Item")
                .setView(editLayout)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String newName =
                                    editName.getText()
                                            .toString()
                                            .trim();

                            String newQuantityText =
                                    editQuantity.getText()
                                            .toString()
                                            .trim();

                            String newUnit =
                                    editUnit.getText()
                                            .toString()
                                            .trim();

                            if (newName.isEmpty()
                                    || newQuantityText.isEmpty()
                                    || newUnit.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Please complete all fields",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            double newQuantity;

                            try {

                                newQuantity =
                                        Double.parseDouble(
                                                newQuantityText
                                        );

                            } catch (
                                    NumberFormatException e
                            ) {

                                Toast.makeText(
                                        this,
                                        "Enter a valid quantity",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (newQuantity <= 0) {

                                Toast.makeText(
                                        this,
                                        "Quantity must be greater than 0",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            ContentValues values =
                                    new ContentValues();

                            values.put(
                                    "name",
                                    newName
                            );

                            values.put(
                                    "quantity",
                                    newQuantity
                            );

                            values.put(
                                    "unit",
                                    newUnit
                            );

                            database.update(
                                    "pantry",
                                    values,
                                    "id = ?",
                                    new String[]{
                                            String.valueOf(id)
                                    }
                            );

                            Toast.makeText(
                                    this,
                                    "Item updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadPantryItems();
                        }
                )
                .show();
    }

    private void showDeleteConfirmation(
            int id,
            String name
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage(
                        "Are you sure you want to delete "
                                + name
                                + "?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            database.delete(
                                    "pantry",
                                    "id = ?",
                                    new String[]{
                                            String.valueOf(id)
                                    }
                            );

                            Toast.makeText(
                                    this,
                                    "Item deleted",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadPantryItems();
                        }
                )
                .show();
    }
}