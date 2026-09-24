package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private LinearLayout recipeList;
    private TextView recipeMessage;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recipeList = findViewById(R.id.recipeList);
        recipeMessage = findViewById(R.id.recipeMessage);

        Button backButton = findViewById(R.id.backButton);

        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getReadableDatabase();

        backButton.setOnClickListener(v -> finish());

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        recipeList.removeAllViews();

        Cursor pantryCursor = database.query(
                "pantry",
                new String[]{"name", "quantity"},
                null,
                null,
                null,
                null,
                null
        );

        if (pantryCursor.getCount() == 0) {

            pantryCursor.close();

            recipeMessage.setText(
                    "Your pantry is empty. Add ingredients first."
            );

            return;
        }

        Cursor recipeCursor = database.query(
                "recipes",
                new String[]{"id", "name"},
                null,
                null,
                null,
                null,
                "name ASC"
        );

        int matchingRecipes = 0;

        while (recipeCursor.moveToNext()) {

            int recipeId = recipeCursor.getInt(
                    recipeCursor.getColumnIndexOrThrow("id")
            );

            String recipeName = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow("name")
            );

            if (recipeMatchesPantry(recipeId)) {

                matchingRecipes++;

                addRecipeButton(
                        recipeId,
                        recipeName
                );
            }
        }

        recipeCursor.close();
        pantryCursor.close();

        if (matchingRecipes == 0) {

            recipeMessage.setText(
                    "No recipes can be made with your current pantry ingredients."
            );

        } else {

            recipeMessage.setText(
                    "Select a recipe to view its ingredients and instructions."
            );
        }
    }

    private boolean recipeMatchesPantry(int recipeId) {

        Cursor ingredientCursor = database.query(
                "recipe_ingredients",
                new String[]{
                        "ingredient_name",
                        "quantity"
                },
                "recipe_id = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );

        boolean matches = true;

        while (ingredientCursor.moveToNext()) {

            String requiredIngredient =
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

            if (!pantryHasEnoughIngredient(
                    requiredIngredient,
                    requiredQuantity
            )) {

                matches = false;
                break;
            }
        }

        ingredientCursor.close();

        return matches;
    }

    private boolean pantryHasEnoughIngredient(
            String requiredIngredient,
            double requiredQuantity
    ) {

        Cursor pantryCursor = database.query(
                "pantry",
                new String[]{"quantity"},
                "LOWER(name) = ?",
                new String[]{
                        requiredIngredient.toLowerCase()
                },
                null,
                null,
                null
        );

        if (!pantryCursor.moveToFirst()) {

            pantryCursor.close();
            return false;
        }

        double pantryQuantity =
                pantryCursor.getDouble(
                        pantryCursor.getColumnIndexOrThrow(
                                "quantity"
                        )
                );

        pantryCursor.close();

        return pantryQuantity >= requiredQuantity;
    }

    private void addRecipeButton(
            int recipeId,
            String recipeName
    ) {

        Button recipeButton = new Button(this);

        recipeButton.setText(recipeName);
        recipeButton.setTextSize(18);
        recipeButton.setAllCaps(false);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
                0,
                8
        );

        recipeButton.setLayoutParams(params);

        recipeButton.setOnClickListener(
                v -> showRecipeDetails(
                        recipeId,
                        recipeName
                )
        );

        recipeList.addView(recipeButton);
    }

    private void showRecipeDetails(
            int recipeId,
            String recipeName
    ) {

        recipeList.removeAllViews();

        recipeMessage.setText(recipeName);

        TextView ingredientsTitle =
                new TextView(this);

        ingredientsTitle.setText("Ingredients");
        ingredientsTitle.setTextSize(22);
        ingredientsTitle.setTextAlignment(
                TextView.TEXT_ALIGNMENT_CENTER
        );
        ingredientsTitle.setPadding(
                0,
                16,
                0,
                12
        );

        recipeList.addView(ingredientsTitle);

        Cursor ingredientCursor = database.query(
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

            TextView ingredientView =
                    new TextView(this);

            ingredientView.setText(
                    "• " + ingredientName
                            + " - "
                            + quantity
                            + " "
                            + unit
            );

            ingredientView.setTextSize(18);

            ingredientView.setPadding(
                    8,
                    6,
                    8,
                    6
            );

            recipeList.addView(ingredientView);
        }

        ingredientCursor.close();

        TextView instructionsTitle =
                new TextView(this);

        instructionsTitle.setText("Instructions");
        instructionsTitle.setTextSize(22);
        instructionsTitle.setTextAlignment(
                TextView.TEXT_ALIGNMENT_CENTER
        );
        instructionsTitle.setPadding(
                0,
                24,
                0,
                12
        );

        recipeList.addView(instructionsTitle);

        Cursor recipeCursor = database.query(
                "recipes",
                new String[]{"steps"},
                "id = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );

        if (recipeCursor.moveToFirst()) {

            String steps =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    "steps"
                            )
                    );

            String[] individualSteps =
                    steps.split("(?<=[.!?])\\s+");

            for (int i = 0; i < individualSteps.length; i++) {

                String step =
                        individualSteps[i].trim();

                if (!step.isEmpty()) {

                    TextView instructionView =
                            new TextView(this);

                    instructionView.setText(
                            (i + 1) + ". " + step
                    );

                    instructionView.setTextSize(16);

                    instructionView.setPadding(
                            8,
                            8,
                            8,
                            8
                    );

                    recipeList.addView(
                            instructionView
                    );
                }
            }
        }

        recipeCursor.close();

        Button backToRecipesButton =
                new Button(this);

        backToRecipesButton.setText(
                "Back to Recipes"
        );

        backToRecipesButton.setOnClickListener(
                v -> loadSuggestedRecipes()
        );

        recipeList.addView(
                backToRecipesButton
        );
    }
}

