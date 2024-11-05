//package com.example.fitnessapp;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection
//    private FirebaseAuth auth;
//    private Button button;
//    private TextView textview;
//    private FirebaseUser user;
//    private Button calculateBmiButton, startExerciseButton, generateDietButton, calculateStepsButton, startWorkout;
//    private TextView graphPlaceholder; // Add a TextView reference for the graph placeholder
//    private SharedPreferences sharedPreferences;
//    private ImageView profileImageView; // Reference for the profile image view
//    private Uri imageUri; // URI for the selected image
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
//
//        // Initialize UI components
//        button = findViewById(R.id.logout);
//        textview = findViewById(R.id.user_details);
//        calculateBmiButton = findViewById(R.id.calculateBmiButton);
//        startExerciseButton = findViewById(R.id.startExerciseButton);
//        generateDietButton = findViewById(R.id.generateDietButton);
//        calculateStepsButton = findViewById(R.id.calculateStepsButton);
//        startWorkout = findViewById(R.id.startWorkoutButton);
//        graphPlaceholder = findViewById(R.id.graphPlaceholder);
//        profileImageView = findViewById(R.id.profileImage); // Initialize profile image view
//
//        // Initialize SharedPreferences
//        sharedPreferences = getSharedPreferences("FitnessAppPrefs", MODE_PRIVATE);
//
//        // Check if user is logged in
//        if (user == null) {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        } else {
//            textview.setText(user.getEmail());
//            displayAccountCreationInfo();
//            displayWorkoutCount();
//        }
//
//        // Set click listener for Calculate BMI button
//        calculateBmiButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, BmiCalculationActivity.class);
//            startActivity(intent);
//        });
//
//        // Set click listener for Start Exercise button with BMI check
//        startExerciseButton.setOnClickListener(v -> checkBmiAndStartExercise());
//
//        // Set click listener for Generate Diet button
//        generateDietButton.setOnClickListener(v -> checkBmiAndStartDiet());
//
//        // Set click listener for Calculate Steps button
//        calculateStepsButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, StepsActivity.class);
//            startActivity(intent);
//            finish();
//        });
//
//        // Set click listener for Start Workout button
//        startWorkout.setOnClickListener(v -> {
//            incrementWorkoutCount();
//            Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
//            startActivity(intent);
//        });
//
//        // Set click listener for Logout button
//        button.setOnClickListener(view -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        });
//
//        // Set click listener for Profile Image selection
//        profileImageView.setOnClickListener(v -> openGallery());
//    }
//
//    public void openAchievementsNews(View view) {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://news.google.com/search?cf=all&q=sport+india&hl=en-IN&gl=IN&ceid=IN:en"));
//        startActivity(browserIntent);
//    }
//
//    private void displayAccountCreationInfo() {
//        // Get the account creation date from Firebase User metadata
//        if (user != null) {
//            long creationTime = user.getMetadata().getCreationTimestamp(); // Timestamp in milliseconds
//            String accountCreationDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(creationTime));
//            long currentTime = System.currentTimeMillis();
//            long daysSinceCreation = (currentTime - creationTime) / (1000 * 60 * 60 * 24); // Convert milliseconds to days
//
//            graphPlaceholder.append("Account Creation Date: " + accountCreationDate + "\n");
//            graphPlaceholder.append("Days Since Account Creation: " + daysSinceCreation + " days\n");
//        }
//    }
//
//    private void displayWorkoutCount() {
//        int workoutCount = sharedPreferences.getInt(user.getUid() + "_workout_count", 0); // Get workout count
//        graphPlaceholder.append("Workouts Completed Today: " + workoutCount);
//    }
//
//    private void incrementWorkoutCount() {
//        int workoutCount = sharedPreferences.getInt(user.getUid() + "_workout_count", 0); // Get the current count
//        workoutCount++; // Increment the count
//
//        // Save the updated count back to SharedPreferences
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(user.getUid() + "_workout_count", workoutCount);
//        editor.apply();
//    }
//
//    private void checkBmiAndStartExercise() {
//        float bmi = sharedPreferences.getFloat(user.getUid() + "_bmi", -1); // Default to -1 if no data
//
//        if (bmi == -1) {
//            Toast.makeText(this, "Please calculate your BMI first!", Toast.LENGTH_SHORT).show();
//        } else {
//            Intent intent = new Intent(MainActivity.this, StartExerciseActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private void checkBmiAndStartDiet() {
//        float bmi = sharedPreferences.getFloat(user.getUid() + "_bmi", -1); // Default to -1 if no data
//
//        if (bmi == -1) {
//            Toast.makeText(this, "Please calculate your BMI first!", Toast.LENGTH_SHORT).show();
//        } else {
//            Intent intent = new Intent(MainActivity.this, GenerateDietActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            try {
//                // Set the selected image to the ImageView
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                profileImageView.setImageBitmap(bitmap);
//
//                // Upload the image to Firebase
//                uploadImageToFirebase(imageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images");
//    private void uploadImageToFirebase(Uri uri) {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images");
//        String fileName = System.currentTimeMillis() + ".jpg"; // Generate a unique filename
//
//        // Create a reference to the file to upload
//        StorageReference fileReference = storageReference.child(fileName);
//        fileReference.putFile(uri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Handle successful uploads
//                    Toast.makeText(MainActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
//                    // Get the download URL
//                    fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
//                        // Save the download URL to the database or your user profile
//                        // You can save it in the user's profile in your database here
//                        Log.d("MainActivity", "Download URL: " + downloadUri.toString());
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//    private void saveImageUrlToFirestore(String imageUrl) {
//        if (user != null) {
//            String userId = user.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            // Create a map to hold the image URL
//            Map<String, Object> userData = new HashMap<>();
//            userData.put("profileImageUrl", imageUrl);
//
//            // Update the user's document in Firestore
//            db.collection("users").document(userId)
//                    .update(userData)
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(MainActivity.this, "Image URL saved successfully", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(MainActivity.this, "Failed to save URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        }
//    }
//
//}
//

package com.example.fitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection
    private FirebaseAuth auth;
    private Button button;
    private TextView textview;
    private FirebaseUser user;
    private Button calculateBmiButton, startExerciseButton, generateDietButton, calculateStepsButton, startWorkout;
    private TextView graphPlaceholder; // Add a TextView reference for the graph placeholder
    private SharedPreferences sharedPreferences;
    private ImageView profileImageView; // Reference for the profile image view
    private Uri imageUri; // URI for the selected image
    private StorageReference storageReference; // Reference for Firebase Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Storage
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        user = auth.getCurrentUser();

        // Initialize UI components
        button = findViewById(R.id.logout);
        textview = findViewById(R.id.user_details);
        calculateBmiButton = findViewById(R.id.calculateBmiButton);
        startExerciseButton = findViewById(R.id.startExerciseButton);
        generateDietButton = findViewById(R.id.generateDietButton);
        calculateStepsButton = findViewById(R.id.calculateStepsButton);
        startWorkout = findViewById(R.id.startWorkoutButton);
        graphPlaceholder = findViewById(R.id.graphPlaceholder);
        profileImageView = findViewById(R.id.profileImage); // Initialize profile image view

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FitnessAppPrefs", MODE_PRIVATE);

        // Check if user is logged in
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textview.setText(user.getEmail());
            displayAccountCreationInfo();
            displayWorkoutCount();
        }

        // Set click listener for Calculate BMI button
        calculateBmiButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BmiCalculationActivity.class);
            startActivity(intent);
        });

        // Set click listener for Start Exercise button with BMI check
        startExerciseButton.setOnClickListener(v -> checkBmiAndStartExercise());

        // Set click listener for Generate Diet button
        generateDietButton.setOnClickListener(v -> checkBmiAndStartDiet());

        // Set click listener for Calculate Steps button
        calculateStepsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StepsActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for Start Workout button
        startWorkout.setOnClickListener(v -> {
            incrementWorkoutCount();
            Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
            startActivity(intent);
        });

        // Set click listener for Logout button
        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for Profile Image selection
        profileImageView.setOnClickListener(v -> openGallery());
    }

    public void openAchievementsNews(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://news.google.com/search?cf=all&q=sport+india&hl=en-IN&gl=IN&ceid=IN:en"));
        startActivity(browserIntent);
    }

    private void displayAccountCreationInfo() {
        // Get the account creation date from Firebase User metadata
        if (user != null) {
            long creationTime = user.getMetadata().getCreationTimestamp(); // Timestamp in milliseconds
            String accountCreationDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(creationTime));
            long currentTime = System.currentTimeMillis();
            long daysSinceCreation = (currentTime - creationTime) / (1000 * 60 * 60 * 24); // Convert milliseconds to days

            graphPlaceholder.append("Account Creation Date: " + accountCreationDate + "\n");
            graphPlaceholder.append("Days Since Account Creation: " + daysSinceCreation + " days\n");
        }
    }

    private void displayWorkoutCount() {
        int workoutCount = sharedPreferences.getInt(user.getUid() + "_workout_count", 0); // Get workout count
        graphPlaceholder.append("Workouts Completed Today: " + workoutCount);
    }

    private void incrementWorkoutCount() {
        int workoutCount = sharedPreferences.getInt(user.getUid() + "_workout_count", 0); // Get the current count
        workoutCount++; // Increment the count

        // Save the updated count back to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(user.getUid() + "_workout_count", workoutCount);
        editor.apply();
    }

    private void checkBmiAndStartExercise() {
        float bmi = sharedPreferences.getFloat(user.getUid() + "_bmi", -1); // Default to -1 if no data

        if (bmi == -1) {
            Toast.makeText(this, "Please calculate your BMI first!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, StartExerciseActivity.class);
            startActivity(intent);
        }
    }

    private void checkBmiAndStartDiet() {
        float bmi = sharedPreferences.getFloat(user.getUid() + "_bmi", -1); // Default to -1 if no data

        if (bmi == -1) {
            Toast.makeText(this, "Please calculate your BMI first!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, GenerateDietActivity.class);
            startActivity(intent);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                // Set the selected image to the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);

                // Upload the image to Firebase
                uploadImageToFirebase(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri uri) {
        String fileName = System.currentTimeMillis() + ".jpg"; // Generate a unique filename

        // Create a reference to the file to upload
        StorageReference fileReference = storageReference.child(fileName);
        fileReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Handle successful uploads
                    Toast.makeText(MainActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Get the download URL
                    fileReference.getDownloadUrl().addOnSuccessListener(this::saveImageUrlToFirestore);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToFirestore(Uri imageUrl) {
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a map to hold the image URL
            Map<String, Object> userData = new HashMap<>();
            userData.put("profileImageUrl", imageUrl.toString());

            // Update the user's document in Firestore
            db.collection("users").document(userId)
                    .update(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Image URL saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to save URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
