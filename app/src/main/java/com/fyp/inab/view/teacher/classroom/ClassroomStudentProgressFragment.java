package com.fyp.inab.view.teacher.classroom;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fyp.inab.R;
import com.fyp.inab.adapter.NilamHistoryListAdapter;
import com.fyp.inab.object.Nilam;
import com.fyp.inab.object.User;
import com.fyp.inab.viewmodel.NilamViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ClassroomStudentProgressFragment extends Fragment implements NilamHistoryListAdapter.OnClickNilamItem {

    private RecyclerView rcNilamApproved;
    private TextView tvStudentName, tvTotalBooks, tvBookGoal, tvPhoneNumber;
    private NavController navController;
    private ProgressBar progressBar;
    private MaterialAlertDialogBuilder builder;

    private FirebaseFirestore firestore;

    private NilamViewModel nilamViewModel;

    private String studentId;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.studentId = getArguments().getString("student_id");
            this.user = getArguments().getParcelable("student");
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
        return inflater.inflate(R.layout.fragment_classroom_student_progress, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        firestore = FirebaseFirestore.getInstance();

        // init component
        navController = Navigation.findNavController(view);
        tvStudentName = view.findViewById(R.id.tv_nilam_student_name);
        tvTotalBooks = view.findViewById(R.id.tv_nilam_total_books);
        tvBookGoal = view.findViewById(R.id.tv_nilam_books_goal);
        rcNilamApproved = view.findViewById(R.id.rc_nilam_history_approved_progress);
        progressBar = view.findViewById(R.id.progressBar_nilam);
        tvPhoneNumber = view.findViewById(R.id.tv_phoneNumber_student);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);

        // setup recycler view
        setupRecyclerView();

        // set data
        progressNilam();
        tvStudentName.setText(user.getFullName());
        tvPhoneNumber.setText(" +60"+user.getPhoneNum());

        // click listener
        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "+60"+user.getPhoneNum();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber ));
                startActivity(intent);
            }
        });
    }

    private void progressNilam() {
        nilamViewModel.getNilamProgress(studentId).observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tvTotalBooks.setText(String.valueOf(integer));
                progressBar.setMax(121);
                progressBar.setProgress(integer);
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcNilamApproved.setLayoutManager(linearLayoutManager);

        Query query = firestore.collection("users")
                .document(studentId)
                .collection("nilam")
                .whereEqualTo("approval", "approved");

        FirestoreRecyclerOptions<Nilam> options = new FirestoreRecyclerOptions.Builder<Nilam>()
                .setQuery(query, Nilam.class)
                .build();

        NilamHistoryListAdapter nilamHistoryListAdapter = new NilamHistoryListAdapter(options, this);
        rcNilamApproved.setAdapter(nilamHistoryListAdapter);
        nilamHistoryListAdapter.startListening();
    }

    @Override
    public void onClickDisplayComment(String comment, String summary, String date) {
        builder.setTitle("Comment NILAM for " + date)
                .setMessage("Student summary:\n" + summary + "\n\nTeacher's comment:\n" + comment)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}