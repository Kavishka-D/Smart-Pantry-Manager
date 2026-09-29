package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends BaseDrawerActivity {

    private static final int PICK_IMAGE = 101;

    private ImageView profileImage;
    private EditText nameInput;
    private Switch expiryAlertSwitch;
    private Uri selectedImageUri;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        // Make the Android status bar sage green.
        window.setStatusBarColor(
                getColor(R.color.sage_dark)
        );

        setContentView(R.layout.activity_settings);

        // Toolbar
        Toolbar toolbar =
                findViewById(R.id.mainToolbar);

        setupDrawer(toolbar);

        toolbar.setTitleTextAppearance(
                this,
                R.style.ToolbarTitleSmall
        );

        setTitle("Smart Pantry Manager");

        // Find views
        profileImage =
                findViewById(R.id.settingsProfileImage);

        nameInput =
                findViewById(R.id.settingsNameInput);

        expiryAlertSwitch =
                findViewById(R.id.expiryAlertSwitch);

        Button changePhotoButton =
                findViewById(R.id.changePhotoButton);

        Button saveProfileButton =
                findViewById(R.id.saveProfileButton);

        Button backToHomeButton =
                findViewById(R.id.backToHomeButton);

        // Shared Preferences
        preferences =
                getSharedPreferences(
                        "SmartPantryPreferences",
                        MODE_PRIVATE
                );

        // Load saved name
        String savedName =
                preferences.getString(
                        "user_name",
                        ""
                );

        nameInput.setText(savedName);

        // Load expiry alert setting
        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts_enabled",
                        true
                );

        expiryAlertSwitch.setChecked(
                expiryAlertsEnabled
        );

        // Load saved profile picture
        String savedImage =
                preferences.getString(
                        "profile_image",
                        ""
                );

        if (!savedImage.isEmpty()) {

            try {

                profileImage.setImageURI(
                        Uri.parse(savedImage)
                );

            } catch (Exception e) {

                profileImage.setImageResource(
                        R.drawable.ic_launcher_foreground
                );
            }
        }

        // Change profile picture
        changePhotoButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    );

            startActivityForResult(
                    intent,
                    PICK_IMAGE
            );
        });

        // Save profile
        saveProfileButton.setOnClickListener(v -> {

            String name =
                    nameInput.getText()
                            .toString()
                            .trim();

            if (name.isEmpty()) {

                nameInput.setError(
                        "Please enter your name"
                );

                nameInput.requestFocus();

                return;
            }

            SharedPreferences.Editor editor =
                    preferences.edit();

            editor.putString(
                    "user_name",
                    name
            );

            editor.putBoolean(
                    "expiry_alerts_enabled",
                    expiryAlertSwitch.isChecked()
            );

            if (selectedImageUri != null) {

                String savedImagePath =
                        saveImageToInternalStorage(
                                selectedImageUri
                        );

                if (savedImagePath != null) {

                    editor.putString(
                            "profile_image",
                            savedImagePath
                    );
                }
            }

            editor.apply();

            Toast.makeText(
                    SettingsActivity.this,
                    "Settings saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            finish();
        });

        // Back to Home
        backToHomeButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            finish();
        });
    }

    private String saveImageToInternalStorage(
            Uri imageUri
    ) {

        try {

            Bitmap bitmap =
                    MediaStore.Images.Media.getBitmap(
                            getContentResolver(),
                            imageUri
                    );

            File file =
                    new File(
                            getFilesDir(),
                            "profile_picture.jpg"
                    );

            FileOutputStream outputStream =
                    new FileOutputStream(file);

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
            );

            outputStream.close();

            return file.getAbsolutePath();

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Unable to save profile picture",
                    Toast.LENGTH_SHORT
            ).show();

            return null;
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {

            selectedImageUri =
                    data.getData();

            try {

                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                selectedImageUri
                        );

                profileImage.setImageBitmap(
                        bitmap
                );

            } catch (IOException e) {

                Toast.makeText(
                        this,
                        "Unable to load image",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}