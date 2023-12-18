package com.fyp.inab.view.authentication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.student.StudentHomeActivity;
import com.fyp.inab.view.teacher.TeacherHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    String userRoles;
    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Util.goToActivity(SplashScreenActivity.this, LoginActivity.class);
                }
            }, 3000);

        } else {
            firestore.collection("users")
                    .document(auth.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        userRoles = documentSnapshot.getString("role");
                        Log.d(TAG, "onCreate: current user " + auth.getCurrentUser());

                        // set user sharedPreferences
                        userPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = userPreferences.edit();
                        editor.putString("role", userRoles);
                        editor.putString("id", auth.getUid());
                        editor.apply();

                        if (userRoles.equals("student")){
                            Util.goToActivity(SplashScreenActivity.this, StudentHomeActivity.class);

                        } else {
                            Util.goToActivity(SplashScreenActivity.this, TeacherHomeActivity.class);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Please login again.", Toast.LENGTH_SHORT).show();
                        Util.goToActivity(SplashScreenActivity.this, LoginActivity.class);
                    });
        }
    }
}