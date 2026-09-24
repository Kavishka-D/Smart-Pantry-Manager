package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProfileSetupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private ImageView profileImage;
    private EditText nameInput;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        profileImage = findViewById(R.id.profileImage);
        nameInput = findViewById(R.id.nameInput);

        Button selectPhotoButton =
                findViewById(R.id.selectPhotoButton);

        Button continueButton =
                findViewById(R.id.continueButton);

        selectPhotoButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            startActivityForResult(intent, PICK_IMAGE);
        });

        continueButton.setOnClickListener(v -> {

            String name = nameInput.getText()
                    .toString()
                    .trim();

            if (name.isEmpty()) {

                nameInput.setError("Please enter your name");
                return;
            }

            SharedPreferences preferences =
                    getSharedPreferences(
                            "SmartPantryPreferences",
                            MODE_PRIVATE
                    );

            SharedPreferences.Editor editor =
                    preferences.edit();

            editor.putString("user_name", name);

            if (selectedImageUri != null) {
                editor.putString(
                        "profile_image",
                        selectedImageUri.toString()
                );
            }

            editor.putBoolean(
                    "profile_completed",
                    true
            );

            editor.apply();

            Toast.makeText(
                    ProfileSetupActivity.this,
                    "Profile saved!",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    ProfileSetupActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });
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

            selectedImageUri = data.getData();

            try {

                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                selectedImageUri
                        );

                profileImage.setImageBitmap(bitmap);

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