package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.database.DatabaseHelper;

import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText ingredientNameInput;
    private EditText ingredientQuantityInput;
    private EditText ingredientUnitInput;
    private EditText expiryDateInput;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_add_edit_ingredient);

        View statusBarBackground =
                findViewById(R.id.statusBarBackground);

        ViewCompat.setOnApplyWindowInsetsListener(
                statusBarBackground,
                (v, insets) -> {

                    int statusBarHeight =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.statusBars()
                            ).top;

                    v.getLayoutParams().height =
                            statusBarHeight + 20;

                    v.requestLayout();

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(
                statusBarBackground
        );

        ingredientNameInput =
                findViewById(R.id.ingredientNameInput);

        ingredientQuantityInput =
                findViewById(R.id.ingredientQuantityInput);

        ingredientUnitInput =
                findViewById(R.id.ingredientUnitInput);

        expiryDateInput =
                findViewById(R.id.expiryDateInput);

        TextView addEditTitle =
                findViewById(R.id.addEditTitle);

        Button saveIngredientButton =
                findViewById(R.id.saveIngredientButton);

        Button cancelButton =
                findViewById(R.id.cancelButton);

        databaseHelper =
                new DatabaseHelper(this);

        database =
                databaseHelper.getWritableDatabase();

        // Check whether this is an edit
        ingredientId =
                getIntent().getIntExtra(
                        "ingredient_id",
                        -1
                );

        if (ingredientId != -1) {

            addEditTitle.setText(
                    "Edit Pantry Item"
            );

            saveIngredientButton.setText(
                    "Update Ingredient"
            );

            loadIngredient();
        }

        // Expiry date picker
        expiryDateInput.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            int year =
                    calendar.get(Calendar.YEAR);

            int month =
                    calendar.get(Calendar.MONTH);

            int day =
                    calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            AddEditIngredientActivity.this,
                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                String date =
                                        selectedDay
                                                + "/"
                                                + (selectedMonth + 1)
                                                + "/"
                                                + selectedYear;

                                expiryDateInput.setText(
                                        date
                                );
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });

        saveIngredientButton.setOnClickListener(
                v -> saveIngredient()
        );

        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadIngredient() {

        android.database.Cursor cursor =
                database.query(
                        "pantry",
                        new String[]{
                                "name",
                                "quantity",
                                "unit",
                                "expiry_date"
                        },
                        "id = ?",
                        new String[]{
                                String.valueOf(ingredientId)
                        },
                        null,
                        null,
                        null
                );

        if (cursor.moveToFirst()) {

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

            ingredientNameInput.setText(
                    name
            );

            ingredientQuantityInput.setText(
                    String.valueOf(quantity)
            );

            ingredientUnitInput.setText(
                    unit
            );

            if (expiryDate != null) {

                expiryDateInput.setText(
                        expiryDate
                );
            }
        }

        cursor.close();
    }

    private void saveIngredient() {

        String name =
                ingredientNameInput.getText()
                        .toString()
                        .trim();

        String quantityText =
                ingredientQuantityInput.getText()
                        .toString()
                        .trim();

        String unit =
                ingredientUnitInput.getText()
                        .toString()
                        .trim();

        String expiryDate =
                expiryDateInput.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            ingredientNameInput.setError(
                    "Enter an ingredient name"
            );

            ingredientNameInput.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {

            ingredientQuantityInput.setError(
                    "Enter a quantity"
            );

            ingredientQuantityInput.requestFocus();
            return;
        }

        if (unit.isEmpty()) {

            ingredientUnitInput.setError(
                    "Enter a unit"
            );

            ingredientUnitInput.requestFocus();
            return;
        }

        String normalisedUnit =
                unit.toLowerCase().trim();

        if (!normalisedUnit.equals("g")
                && !normalisedUnit.equals("gram")
                && !normalisedUnit.equals("grams")
                && !normalisedUnit.equals("kg")
                && !normalisedUnit.equals("kilogram")
                && !normalisedUnit.equals("kilograms")
                && !normalisedUnit.equals("ml")
                && !normalisedUnit.equals("milliliter")
                && !normalisedUnit.equals("milliliters")
                && !normalisedUnit.equals("millilitre")
                && !normalisedUnit.equals("millilitres")
                && !normalisedUnit.equals("l")
                && !normalisedUnit.equals("liter")
                && !normalisedUnit.equals("liters")
                && !normalisedUnit.equals("litre")
                && !normalisedUnit.equals("litres")
                && !normalisedUnit.equals("cup")
                && !normalisedUnit.equals("cups")
                && !normalisedUnit.equals("tbsp")
                && !normalisedUnit.equals("tablespoon")
                && !normalisedUnit.equals("tablespoons")
                && !normalisedUnit.equals("tsp")
                && !normalisedUnit.equals("teaspoon")
                && !normalisedUnit.equals("teaspoons")) {

            ingredientUnitInput.setError(
                    "Enter a valid unit such as g, kg, ml, L, cup, tbsp or tsp"
            );

            ingredientUnitInput.requestFocus();
            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(
                            quantityText
                    );

        } catch (NumberFormatException e) {

            ingredientQuantityInput.setError(
                    "Enter a valid number"
            );

            ingredientQuantityInput.requestFocus();
            return;
        }

        if (quantity <= 0) {

            ingredientQuantityInput.setError(
                    "Quantity must be greater than 0"
            );

            ingredientQuantityInput.requestFocus();
            return;
        }

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        if (expiryDate.isEmpty()) {
            values.putNull("expiry_date");
        } else {
            values.put("expiry_date", expiryDate);
        }

        if (ingredientId == -1) {

            long result = database.insert(
                            "pantry",
                            null,
                            values
                    );

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Ingredient added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to add ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            int result =
                    database.update(
                            "pantry",
                            values,
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
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}