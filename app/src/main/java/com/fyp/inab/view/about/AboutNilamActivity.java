package com.fyp.inab.view.about;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fyp.inab.R;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.student.StudentHomeActivity;
import com.fyp.inab.view.teacher.TeacherHomeActivity;

public class AboutNilamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_nilam);
    }

    @Override
    public void onBackPressed() {
        Util.goToActivity(AboutNilamActivity.this, AboutActivity.class);
    }
}