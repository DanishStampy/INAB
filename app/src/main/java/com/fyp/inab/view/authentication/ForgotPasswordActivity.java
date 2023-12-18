package com.fyp.inab.view.authentication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.LoadingDialog;
import com.fyp.inab.object.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailAddress;
    private Button btnSubmit;

    private FirebaseAuth auth;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // init firebase
         auth = FirebaseAuth.getInstance();

         // init component
        etEmailAddress = findViewById(R.id.et_forgot_password);
        btnSubmit = findViewById(R.id.btn_submit_forgot_password);
        dialog = new LoadingDialog(this);

        // click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.showDialog();

                if (formValidation()) {
                    String email = etEmailAddress.getText().toString().trim();

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: email sent");
                                        dialog.hideDialog();
                                        Util.goToActivity(ForgotPasswordActivity.this, LoginActivity.class);
                                        Toast.makeText(ForgotPasswordActivity.this, "Please check your email inbox", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "Email doesn't registered in the system", Toast.LENGTH_SHORT).show();
                                        dialog.hideDialog();
                                    }
                                }
                            });
                } else {
                    dialog.hideDialog();
                }
            }
        });

    }

    private boolean formValidation() {
        if (etEmailAddress.getText().toString().isEmpty()) {
            etEmailAddress.requestFocus();
            etEmailAddress.setError("Please enter the email address");
            return false;
        }
        return true;
    }
}