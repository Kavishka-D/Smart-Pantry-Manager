package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences =
                getSharedPreferences(
                        "SmartPantryPreferences",
                        MODE_PRIVATE
                );

        boolean profileCompleted =
                preferences.getBoolean(
                        "profile_completed",
                        false
                );

        if (!profileCompleted) {

            Intent intent = new Intent(
                    MainActivity.this,
                    ProfileSetupActivity.class
            );

            startActivity(intent);
            finish();

            return;
        }

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar =
                findViewById(R.id.mainToolbar);

        setupDrawer(toolbar);

        // Welcome message
        TextView welcomeMessage =
                findViewById(R.id.welcomeMessage);

        ImageView homeProfileImage =
                findViewById(R.id.homeProfileImage);

        String userName =
                preferences.getString(
                        "user_name",
                        "User"
                );

        welcomeMessage.setText(
                "Welcome, " + userName + "!"
        );

        // Profile picture
        String profileImage =
                preferences.getString(
                        "profile_image",
                        ""
                );

        if (!profileImage.isEmpty()) {

            try {

                homeProfileImage.setImageURI(
                        Uri.parse(profileImage)
                );

            } catch (Exception e) {

                homeProfileImage.setImageResource(
                        R.drawable.ic_launcher_foreground
                );
            }
        }

        // Home buttons
        Button addIngredientButton =
                findViewById(R.id.addIngredientButton);

        Button viewPantryButton =
                findViewById(R.id.viewPantryButton);

        Button viewRecipesButton =
                findViewById(R.id.viewRecipesButton);

        Button settingsButton =
                findViewById(R.id.settingsButton);

        // Add Ingredient
        addIngredientButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        // View Pantry
        viewPantryButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );

            startActivity(intent);
        });

        // View Suggested Recipes
        viewRecipesButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // Settings & Profile
        settingsButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        // Handle system window insets for main content
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            0,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }
}