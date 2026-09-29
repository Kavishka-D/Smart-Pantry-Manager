package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupDrawer(Toolbar toolbar) {

        drawerLayout =
                findViewById(R.id.drawerLayout);

        navigationView =
                findViewById(R.id.navigationView);

        if (toolbar != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                    toolbar,
                    (v, insets) -> {

                        v.setPadding(
                                0,
                                55,
                                0,
                                0
                        );

                        return insets;
                    }
            );

            setSupportActionBar(toolbar);

            toolbar.setTitleTextAppearance(
                    this,
                    R.style.ToolbarTitleSmall
            );
        }

        // Keep the drawer menu items at the bottom.
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

        navigationView.setNavigationItemSelectedListener(item -> {

            int itemId =
                    item.getItemId();

            if (itemId == R.id.nav_home) {

                if (!(this instanceof MainActivity)) {

                    Intent intent =
                            new Intent(
                                    this,
                                    MainActivity.class
                            );

                    startActivity(intent);
                }

            } else if (itemId == R.id.nav_pantry) {

                if (!(this instanceof PantryActivity)) {

                    Intent intent =
                            new Intent(
                                    this,
                                    PantryActivity.class
                            );

                    startActivity(intent);
                }

            } else if (itemId == R.id.nav_recipes) {

                if (!(this instanceof SuggestedRecipesActivity)) {

                    Intent intent =
                            new Intent(
                                    this,
                                    SuggestedRecipesActivity.class
                            );

                    startActivity(intent);
                }

            } else if (itemId == R.id.nav_settings) {

                if (!(this instanceof SettingsActivity)) {

                    Intent intent =
                            new Intent(
                                    this,
                                    SettingsActivity.class
                            );

                    startActivity(intent);
                }
            }

            drawerLayout.closeDrawer(
                    navigationView
            );

            return true;
        });
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