package com.example.fitnessapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GenerateDietActivity extends AppCompatActivity {

    private TextView welcomeDietTextView;
    private TextView dietPlanTextView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_diet);

        welcomeDietTextView = findViewById(R.id.welcomeDietTextView);
        dietPlanTextView = findViewById(R.id.dietPlanTextView);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        checkBmiStatus();
    }

    private void checkBmiStatus() {
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Double bmi = documentSnapshot.getDouble("bmi");

                            if (bmi != null) {
                                loadUserNameAndDisplayDietPlan(bmi.floatValue());
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
                        Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserNameAndDisplayDietPlan(float bmi) {
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        welcomeDietTextView.setText("Welcome, " + (name != null ? name : "User") + "! Here's your personalized diet plan.");

                        // Generate diet plans based on BMI category
                        String dietPlan;
                        if (bmi < 18.5) {
                            dietPlan = "Diet Plan for Underweight:\n\n"
                                    + "Calorie Goal: Surplus (consume more calories than you burn)\n\n"
                                    + "1. Whole grains like oats, quinoa, and brown rice.\n"
                                    + "2. Lean proteins such as chicken, fish, tofu, and legumes.\n"
                                    + "3. Healthy fats from sources like avocados, nuts, and olive oil.\n"
                                    + "4. Include regular meals and snacks to increase calorie intake.\n\n"
                                    + "Tips:\n- Focus on nutrient-dense foods and aim to gain muscle mass.";
                        } else if (bmi < 24.9) {
                            dietPlan = "Diet Plan for Normal Weight:\n\n"
                                    + "Calorie Goal: Maintenance (consume calories to maintain current weight)\n\n"
                                    + "1. Balanced meals with a mix of carbohydrates, proteins, and healthy fats.\n"
                                    + "2. Plenty of vegetables for vitamins, minerals, and fiber.\n"
                                    + "3. Lean protein sources like chicken, fish, beans, and eggs.\n"
                                    + "4. Drink water regularly and avoid sugary beverages.\n\n"
                                    + "Tips:\n- Focus on a balanced diet and regular meals to maintain energy levels.";
                        } else if (bmi < 29.9) {
                            dietPlan = "Diet Plan for Overweight:\n\n"
                                    + "Calorie Goal: Deficit (consume fewer calories than you burn to promote weight loss)\n\n"
                                    + "1. Low-calorie options like leafy greens, broccoli, and other high-fiber vegetables.\n"
                                    + "2. Lean proteins such as turkey, fish, or tofu to feel full with fewer calories.\n"
                                    + "3. High-fiber foods like oatmeal and whole grains to promote satiety.\n"
                                    + "4. Limit sugar, refined carbs, and processed foods.\n\n"
                                    + "Tips:\n- Include healthy fats in moderation and stay hydrated.";
                        } else {
                            dietPlan = "Diet Plan for Obesity:\n\n"
                                    + "Calorie Goal: Deficit (consume fewer calories than you burn)\n\n"
                                    + "1. High-fiber, low-calorie foods like leafy greens, berries, and legumes.\n"
                                    + "2. Protein-rich foods such as chicken, fish, beans, and low-fat dairy.\n"
                                    + "3. Avoid processed foods, sugary drinks, and high-calorie snacks.\n"
                                    + "4. Opt for healthy cooking methods like grilling, steaming, or baking.\n\n"
                                    + "Tips:\n- Consider consulting a nutritionist for a more tailored diet plan.";
                        }

                        dietPlanTextView.setText(dietPlan);
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
