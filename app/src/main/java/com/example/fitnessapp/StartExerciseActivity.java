package com.example.fitnessapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartExerciseActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private TextView exerciseLinksTextView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "StartExerciseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        exerciseLinksTextView = findViewById(R.id.exerciseLinksTextView);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        checkBmiStatus();
    }

    private void checkBmiStatus() {
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Double bmi = documentSnapshot.getDouble("bmi");

                            if (bmi != null) {
                                Log.d(TAG, "Retrieved BMI value from Firestore: " + bmi);
                                loadUserNameAndDisplay();

                                // Determine BMI category
                                String bmiCategory;
                                if (bmi < 18.5) {
                                    bmiCategory = "Underweight";
                                } else if (bmi < 24.9) {
                                    bmiCategory = "Normal";
                                } else {
                                    bmiCategory = "Overweight";
                                }

                                // Display exercises based on BMI category
                                displayExerciseOptions(bmiCategory);
                            } else {
                                Toast.makeText(this, "Please calculate your BMI first!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading user data", e);
                        Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserNameAndDisplay() {
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            welcomeTextView.setText("Hey " + (name != null ? name : "User") + ", get ready to start exercising!");
                        } else {
                            welcomeTextView.setText("Hey there, get ready to start exercising!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading user data", e);
                        welcomeTextView.setText("Hey there, get ready to start exercising!");
                    });
        }
    }

    private void displayExerciseOptions(String bmiCategory) {
        String exerciseOptions;

        switch (bmiCategory) {
            case "Underweight":
                exerciseOptions = "Recommended Exercise Routine for Underweight Individuals:\n\n"
                        + "1. Jumping Jacks – 2 sets of 15 seconds each (warm-up)\n"
                        + "2. Bodyweight Squats – 3 sets of 10-12 reps\n"
                        + "3. Push-Ups – 3 sets of 8-10 reps\n"
                        + "4. Plank – 3 sets of 20 seconds each\n"
                        + "5. Glute Bridges – 3 sets of 15 reps\n\n"
                        + "Tips:\n"
                        + "- Include a healthy diet with balanced macros to support muscle gain.\n"
                        + "- Rest adequately between sets (45-60 seconds) and avoid overexertion.";
                break;

            case "Normal":
                exerciseOptions = "Recommended Exercise Routine for Normal BMI:\n\n"
                        + "1. Jumping Jacks – 3 sets of 20 seconds each\n"
                        + "2. Bodyweight Squats – 3 sets of 15 reps\n"
                        + "3. Push-Ups – 3 sets of 10-15 reps\n"
                        + "4. Plank – 3 sets of 30 seconds each\n"
                        + "5. Mountain Climbers – 3 sets of 20 seconds each\n\n"
                        + "This workout hits various muscle groups and includes both cardio and strength components. "
                        + "Be sure to rest for 30-45 seconds between sets to keep your heart rate up but avoid overexertion.";
                break;

            case "Overweight":
                exerciseOptions = "Recommended Exercise Routine for Overweight Individuals:\n\n"
                        + "1. Marching in Place – 3 sets of 20 seconds each (low-impact warm-up)\n"
                        + "2. Wall Push-Ups – 3 sets of 10-12 reps\n"
                        + "3. Seated Knee Lifts – 3 sets of 15 reps each leg\n"
                        + "4. Standing Side Leg Lifts – 3 sets of 15 reps each leg\n"
                        + "5. Plank (Modified on Knees) – 2-3 sets of 15-20 seconds each\n\n"
                        + "Tips:\n"
                        + "- Take 30-45 seconds between sets to maintain pace without heavy strain.\n"
                        + "- Focus on a nutritious diet with calorie control to support weight loss.";
                break;

            default:
                exerciseOptions = "No specific exercise routine found for this BMI category.";
                break;
        }

        exerciseLinksTextView.setText(exerciseOptions);
    }
}

