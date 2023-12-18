package com.fyp.inab.view.profile;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static com.fyp.inab.object.Util.backHome;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fyp.inab.R;
import com.fyp.inab.object.User;
import com.fyp.inab.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileDetailsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private TextView tvFullname, tvEmail, tvPhoneNumber, tvRoles, tvProfileDetails, tvSchoolName;
    private Button btnEdit, btnChangePassword;
    private NavController navController;

    private ProfileViewModel profileViewModel;
    private SharedPreferences userPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPreferences = requireActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
        String role = userPreferences.getString("role", null);
        Log.d(TAG, "onCreate: " +role);

        backHome(this, getActivity(), (role.equals("teacher")) ? 0 : 1);

        if (getActivity().getApplication() != null) {
            profileViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ProfileViewModel.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // init component
        tvFullname = view.findViewById(R.id.tv_fullname_profile);
        tvEmail = view.findViewById(R.id.tv_email_profile);
        tvPhoneNumber = view.findViewById(R.id.tv_phoneNum_profile);
        tvRoles = view.findViewById(R.id.tv_roles_profile);
        tvProfileDetails = view.findViewById(R.id.tv_profile_details);
        btnEdit = view.findViewById(R.id.btn_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        tvSchoolName = view.findViewById(R.id.tv_school_name);
        navController = Navigation.findNavController(view);

        // retrieve data and set
        setData();


        // click listener
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_profileDetailsFragment_to_resetPasswordFragment);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                profileViewModel.getUserProfile().observe(getActivity(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        // init json object and send to next page
                        JSONObject userObj = new JSONObject();

                        try {
                            userObj.put("fullName", user.getFullName());
                            userObj.put("email", user.getEmail());
                            userObj.put("phoneNum", user.getPhoneNum());
                            userObj.put("role", user.getRole());
                            userObj.put("school", user.getSchool());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String userInfo = userObj.toString();
                        bundle.putString("userInfo", userInfo);
                    }
                });
                //Log.d(TAG, "onClick: bundle = " + bundle);
                navController.navigate(R.id.action_profileDetailsFragment_to_editProcessFragment, bundle);
            }
        });
    }

    private void setData() {

        profileViewModel.getUserProfile().observe(getActivity(), new Observer<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(User user) {

                String role = user.getRole();
                String uppercaseRole = role.substring(0, 1).toUpperCase() + role.substring(1);

                tvFullname.setText(user.getFullName());
                tvEmail.setText(user.getEmail());
                tvPhoneNumber.setText("+60" + user.getPhoneNum());
                tvRoles.setText(uppercaseRole + "'s Account");
                tvProfileDetails.setText(uppercaseRole + "'s Details");
                tvSchoolName.setText(user.getSchool());
            }
        });
    }
}