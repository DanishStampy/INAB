package com.fyp.inab.view.about;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;

import com.fyp.inab.R;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.student.StudentHomeActivity;
import com.fyp.inab.view.teacher.TeacherHomeActivity;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences userPreferences;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // sharedPreferences
        userPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        role = userPreferences.getString("role", null);

        // init component
        Button btnInab = findViewById(R.id.btn_about_inab);
        Button btnNilam = findViewById(R.id.btn_about_nilam);

        // click listener
        btnInab.setOnClickListener(this);
        btnNilam.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Util.goToActivity(AboutActivity.this, (role.equals("teacher")) ? TeacherHomeActivity.class : StudentHomeActivity.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_about_inab:
                Util.goToActivity(AboutActivity.this, AboutInabActivity.class);
                break;
            case R.id.btn_about_nilam:
                Util.goToActivity(AboutActivity.this, AboutNilamActivity.class);
                break;
        }
    }
}