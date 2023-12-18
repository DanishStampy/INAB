package com.fyp.inab.view.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.LoadingDialog;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.student.StudentHomeActivity;
import com.fyp.inab.view.teacher.TeacherHomeActivity;
import com.fyp.inab.view.teacher.TeacherProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private LoadingDialog dialog;
    private EditText etLoginEmail, etLoginPassword;
    private TextView tvForgotPassword;

    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // init component
        TextView tvRegisterPage = findViewById(R.id.btn_register_page);
        etLoginEmail = findViewById(R.id.et_login_email);
        etLoginPassword = findViewById(R.id.et_login_password);
        Button btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.btn_forgot_password);

        // init loading
        dialog = new LoadingDialog(LoginActivity.this);

        // click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (formValidation()) {
                    dialog.showDialog();

                    String email = etLoginEmail.getText().toString().trim();
                    String password = etLoginPassword.getText().toString().trim();

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        firestore.collection("users")
                                                .document(auth.getUid())
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String roles = documentSnapshot.getString("role");

                                                        // set user sharedPreferences
                                                        userPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = userPreferences.edit();
                                                        editor.putString("role", roles);
                                                        editor.putString("id", auth.getUid());
                                                        editor.apply();

                                                        dialog.hideDialog();

                                                        if (roles.equals("student")) {
                                                            Util.goToActivity(LoginActivity.this, StudentHomeActivity.class);

                                                        } else if (roles.equals("teacher")) {
                                                            Util.goToActivity(LoginActivity.this, TeacherHomeActivity.class);

                                                        }
                                                    }
                                                });
                                    } else {
                                        dialog.hideDialog();
                                        Toast.makeText(LoginActivity.this, "Wrong credentials. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        tvRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(LoginActivity.this, RegisterActivity.class);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.goToActivity(LoginActivity.this, ForgotPasswordActivity.class);
            }
        });

    }

    private boolean formValidation() {

        if (etLoginEmail.getText().toString().isEmpty()) {
            etLoginEmail.requestFocus();
            etLoginEmail.setError("Please enter the email");
            return false;

        } else if (etLoginPassword.getText().toString().isEmpty()) {
            etLoginPassword.requestFocus();
            etLoginPassword.setError("Please enter the password");
            return false;
        }
        return true;
    }
}