package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        ViewCompat.setOnApplyWindowInsetsListener(
                toolbar,
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            0,
                            systemBars.top,
                            0,
                            0
                    );

                    return insets;
                }
        );

        setSupportActionBar(toolbar);

        // Navigation drawer
        drawerLayout =
                findViewById(R.id.drawerLayout);

        navigationView =
                findViewById(R.id.navigationView);

        // Move drawer menu items to the bottom
        navigationView.post(() -> {

            if (navigationView.getChildCount() > 0) {

                View menuView =
                        navigationView.getChildAt(
                                navigationView.getChildCount() - 1
                        );

                int menuHeight =
                        menuView.getMeasuredHeight();

                int availableHeight =
                        navigationView.getHeight();

                int topSpace =
                        availableHeight - menuHeight;

                if (topSpace > 0) {

                    menuView.setPadding(
                            menuView.getPaddingLeft(),
                            topSpace,
                            menuView.getPaddingRight(),
                            menuView.getPaddingBottom()
                    );
                }
            }
        });

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

        // Navigation drawer menu
        navigationView.setNavigationItemSelectedListener(item -> {

            int itemId =
                    item.getItemId();

            if (itemId == R.id.nav_home) {

                drawerLayout.closeDrawer(
                        navigationView
                );

                return true;
            }

            if (itemId == R.id.nav_pantry) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                PantryActivity.class
                        );

                startActivity(intent);

            } else if (itemId == R.id.nav_recipes) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                SuggestedRecipesActivity.class
                        );

                startActivity(intent);

            } else if (itemId == R.id.nav_settings) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                SettingsActivity.class
                        );

                startActivity(intent);
            }

            drawerLayout.closeDrawer(
                    navigationView
            );

            return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem menuItem =
                menu.add("Open Menu");

        menuItem.setIcon(
                R.drawable.ic_menu
        );

        menuItem.setShowAsAction(
                MenuItem.SHOW_AS_ACTION_ALWAYS
        );

        menuItem.setOnMenuItemClickListener(item -> {

            if (drawerLayout.isDrawerOpen(
                    navigationView
            )) {

                drawerLayout.closeDrawer(
                        navigationView
                );

            } else {

                drawerLayout.openDrawer(
                        navigationView
                );
            }

            return true;
        });

        return true;
    }
}