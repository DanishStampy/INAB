package com.fyp.inab.view.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fyp.inab.R;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.about.AboutActivity;
import com.fyp.inab.view.authentication.LoginActivity;
import com.fyp.inab.viewmodel.ClassroomViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentHomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private Button btnLogout, btnProfile, btnClassroom, btnLibrary, btnNilam, btnAbout;
    private MaterialAlertDialogBuilder builder;

    private ClassroomViewModel classroomViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);

        // init viewModel
        classroomViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(ClassroomViewModel.class);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Util.goToActivity(StudentHomeActivity.this, LoginActivity.class);
        }

        // init component
        btnLogout = findViewById(R.id.btn_logout);
        btnProfile = findViewById(R.id.btn_profile_student);
        btnClassroom = findViewById(R.id.btn_classroom_list);
        btnLibrary = findViewById(R.id.btn_library);
        btnNilam = findViewById(R.id.btn_nilam);
        btnAbout = findViewById(R.id.btn_about_student);
        builder = new MaterialAlertDialogBuilder(StudentHomeActivity.this, R.style.CustomDialogFont);


        // click listener
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(StudentHomeActivity.this, StudentProfileActivity.class);
            }
        });

        btnClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(StudentHomeActivity.this, StudentClassroomActivity.class);
            }
        });

        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(StudentHomeActivity.this, StudentLibraryActivity.class);
            }
        });

        btnNilam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classroomViewModel.totalCountClassroom().observe(StudentHomeActivity.this, count -> {
                    if (count > 0) {
                        Util.goToActivity(StudentHomeActivity.this, StudentNilamActivity.class);
                    } else {
                        builder.setTitle("No classroom joined")
                                .setMessage("You need to join any classroom first before proceed with NILAM. " +
                                        "You can go to 'Classroom' section and insert the class code given by the teacher.")
                                .setPositiveButton("Noted", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                    classroomViewModel.totalCountClassroom().removeObservers(StudentHomeActivity.this);
                });
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(StudentHomeActivity.this, AboutActivity.class);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("Log Out")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                Util.goToActivity(StudentHomeActivity.this, LoginActivity.class);
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