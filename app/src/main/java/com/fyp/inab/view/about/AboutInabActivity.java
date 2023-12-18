package com.fyp.inab.view.about;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fyp.inab.R;
import com.fyp.inab.object.Util;

public class AboutInabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_inab);
    }

    @Override
    public void onBackPressed() {
        Util.goToActivity(AboutInabActivity.this, AboutActivity.class);
    }
}