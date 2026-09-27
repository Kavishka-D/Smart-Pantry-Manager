package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private TextView recipeDetailTitle;
    private TextView recipeIngredients;
    private TextView recipeSteps;

    private int recipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        // Make the actual Android status bar transparent.
        // The sage view underneath it provides the green background.
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_recipe_detail);

        View statusBarBackground =
                findViewById(R.id.statusBarBackground);

        // Make the sage area the status-bar height
        // plus a little extra space.
        ViewCompat.setOnApplyWindowInsetsListener(
                statusBarBackground,
                (v, insets) -> {

                    int statusBarHeight =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.statusBars()
                            ).top;

                    v.getLayoutParams().height =
                            statusBarHeight + 15;

                    v.requestLayout();

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(
                statusBarBackground
        );

        recipeDetailTitle =
                findViewById(R.id.recipeDetailTitle);

        recipeIngredients =
                findViewById(R.id.recipeIngredients);

        recipeSteps =
                findViewById(R.id.recipeSteps);

        Button backToRecipesButton =
                findViewById(R.id.backToRecipesButton);

        databaseHelper =
                new DatabaseHelper(this);

        database =
                databaseHelper.getReadableDatabase();

        recipeId =
                getIntent().getIntExtra(
                        "recipe_id",
                        -1
                );

        if (recipeId != -1) {
            loadRecipe();
        }

        backToRecipesButton.setOnClickListener(
                v -> finish()
        );
    }

    private void loadRecipe() {

        Cursor recipeCursor =
                database.query(
                        "recipes",
                        new String[]{
                                "name",
                                "steps"
                        },
                        "id = ?",
                        new String[]{
                                String.valueOf(recipeId)
                        },
                        null,
                        null,
                        null
                );

        if (recipeCursor.moveToFirst()) {

            String recipeName =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    "name"
                            )
                    );

            String steps =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    "steps"
                            )
                    );

            recipeDetailTitle.setText(
                    recipeName
            );

            recipeSteps.setText(
                    formatSteps(steps)
            );
        }

        recipeCursor.close();

        loadIngredients();
    }

    private void loadIngredients() {

        StringBuilder ingredients =
                new StringBuilder();

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
                        "id ASC"
                );

        int number = 1;

        while (ingredientCursor.moveToNext()) {

            String name =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

            double quantity =
                    ingredientCursor.getDouble(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "quantity"
                            )
                    );

            String unit =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            ingredients.append(
                            number
                    )
                    .append(". ")
                    .append(name)
                    .append(" - ")
                    .append(formatQuantity(quantity))
                    .append(" ")
                    .append(unit)
                    .append("\n");

            number++;
        }

        ingredientCursor.close();

        recipeIngredients.setText(
                ingredients.toString()
        );
    }

    private String formatSteps(String steps) {

        if (steps == null
                || steps.trim().isEmpty()) {

            return "No method available.";
        }

        String[] stepArray =
                steps.split("\\.");

        StringBuilder formattedSteps =
                new StringBuilder();

        int number = 1;

        for (String step : stepArray) {

            String cleanStep =
                    step.trim();

            if (!cleanStep.isEmpty()) {

                formattedSteps
                        .append(number)
                        .append(". ")
                        .append(cleanStep)
                        .append("\n\n");

                number++;
            }
        }

        return formattedSteps.toString().trim();
    }

    private String formatQuantity(
            double quantity
    ) {

        if (quantity == (long) quantity) {

            return String.valueOf(
                    (long) quantity
            );
        }

        return String.valueOf(quantity);
    }
}