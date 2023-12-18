package com.fyp.inab.view.authentication.form;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.LoadingDialog;
import com.fyp.inab.object.User;
import com.fyp.inab.object.Util;
import com.fyp.inab.view.authentication.LoginActivity;
import com.fyp.inab.view.authentication.SplashScreenActivity;
import com.fyp.inab.view.teacher.TeacherHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class StepTwoRegistrationFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private EditText etRegisterPassword, etRegisterConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginPage;

    private String password, confirmPassword, userJson;
    private LoadingDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.userJson = getArguments().getString("userInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_two_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // init component
        etRegisterPassword = view.findViewById(R.id.register_password);
        etRegisterConfirmPassword = view.findViewById(R.id.register_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        tvLoginPage = view.findViewById(R.id.btn_login_page);
        dialog = new LoadingDialog(getActivity());

        // click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = etRegisterPassword.getText().toString().trim();
                confirmPassword = etRegisterConfirmPassword.getText().toString().trim();

                dialog.showDialog();

                if(formValidation(password, confirmPassword)) {

                    // deserialize json object using Gson
                    Gson userGson = new Gson();

                    User user = userGson.fromJson(userJson, User.class);

                    auth.createUserWithEmailAndPassword(user.getEmail(), password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(user.getFullName())
                                                .build();

                                        firebaseUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User profile updated.");
                                                        }
                                                    }
                                                });

                                        firestore.collection("users")
                                                .document(auth.getUid())
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "onSuccess: register");
                                                        dialog.hideDialog();
                                                        Toast.makeText(getActivity(), "Registration is successful. Please login again.", Toast.LENGTH_SHORT).show();
                                                        loginPage();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: register");
                                                        dialog.hideDialog();
                                                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        dialog.hideDialog();
                                        Log.d(TAG, "onComplete: failed register not success");
                                    }
                                }
                            });

                } else {
                    dialog.hideDialog();
                }
            }
        });

        tvLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPage();
            }
        });

    }

    private boolean formValidation(String password, String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            etRegisterConfirmPassword.requestFocus();
            etRegisterConfirmPassword.setError("Please make sure the password are match");
            return false;

        } else if (password.length() < 7) {
            etRegisterPassword.requestFocus();
            etRegisterPassword.setError("Please enter password more than 7 characters");
            return false;

        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{7,}$")) {
            etRegisterPassword.requestFocus();
            etRegisterPassword.setError("Please make sure the password has at least one alphabet and one number");
            return false;
        }
        return true;
    }

    private void loginPage() {
        Util.goToActivity(getActivity(), LoginActivity.class);
    }
}