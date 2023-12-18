package com.fyp.inab.view.student.classroom;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.Classroom;
import com.fyp.inab.object.LoadingDialog;
import com.fyp.inab.object.User;
import com.fyp.inab.viewmodel.ClassroomViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentClassroomDetailsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private TextView tvClassTitle, tvClassDesc, tvTeacherName, tvTeacherPhone, tvTeacherEmail;
    private Button btnLeaveClassroom;
    private MaterialAlertDialogBuilder builder;
    private NavController navController;

    private String classRoomID, teacherId;
    private Classroom classroom;
    private User user;
    private LoadingDialog dialog;

    private ClassroomViewModel classroomViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dialog = new LoadingDialog(getActivity());

        if (getArguments() != null) {
            this.classroom = getArguments().getParcelable("classroom_details");
            this.classRoomID = getArguments().getString("classroom_id");
            this.teacherId = getArguments().getString("teacher_id");
        }

        if (getActivity().getApplication() != null) {
            classroomViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ClassroomViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_classroom_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // init component
        tvClassTitle = view.findViewById(R.id.tv_class_title_details);
        tvClassDesc = view.findViewById(R.id.tv_class_description_details);
        tvTeacherName = view.findViewById(R.id.tv_class_teacher_name);
        tvTeacherPhone = view.findViewById(R.id.tv_class_teacher_phoneNum);
        tvTeacherEmail = view.findViewById(R.id.tv_class_teacher_email);
        btnLeaveClassroom = view.findViewById(R.id.btn_leave_classroom);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);
        navController = Navigation.findNavController(view);

        // set data
        setData();

        // click listener
        btnLeaveClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Leave Confirmation")
                        .setMessage("Are you sure wan to leave " + classroom.getClassTitle() + "?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            dialog.showDialog();
                            classroomViewModel.leaveClassroom(classRoomID, teacherId);
                            dialog.hideDialog();
                            Toast.makeText(getActivity(), "Successfully leave the classroom", Toast.LENGTH_SHORT).show();
                            navController.popBackStack(R.id.studentClassroomListFragment, false);
                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
            }
        });

        tvTeacherPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + user.getPhoneNum());

                String phoneNumber = "+60"+ user.getPhoneNum();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber ));
                startActivity(intent);
            }
        });

        tvTeacherEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + user.getEmail());

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + user.getEmail()));
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });
    }

    private void setData() {
        tvClassTitle.setText(classroom.getClassTitle());
        tvClassDesc.setText(classroom.getClassDescription());

        classroomViewModel.getTeacherData(classroom.getTeacherId()).observe(getActivity(), new Observer<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(User userData) {
                user = userData;
                tvTeacherName.setText("Name: " + user.getFullName());
                tvTeacherPhone.setText("+60"+user.getPhoneNum());
                tvTeacherEmail.setText(user.getEmail());
            }
        });
    }
}