package com.fyp.inab.view.student.classroom;

import static android.content.ContentValues.TAG;

import static com.fyp.inab.object.Util.backHome;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.adapter.ClassroomListAdapter;
import com.fyp.inab.object.Classroom;
import com.fyp.inab.viewmodel.ClassroomViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentClassroomListFragment extends Fragment implements ClassroomListAdapter.OnClickClassRoomItem {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference classRoomRef;
    private DocumentReference userRef;

    private TextView tvEmptyError;
    private Button btnSubmitCode;
    private EditText etClassroomCode;
    private RecyclerView rcClassroomList;
    private NavController navController;
    private MaterialAlertDialogBuilder builder;

    private ClassroomViewModel classroomViewModel;

    private boolean isExists;
    String path = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // onBackPress callback
        backHome(this, getActivity(), 1);

        if (getActivity().getApplication() != null) {
            classroomViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ClassroomViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_classroom_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        classRoomRef = firestore.collection("users")
                        .document(auth.getUid())
                        .collection("classroom");
        userRef = firestore.collection("users")
                    .document(auth.getUid());

        // init component
        tvEmptyError = view.findViewById(R.id.tv_error_classroom_list_student);
        etClassroomCode = view.findViewById(R.id.et_classroom_code);
        btnSubmitCode = view.findViewById(R.id.btn_submit_classroom_code);
        rcClassroomList = view.findViewById(R.id.rc_classroom_list_student);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);
        navController = Navigation.findNavController(view);

        // recycler view
        setRecyclerView();


        // click listener
        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = etClassroomCode.getText().toString().trim();
                Log.d(TAG, "onClick: input = " + code);

                classroomViewModel.inputClassroomCode(code).observe(getActivity(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        switch (integer) {
                            case 0:
                                Toast.makeText(getActivity(), "You already entered the classroom", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(getActivity(), "Successfully enter the classroom!", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                etClassroomCode.requestFocus();
                                etClassroomCode.setError("Please enter the right classroom code");
                        }
                        // reset input code
                        etClassroomCode.setText("");
                    }
                });
            }
        });
    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcClassroomList.setLayoutManager(linearLayoutManager);

        FirestoreRecyclerOptions<Classroom> options = new FirestoreRecyclerOptions.Builder<Classroom>()
                .setQuery(classRoomRef, Classroom.class)
                .build();

        ClassroomListAdapter classroomListAdapter = new ClassroomListAdapter(options, this, 1);
        rcClassroomList.setAdapter(classroomListAdapter);
        classroomListAdapter.startListening();

        tvEmptyError.setVisibility((classroomListAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClickClassroom(Classroom classroom, String id, String teacherId) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("classroom_details", classroom);
        bundle.putString("classroom_id", id);
        bundle.putString("teacher_id", teacherId);
        navController.navigate(R.id.action_studentClassroomListFragment_to_studentClassroomDetailsFragment, bundle);
    }

    @Override
    public void onClickDeleteClassroom(String id, String teacherId) {
        builder.setTitle("Leave Confirmation")
                .setMessage("Are you sure you want to leave this classroom?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    classroomViewModel.leaveClassroom(id, teacherId);
                    Toast.makeText(getActivity(), "Successfully leave the classroom", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }
}