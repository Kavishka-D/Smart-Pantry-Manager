package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Pantry ingredients table
        db.execSQL(
                "CREATE TABLE pantry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT" +
                        ")"
        );

        // Recipes table
        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "steps TEXT NOT NULL" +
                        ")"
        );

        // Recipe ingredients table
        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +
                        ")"
        );

        // Add the default recipes
        insertDefaultRecipes(db);
    }

    private void insertDefaultRecipes(SQLiteDatabase db) {

        addRecipe(
                db,
                "Pancakes",
                "Add the flour and sugar to a mixing bowl. Make a small well in the centre and add the eggs and milk. Whisk until the mixture is smooth and there are no large lumps. Heat a lightly greased frying pan over medium heat. Pour a small amount of batter into the pan and cook for 1–2 minutes until bubbles form. Flip the pancake and cook the other side until golden brown. Repeat with the remaining batter and serve.",
                new String[]{"flour", "milk", "egg", "sugar"},
                new double[]{2, 1, 2, 0.5},
                new String[]{"cups", "cups", "items", "cups"}
        );

        addRecipe(
                db,
                "Omelette",
                "Crack the eggs into a bowl and beat them until the yolks and whites are combined. Season the eggs and set aside. Heat a lightly greased frying pan over medium heat. Pour in the beaten eggs and allow them to cook for about 1 minute. Add the cheese and chopped tomato to one side of the omelette. Fold the omelette in half and cook for another 1–2 minutes until the egg is fully cooked and the cheese has melted. Serve immediately.",
                new String[]{"egg", "cheese", "tomato"},
                new double[]{3, 0.5, 1},
                new String[]{"items", "cups", "items"}
        );

        addRecipe(
                db,
                "Chicken Stir Fry",
                "Cut the chicken into small pieces and prepare the vegetables. Heat a little oil in a large frying pan over medium-high heat. Add the chicken and cook until lightly browned and cooked through. Add the carrot and pepper and stir fry for several minutes until the vegetables begin to soften. Add the soy sauce and mix everything together. Continue cooking for another 2–3 minutes, then remove from the heat and serve while hot.",
                new String[]{"chicken", "carrot", "pepper", "soy sauce"},
                new double[]{500, 2, 1, 2},
                new String[]{"g", "items", "items", "tbsp"}
        );

        addRecipe(
                db,
                "Tomato Pasta",
                "Bring a large pot of water to the boil and cook the pasta according to the package instructions. While the pasta cooks, chop the onion, garlic and tomatoes. Heat a little oil in a pan and cook the onion until soft. Add the garlic and tomatoes and cook for several minutes until the tomatoes soften and form a sauce. Drain the cooked pasta and add it to the sauce. Stir everything together and cook for another 1–2 minutes before serving.",
                new String[]{"pasta", "tomato", "onion", "garlic"},
                new double[]{250, 3, 1, 2},
                new String[]{"g", "items", "items", "cloves"}
        );

        addRecipe(
                db,
                "Grilled Cheese Sandwich",
                "Place the cheese evenly between two slices of bread. Spread butter on the outside of each slice. Heat a frying pan over medium heat and place the sandwich in the pan. Cook for 2–3 minutes until the bottom is golden brown. Carefully turn the sandwich over and cook the other side until golden and the cheese has melted. Remove from the pan, cut in half and serve warm.",
                new String[]{"bread", "cheese", "butter"},
                new double[]{2, 2, 1},
                new String[]{"slices", "slices", "tbsp"}
        );

        addRecipe(
                db,
                "Chicken Sandwich",
                "Season the chicken and cook it in a frying pan over medium heat until completely cooked through. Allow the chicken to rest for a few minutes and then slice it into smaller pieces. Place the bread slices on a clean surface and add the chicken. Add lettuce and sliced tomato on top of the chicken. Place the second slice of bread on top, cut the sandwich in half and serve.",
                new String[]{"chicken", "bread", "lettuce", "tomato"},
                new double[]{200, 2, 1, 1},
                new String[]{"g", "slices", "leaves", "items"}
        );

        addRecipe(
                db,
                "Vegetable Soup",
                "Wash and chop the carrot, potato, onion and tomato into small pieces. Heat a little oil in a large pot and cook the onion until soft. Add the carrot, potato and tomato and stir for a few minutes. Add enough water or stock to cover the vegetables. Bring the mixture to the boil, then reduce the heat and simmer until all the vegetables are tender. Season to taste and serve the soup while hot.",
                new String[]{"carrot", "potato", "onion", "tomato"},
                new double[]{2, 2, 1, 2},
                new String[]{"items", "items", "items", "items"}
        );

        addRecipe(
                db,
                "French Toast",
                "Crack the eggs into a bowl and whisk them together with the milk and sugar. Dip each slice of bread into the egg mixture, making sure both sides are coated without soaking the bread for too long. Heat a lightly greased frying pan over medium heat. Place the coated bread into the pan and cook for 2–3 minutes on each side until golden brown. Remove from the pan and serve warm.",
                new String[]{"bread", "egg", "milk", "sugar"},
                new double[]{4, 2, 0.5, 1},
                new String[]{"slices", "items", "cups", "tbsp"}
        );

        addRecipe(
                db,
                "Chicken Curry",
                "Cut the chicken into bite-sized pieces. Heat oil in a large pot and cook the chopped onion until soft. Add the chicken and cook until lightly browned. Add the chopped tomatoes and curry powder and stir well to coat the chicken. Pour in the coconut milk and bring the mixture to a gentle simmer. Cover and cook until the chicken is completely cooked and the sauce has thickened. Stir occasionally and serve hot.",
                new String[]{"chicken", "onion", "tomato", "curry powder", "coconut milk"},
                new double[]{500, 1, 2, 2, 1},
                new String[]{"g", "items", "items", "tbsp", "cups"}
        );

        addRecipe(
                db,
                "Egg Fried Rice",
                "Cook the rice and allow it to cool slightly. Beat the eggs in a bowl. Heat a little oil in a large frying pan and add the eggs. Stir the eggs until they are lightly scrambled, then remove them from the pan. Add the chopped carrot and cook until slightly softened. Add the cooked rice and soy sauce and stir fry everything together. Return the scrambled egg to the pan, mix well and cook for another 2 minutes before serving.",
                new String[]{"rice", "egg", "carrot", "soy sauce"},
                new double[]{2, 2, 1, 2},
                new String[]{"cups", "items", "items", "tbsp"}
        );

        addRecipe(
                db,
                "Beef Burgers",
                "Divide the beef into four equal portions and shape each portion into a burger patty. Heat a frying pan or grill over medium-high heat. Cook the patties for several minutes on each side until browned and cooked through. Lightly toast the burger buns if desired. Place lettuce and sliced tomato on the bottom half of each bun. Add the cooked beef patty and cover with the top half of the bun. Serve while hot.",
                new String[]{"beef", "bread", "lettuce", "tomato"},
                new double[]{500, 4, 4, 2},
                new String[]{"g", "buns", "leaves", "items"}
        );

        addRecipe(
                db,
                "Spaghetti Bolognese",
                "Bring a large pot of water to the boil and cook the spaghetti according to the package instructions. While the pasta cooks, heat oil in a large pan and cook the chopped onion until soft. Add the garlic and beef and cook until the beef is browned. Add the chopped tomatoes and stir well. Allow the sauce to simmer for 10–15 minutes so that the flavours develop. Drain the spaghetti and serve it with the prepared beef and tomato sauce.",
                new String[]{"spaghetti", "beef", "tomato", "onion", "garlic"},
                new double[]{250, 300, 3, 1, 2},
                new String[]{"g", "g", "items", "items", "cloves"}
        );

        addRecipe(
                db,
                "Chicken Wrap",
                "Cut the chicken into small pieces and season it. Heat a frying pan over medium heat and cook the chicken until browned and completely cooked. Warm the wraps briefly in a clean pan. Place the cooked chicken in the centre of each wrap and add lettuce, chopped tomato and cheese. Fold the sides of the wrap inward and roll it tightly from the bottom. Cut the wrap in half and serve.",
                new String[]{"chicken", "wrap", "lettuce", "tomato", "cheese"},
                new double[]{200, 2, 2, 1, 0.5},
                new String[]{"g", "items", "leaves", "items", "cups"}
        );

        addRecipe(
                db,
                "Potato Salad",
                "Wash and peel the potatoes, then cut them into small pieces. Place the potatoes in a pot of water and boil until they are tender but not falling apart. Drain the potatoes and allow them to cool. Chop the onion into small pieces and add it to the potatoes. Add the mayonnaise and gently mix everything together. Chill the potato salad before serving.",
                new String[]{"potato", "mayonnaise", "onion"},
                new double[]{500, 3, 1},
                new String[]{"g", "tbsp", "items"}
        );

        addRecipe(
                db,
                "Banana Smoothie",
                "Peel the bananas and cut them into smaller pieces. Place the bananas, milk and sugar into a blender. Blend the ingredients until the mixture becomes smooth and creamy. Taste the smoothie and add a little more sugar if needed. Pour into a glass and serve immediately while cold.",
                new String[]{"banana", "milk", "sugar"},
                new double[]{2, 1, 1},
                new String[]{"items", "cups", "tbsp"}
        );
    }

    private void addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String steps,
            String[] ingredientNames,
            double[] quantities,
            String[] units
    ) {

        ContentValues recipeValues = new ContentValues();

        recipeValues.put("name", recipeName);
        recipeValues.put("steps", steps);

        long recipeId = db.insert(
                "recipes",
                null,
                recipeValues
        );

        for (int i = 0; i < ingredientNames.length; i++) {

            ContentValues ingredientValues =
                    new ContentValues();

            ingredientValues.put(
                    "recipe_id",
                    recipeId
            );

            ingredientValues.put(
                    "ingredient_name",
                    ingredientNames[i]
            );

            ingredientValues.put(
                    "quantity",
                    quantities[i]
            );

            ingredientValues.put(
                    "unit",
                    units[i]
            );

            db.insert(
                    "recipe_ingredients",
                    null,
                    ingredientValues
            );
        }
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");

        // Keep the pantry table and existing pantry items.
        onCreateRecipes(db);
    }

    private void onCreateRecipes(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "steps TEXT NOT NULL" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +
                        ")"
        );

        insertDefaultRecipes(db);
    }
}