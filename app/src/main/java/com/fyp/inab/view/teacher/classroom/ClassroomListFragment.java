package com.fyp.inab.view.teacher.classroom;

import static com.fyp.inab.object.Util.backHome;

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
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.adapter.ClassroomListAdapter;
import com.fyp.inab.object.Classroom;
import com.fyp.inab.viewmodel.ClassroomViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClassroomListFragment extends Fragment implements ClassroomListAdapter.OnClickClassRoomItem {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference classroomRef;

    private TextView tvErrorMessage;
    private RecyclerView rcClassroomList;
    private ExtendedFloatingActionButton fabCreateClass;
    private View viewInflated;
    private MaterialAlertDialogBuilder builder;
    private NavController navController;

    private ClassroomViewModel classroomViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // onBackPress callback
        backHome(this, getActivity(), 0);

        if (getActivity().getApplication() != null) {
            classroomViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ClassroomViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classroom_list, container, false);
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
        rcClassroomList = view.findViewById(R.id.rc_classroom_list);
        fabCreateClass = view.findViewById(R.id.fab_create_class);
        navController = Navigation.findNavController(view);
        tvErrorMessage = view.findViewById(R.id.tv_error_classroom_list_teacher);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);

        // recycler view
        setRecyclerView();


        // click listener
        fabCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setup input dialog
                viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.add_classroom_dialog, null, false);
                addClassroomBuilder();
            }
        });
    }

    private void addClassroomBuilder() {
        builder.setTitle("Create new classroom")
                .setView(viewInflated)
                .setPositiveButton("Create", (dialogInterface, i) -> {

                    // init component from viewInflated
                    EditText etClassTitle = viewInflated.findViewById(R.id.et_classroom_title);
                    EditText etClassDesc = viewInflated.findViewById(R.id.et_classroom_desc);

                    String title = etClassTitle.getText().toString().trim();
                    String description = etClassDesc.getText().toString().trim();

                    Classroom classroom = new Classroom(title, description, auth.getUid());
                    classroomViewModel.createClassroom(classroom);
                    Toast.makeText(getActivity(), "Classroom created!", Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> { dialogInterface.dismiss(); })
                .show();
    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcClassroomList.setLayoutManager(linearLayoutManager);

        FirestoreRecyclerOptions<Classroom> options = new FirestoreRecyclerOptions.Builder<Classroom>()
                .setQuery(classroomRef, Classroom.class)
                .build();

        ClassroomListAdapter classroomListAdapter = new ClassroomListAdapter(options, this, 1) {
            @Override
            public void onDataChanged() {
                tvErrorMessage.setVisibility((getItemCount() == 0) ? View.VISIBLE : View.GONE);
            }
        };

        rcClassroomList.setAdapter(classroomListAdapter);
        classroomListAdapter.startListening();
    }

    @Override
    public void onClickClassroom(Classroom classroom, String id, String teacherId) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("classroom_details", classroom);
        bundle.putString("classroom_id", id);
        navController.navigate(R.id.action_classroomListFragment_to_classroomDetailsFragment, bundle);
    }

    @Override
    public void onClickDeleteClassroom(String id, String teacherId) {
        builder.setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this classroom?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    classroomViewModel.deleteClassroom(id);
                    Toast.makeText(getActivity(), "Classroom has been deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }
}