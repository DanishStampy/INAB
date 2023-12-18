package com.fyp.inab.view.teacher.classroom;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.adapter.StudentListAdapter;
import com.fyp.inab.object.Classroom;
import com.fyp.inab.object.LoadingDialog;
import com.fyp.inab.object.User;
import com.fyp.inab.viewmodel.ClassroomViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassroomDetailsFragment extends Fragment implements StudentListAdapter.OnClickStudentItem {

    private TextView tvClassTitle, tvClassDesc, tvClassCode, tvErrorMessage;
    private RecyclerView rcListStudent;
    private Button btnGenerateCode, btnResetCode, btnDeleteClassroom;
    private NavController navController;

    private Classroom classroom;
    private String classroomId;
    private MaterialAlertDialogBuilder builder;
    private ClassroomViewModel classroomViewModel;
    private StudentListAdapter studentListAdapter;
    private LoadingDialog dialog;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference studentListRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.classroom = getArguments().getParcelable("classroom_details");
            this.classroomId = getArguments().getString("classroom_id");
        }

        if (getActivity().getApplication() != null) {
            classroomViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ClassroomViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classroom_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        studentListRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("classroom")
                .document(classroomId)
                .collection("student_list");

        // init component
        tvClassTitle = view.findViewById(R.id.tv_class_title_details);
        tvClassDesc = view.findViewById(R.id.tv_class_description_details);
        rcListStudent = view.findViewById(R.id.rc_list_of_student_classroom);
        btnGenerateCode = view.findViewById(R.id.btn_generate_code);
        btnResetCode = view.findViewById(R.id.btn_reset_code);
        btnDeleteClassroom = view.findViewById(R.id.btn_delete_classroom);
        tvClassCode = view.findViewById(R.id.tv_class_code);
        //tvErrorMessage = view.findViewById(R.id.tv_error_classroom_details);
        navController = Navigation.findNavController(view);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);
        dialog = new LoadingDialog(getContext());

        // set data
        tvClassTitle.setText(classroom.getClassTitle());
        tvClassDesc.setText(classroom.getClassDescription());

        // setup recyclerview
        setRecyclerView();


        // checking the existence of class code
        classroomViewModel.classCodeIsExists(classroomId).observe(getActivity(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {

                boolean exist = (Boolean) stringObjectMap.get("isExist");

                if (exist) {
                    tvClassCode.setText(String.valueOf(stringObjectMap.get("code")));
                    tvClassCode.setVisibility(View.VISIBLE);
                    btnGenerateCode.setVisibility(View.GONE);
                    btnResetCode.setVisibility(View.VISIBLE);
                } else {
                    btnResetCode.setVisibility(View.GONE);
                    btnGenerateCode.setVisibility(View.VISIBLE);
                }
            }
        });

        // click listener
        btnGenerateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // generate 6 digit UUID for classroom
                String classCode = setupClassCode();

                // set hashmap for class code and path
                Map<String, Object> pathClassroom = new HashMap<>();
                pathClassroom.put("path", "users/" + auth.getUid() + "/classroom/" + classroomId);
                pathClassroom.put("code", classCode);

                classroomViewModel.generateClassroomCode(pathClassroom);
            }
        });

        btnResetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to reset the class code and generate a new one?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // generate 6 digit UUID for classroom
                                String classCode = setupClassCode();

                                // update/reset the class code
                                classroomViewModel.resetClassroomCode(classroomId, classCode);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        btnDeleteClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);
                builder.setTitle("Delete Confirmation")
                        .setMessage("Are you sure you want to delete " + classroom.getClassTitle() + " classroom?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            dialog.showDialog();
                            classroomViewModel.deleteClassroom(classroomId);
                            navController.popBackStack(R.id.classroomListFragment, false);
                            Toast.makeText(getActivity(), "Classroom has been deleted", Toast.LENGTH_SHORT).show();
                            dialog.hideDialog();
                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
            }
        });

    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcListStudent.setLayoutManager(linearLayoutManager);

        classroomViewModel.getStudentListClassroom(classroomId).observe(getActivity(), users -> {
//            Log.d(TAG, "setRecyclerView: " + users.isEmpty());
            studentListAdapter = new StudentListAdapter(users, this);
            rcListStudent.setAdapter(studentListAdapter);
        });

    }

    private String setupClassCode() {
        String classCode = UUID.randomUUID().toString().substring(0, 6);
        tvClassCode.setText(classCode);
        tvClassCode.setVisibility(View.VISIBLE);
        btnGenerateCode.setVisibility(View.GONE);
        btnResetCode.setVisibility(View.VISIBLE);

        return classCode;
    }

    @Override
    public void onClickStudent(User user, String id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("student", user);
        bundle.putString("student_id", id);
        navController.navigate(R.id.action_classroomDetailsFragment_to_classroomStudentProgressFragment, bundle);
    }
}