package com.fyp.inab.view.student.nilam.manual;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NilamManualLastStepFragment extends Fragment implements ClassroomListAdapter.OnClickClassRoomItem {

    private RecyclerView rcClassroomList;
    private NavController navController;
    private MaterialAlertDialogBuilder builder;

    private NilamViewModel nilamViewModel;
    private Nilam nilam;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference classroomRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.nilam = getArguments().getParcelable("nilam");
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
        return inflater.inflate(R.layout.fragment_nilam_manual_last_step, container, false);
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
        rcClassroomList = view.findViewById(R.id.rc_classroom_list_nilam);
        NavHostFragment navHostFragment = (NavHostFragment) getParentFragment();
        Fragment parent = (Fragment) navHostFragment.getParentFragment();
        navController = Navigation.findNavController(parent.getView().findViewById(R.id.fragment_parent_nilam));

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

        builder = new MaterialAlertDialogBuilder(getContext(), R.style.CustomDialogFont);
        builder.setTitle("Classroom Confirmation")
                .setMessage("Are you sure for this choice? \nClassroom : " + classroom.getClassTitle())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    nilamViewModel.createNilam(nilam, classroom.getTeacherId(), id);
                    Toast.makeText(getContext(), "Wait for teacher approval...", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_nilamCreateManualFragment_to_nilamProgressFragment);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    @Override
    public void onClickDeleteClassroom(String id, String teacherId) {

    }
}