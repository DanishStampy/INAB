package com.fyp.inab.view.authentication.form;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.view.authentication.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class StepOneRegistrationFragment extends Fragment {

    String fullName, email, phoneNum, roles, school;

    private TextView tvLoginPage;
    private EditText etRegisterFullName, etRegisterEmail, etRegisterPhoneNum, etRegisterSchool;
    private RadioButton radioRoles;
    private Button btnNextRegistrationStep;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_one_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        tvLoginPage = view.findViewById(R.id.btn_login_page);
        etRegisterFullName = view.findViewById(R.id.register_fullname);
        etRegisterEmail = view.findViewById(R.id.register_email);
        etRegisterPhoneNum = view.findViewById(R.id.register_phone_num);
        etRegisterSchool = view.findViewById(R.id.register_school);
        radioRoles = view.findViewById(R.id.radioBtnStudent);
        btnNextRegistrationStep = view.findViewById(R.id.btn_next_step);

        // fragment navigation
        navController = Navigation.findNavController(view);


        // click listener
        btnNextRegistrationStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // data
                fullName = etRegisterFullName.getText().toString().trim();
                email = etRegisterEmail.getText().toString().trim();
                phoneNum = etRegisterPhoneNum.getText().toString().trim();
                roles = radioRoles.isChecked() ? "student" : "teacher";
                school = etRegisterSchool.getText().toString().trim();

                if (formValidation()) {
                    // init json object and send to next page
                    JSONObject userObj = new JSONObject();

                    try {
                        userObj.put("fullName", fullName);
                        userObj.put("email", email);
                        userObj.put("phoneNum", phoneNum);
                        userObj.put("role", roles);
                        userObj.put("school", school);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String userInfo = userObj.toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("userInfo", userInfo);
                    navController.navigate(R.id.action_stepOneRegistrationFragment_to_stepTwoRegistrationFragment, bundle);
                } else {
                    Toast.makeText(getActivity(), "Please fill in form.", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean formValidation() {

                if (fullName.isEmpty() || fullName.split(" ").length == 1) {
                    etRegisterFullName.requestFocus();
                    etRegisterFullName.setError("Please enter your full name");
                    return false;

                } else if (email.isEmpty()) {
                    etRegisterEmail.requestFocus();
                    etRegisterEmail.setError("Please enter your email");
                    return false;

                } else if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                    etRegisterEmail.requestFocus();
                    etRegisterEmail.setError("Please enter your email with correct format");
                    return false;

                } else if (phoneNum.isEmpty()) {
                    etRegisterPhoneNum.requestFocus();
                    etRegisterPhoneNum.setError("Please enter your phone number");
                    return false;

                } else if (phoneNum.length() > 10 && phoneNum.length() < 9) {
                    etRegisterPhoneNum.requestFocus();
                    etRegisterPhoneNum.setError("Please enter phone number with correct length");
                    return false;
                }
                return true;
            }
        });

        tvLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPage();
            }
        });


    }

    private void loginPage() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

}