package com.fyp.inab.view.teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fyp.inab.R;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.about.AboutActivity;
import com.fyp.inab.view.authentication.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherHomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private Button btnLogout, btnProfile, btnClassroom, btnNilam, btnAbout;
    private MaterialAlertDialogBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_teacher);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Util.goToActivity(TeacherHomeActivity.this, LoginActivity.class);
        }

        // init component
        btnLogout = findViewById(R.id.btn_logout);
        btnProfile = findViewById(R.id.btn_profile_teacher);
        btnClassroom = findViewById(R.id.btn_classroom_teacher);
        btnNilam = findViewById(R.id.btn_nilam_teacher);
        btnAbout = findViewById(R.id.btn_about_teacher);

        // click listener
        btnClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(TeacherHomeActivity.this, TeacherClassroomActivity.class);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(TeacherHomeActivity.this, TeacherProfileActivity.class);
            }
        });

        btnNilam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(TeacherHomeActivity.this, TeacherNilamActivity.class);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(TeacherHomeActivity.this, AboutActivity.class);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new MaterialAlertDialogBuilder(TeacherHomeActivity.this, R.style.CustomDialogFont);
                builder.setTitle("Log Out")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                Util.goToActivity(TeacherHomeActivity.this, LoginActivity.class);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }
}