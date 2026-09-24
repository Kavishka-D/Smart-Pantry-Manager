package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private LinearLayout recipeList;
    private TextView recipeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recipeList =
                findViewById(R.id.recipeList);

        recipeMessage =
                findViewById(R.id.recipeMessage);

        Button backButton =
                findViewById(R.id.backButton);

        databaseHelper =
                new DatabaseHelper(this);

        database =
                databaseHelper.getReadableDatabase();

        backButton.setOnClickListener(v -> finish());

        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database != null) {
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes() {

        recipeList.removeAllViews();

        Map<String, PantryAmount> pantry =
                getPantryIngredients();

        Cursor recipeCursor =
                database.query(
                        "recipes",
                        new String[]{
                                "id",
                                "name"
                        },
                        null,
                        null,
                        null,
                        null,
                        "name ASC"
                );

        int recipeCount = 0;

        while (recipeCursor.moveToNext()) {

            int recipeId =
                    recipeCursor.getInt(
                            recipeCursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            String recipeName =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    "name"
                            )
                    );

            if (recipeCanBeMade(
                    recipeId,
                    pantry
            )) {

                addRecipeButton(
                        recipeId,
                        recipeName
                );

                recipeCount++;
            }
        }

        recipeCursor.close();

        if (recipeCount == 0) {

            recipeMessage.setText(
                    "No recipes can be made with your current pantry ingredients."
            );

        } else {

            recipeMessage.setText(
                    recipeCount
                            + " recipe(s) can be made with your pantry."
            );
        }
    }

    private Map<String, PantryAmount> getPantryIngredients() {

        Map<String, PantryAmount> pantry =
                new HashMap<>();

        Cursor cursor =
                database.query(
                        "pantry",
                        new String[]{
                                "name",
                                "quantity",
                                "unit"
                        },
                        null,
                        null,
                        null,
                        null,
                        null
                );

        while (cursor.moveToNext()) {

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

            String normalisedName =
                    normaliseIngredientName(name);

            double convertedQuantity =
                    convertToBaseUnit(
                            quantity,
                            unit
                    );

            String baseUnit =
                    getBaseUnit(unit);

            if (pantry.containsKey(normalisedName)) {

                PantryAmount existing =
                        pantry.get(normalisedName);

                if (existing != null
                        && existing.baseUnit.equals(baseUnit)) {

                    existing.quantity +=
                            convertedQuantity;
                }

            } else {

                pantry.put(
                        normalisedName,
                        new PantryAmount(
                                convertedQuantity,
                                baseUnit
                        )
                );
            }
        }

        cursor.close();

        return pantry;
    }

    private boolean recipeCanBeMade(
            int recipeId,
            Map<String, PantryAmount> pantry
    ) {

        Cursor ingredientCursor =
                database.query(
                        "recipe_ingredients",
                        new String[]{
                                "ingredient_name",
                                "quantity",
                                "unit"
                        },
                        "recipe_id = ?",
                        new String[]{
                                String.valueOf(recipeId)
                        },
                        null,
                        null,
                        null
                );

        while (ingredientCursor.moveToNext()) {

            String ingredientName =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

            double requiredQuantity =
                    ingredientCursor.getDouble(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "quantity"
                            )
                    );

            String requiredUnit =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            String normalisedName =
                    normaliseIngredientName(
                            ingredientName
                    );

            PantryAmount pantryAmount =
                    pantry.get(normalisedName);

            if (pantryAmount == null) {

                ingredientCursor.close();
                return false;
            }

            double requiredBaseQuantity =
                    convertToBaseUnit(
                            requiredQuantity,
                            requiredUnit
                    );

            String requiredBaseUnit =
                    getBaseUnit(requiredUnit);

            if (!pantryAmount.baseUnit.equals(
                    requiredBaseUnit
            )) {

                ingredientCursor.close();
                return false;
            }

            if (pantryAmount.quantity
                    < requiredBaseQuantity) {

                ingredientCursor.close();
                return false;
            }
        }

        ingredientCursor.close();

        return true;
    }

    private String normaliseIngredientName(
            String name
    ) {

        String normalised =
                name.trim()
                        .toLowerCase(Locale.ROOT);

        if (normalised.endsWith("ies")
                && normalised.length() > 3) {

            normalised =
                    normalised.substring(
                            0,
                            normalised.length() - 3
                    )
                            + "y";

        } else if (normalised.endsWith("es")
                && normalised.length() > 2) {

            normalised =
                    normalised.substring(
                            0,
                            normalised.length() - 2
                    );

        } else if (normalised.endsWith("s")
                && !normalised.endsWith("ss")
                && normalised.length() > 1) {

            normalised =
                    normalised.substring(
                            0,
                            normalised.length() - 1
                    );
        }

        return normalised;
    }

    private String getBaseUnit(String unit) {

        String normalised =
                unit.trim()
                        .toLowerCase(Locale.ROOT);

        switch (normalised) {

            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
                return "g";

            case "g":
            case "gram":
            case "grams":
                return "g";

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return "ml";

            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return "ml";

            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "tbsp";

            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return "tsp";

            case "cup":
            case "cups":
                return "cup";

            case "item":
            case "items":
            case "piece":
            case "pieces":
                return "item";

            case "slice":
            case "slices":
                return "slice";

            case "leaf":
            case "leaves":
                return "leaf";

            case "clove":
            case "cloves":
                return "clove";

            case "bun":
            case "buns":
                return "bun";

            default:
                return normalised;
        }
    }

    private double convertToBaseUnit(
            double quantity,
            String unit
    ) {

        String normalised =
                unit.trim()
                        .toLowerCase(Locale.ROOT);

        switch (normalised) {

            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;

            case "g":
            case "gram":
            case "grams":
                return quantity;

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return quantity * 1000;

            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return quantity;

            default:
                return quantity;
        }
    }

    private void addRecipeButton(
            int recipeId,
            String recipeName
    ) {

        Button recipeButton =
                new Button(this);

        recipeButton.setText(
                recipeName
        );

        recipeButton.setTextSize(18);

        recipeButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

            intent.putExtra(
                    "recipe_id",
                    recipeId
            );

            startActivity(intent);
        });

        recipeList.addView(
                recipeButton
        );
    }

    private static class PantryAmount {

        double quantity;
        String baseUnit;

        PantryAmount(
                double quantity,
                String baseUnit
        ) {
            this.quantity = quantity;
            this.baseUnit = baseUnit;
        }
    }
}