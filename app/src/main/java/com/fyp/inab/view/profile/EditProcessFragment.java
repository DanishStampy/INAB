package com.fyp.inab.view.profile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.User;
import com.fyp.inab.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class EditProcessFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    private EditText etFullName, etEmail, etPhoneNumber, etSchool;
    private Button btnSubmitEdit;
    private NavController navController;

    private ProfileViewModel profileViewModel;

    private String oldEmail, userJson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getApplication() != null) {
            profileViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ProfileViewModel.class);
        }

        if (getArguments() != null) {
            this.userJson = getArguments().getString("userInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // init component
        etFullName = view.findViewById(R.id.et_edit_fullname);
        etEmail = view.findViewById(R.id.et_edit_email);
        etPhoneNumber = view.findViewById(R.id.et_edit_phoneNum);
        etSchool = view.findViewById(R.id.et_edit_school);
        btnSubmitEdit = view.findViewById(R.id.btn_submit_edit);
        navController = Navigation.findNavController(view);
        
        // set data
        setDataInput();

        // click listener
        btnSubmitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: old email " + oldEmail);
                if (formValidation()) {

                    // data
                    String fullName = etFullName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phoneNum = etPhoneNumber.getText().toString().trim();
                    String school = etSchool.getText().toString().trim();

                    // map for updates data
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("fullName", fullName);
                    userUpdates.put("email", email);
                    userUpdates.put("phoneNum", phoneNum);
                    userUpdates.put("school", school);

                    Log.d(TAG, "onClick: email " + userUpdates.get("email"));
                    profileViewModel.updateUserProfile(userUpdates);
                    Toast.makeText(getActivity(), "Success edit user data", Toast.LENGTH_SHORT).show();
                    navController.popBackStack(R.id.profileDetailsFragment, false);

                } else {
                    Log.d(TAG, "onClick: form invalid");
                }
            }

            private boolean formValidation() {

                if (etFullName.getText().toString().isEmpty() || etFullName.getText().toString().trim().split(" ").length == 1) {
                    etFullName.requestFocus();
                    etFullName.setError("Please enter your new full name.");
                    return false;
                } else if (etEmail.getText().toString().isEmpty()) {
                    etEmail.requestFocus();
                    etEmail.setError("Please enter your new email.");
                    return false;
                } else if (!etEmail.getText().toString().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                    etEmail.requestFocus();
                    etEmail.setError("Please enter your new email with correct format");
                    return false;

                } else if (etPhoneNumber.getText().toString().isEmpty()) {
                    etPhoneNumber.requestFocus();
                    etPhoneNumber.setError("Please enter your new phone number.");
                    return false;
                } else if (etPhoneNumber.getText().toString().length() > 10 && etPhoneNumber.getText().toString().length() < 9) {
                    etPhoneNumber.requestFocus();
                    etPhoneNumber.setError("Please enter phone number with correct length");
                    return false;
                } else if (etSchool.getText().toString().isEmpty()) {
                    etSchool.requestFocus();
                    etSchool.setError("Please enter your new school name.");
                    return false;
                }
                return true;
            }
        });

    }

    private void setDataInput() {

        // deserialize json object using Gson
        Gson userGson = new Gson();
        User user = userGson.fromJson(userJson, User.class);

        //set data
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail());
        etPhoneNumber.setText(user.getPhoneNum());
        etSchool.setText(user.getSchool());
    }
}