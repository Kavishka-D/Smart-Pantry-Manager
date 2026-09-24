package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        Button addIngredientButton =
                findViewById(R.id.addIngredientButton);

        Button viewPantryButton =
                findViewById(R.id.viewPantryButton);

        Button viewRecipesButton =
                findViewById(R.id.viewRecipesButton);

        Button settingsButton =
                findViewById(R.id.settingsButton);

        // Open Add Ingredient screen
        addIngredientButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        // Open Pantry screen
        viewPantryButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );

            startActivity(intent);
        });

        // Open Suggested Recipes screen
        viewRecipesButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // Open Settings/Profile screen
        settingsButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
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
}