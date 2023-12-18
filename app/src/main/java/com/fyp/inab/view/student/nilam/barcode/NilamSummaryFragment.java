package com.fyp.inab.view.student.nilam.barcode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.adapter.ClassroomListAdapter;
import com.fyp.inab.object.Classroom;
import com.fyp.inab.object.Nilam;
import com.fyp.inab.viewmodel.NilamViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NilamSummaryFragment extends Fragment implements ClassroomListAdapter.OnClickClassRoomItem{

    private EditText etSummary;
    private RecyclerView rcClassroomList;
    private MaterialAlertDialogBuilder builder;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference classroomRef;

    private NilamViewModel nilamViewModel;
    private NavController navController;

    private Nilam nilam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.nilam = getArguments().getParcelable("nilam_info");
        }

        if (getActivity().getApplication() != null) {
            nilamViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(NilamViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nilam_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        classroomRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("classroom");

        // init component
        etSummary = view.findViewById(R.id.et_nilam_summary_scan_barcode);
        rcClassroomList = view.findViewById(R.id.rc_classroom_list_nilam_barcode);
        navController = Navigation.findNavController(view);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);

        // recycler view
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcClassroomList.setLayoutManager(linearLayoutManager);

        FirestoreRecyclerOptions<Classroom> options = new FirestoreRecyclerOptions.Builder<Classroom>()
                .setQuery(classroomRef, Classroom.class)
                .build();

        ClassroomListAdapter classroomListAdapter = new ClassroomListAdapter(options, this, 0);
        rcClassroomList.setAdapter(classroomListAdapter);
        classroomListAdapter.startListening();
    }

    @Override
    public void onClickClassroom(Classroom classroom, String id, String teacherId) {

        if ( !etSummary.getText().toString().isEmpty()) {
            String summary = etSummary.getText().toString().trim();
            nilam.setSummary(summary);

            builder = new MaterialAlertDialogBuilder(getContext(), R.style.CustomDialogFont);
            builder.setTitle("Confirm NILAM Submission")
                    .setMessage("Are you sure for this choice? \nClassroom : " + classroom.getClassTitle())
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        nilamViewModel.createNilam(nilam, classroom.getTeacherId(), id);
                        Toast.makeText(getContext(), "Wait for teacher approval...", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_nilamSummaryFragment_to_nilamProgressFragment);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .show();
        } else {
            etSummary.requestFocus();
            etSummary.setError("Please enter the NILAM summary first!");
        }
    }

    @Override
    public void onClickDeleteClassroom(String id, String teacherId) {

    }
}