package com.fyp.inab.view.student.nilam.history;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fyp.inab.R;
import com.fyp.inab.adapter.NilamHistoryListAdapter;
import com.fyp.inab.object.Nilam;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NilamApprovedFragment extends Fragment implements NilamHistoryListAdapter.OnClickNilamItem {

    private RecyclerView rcNilamApproved;
    private TextView tvEmptyMessage;
    private MaterialAlertDialogBuilder builder;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nilam_approved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // init component
        rcNilamApproved = view.findViewById(R.id.rc_nilam_history_approved);
        tvEmptyMessage = view.findViewById(R.id.tv_error_approved_nilam);


        // setup recycler view
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcNilamApproved.setLayoutManager(linearLayoutManager);

        Query query = firestore.collection("users")
                .document(auth.getUid())
                .collection("nilam")
                .whereEqualTo("approval", "approved");

        FirestoreRecyclerOptions<Nilam> options = new FirestoreRecyclerOptions.Builder<Nilam>()
                .setQuery(query, Nilam.class)
                .build();

        NilamHistoryListAdapter nilamHistoryListAdapter = new NilamHistoryListAdapter(options, this) {
            @Override
            public void onDataChanged() {
                tvEmptyMessage.setVisibility((getItemCount() == 0) ? View.VISIBLE : View.GONE);
            }
        };

        rcNilamApproved.setAdapter(nilamHistoryListAdapter);
        nilamHistoryListAdapter.startListening();
    }

    @Override
    public void onClickDisplayComment(String comment, String summary, String date) {
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);
        builder.setTitle("Comment NILAM for " + date)
                .setMessage("You summary:\n" + summary + "\n\nTeacher's comment:\n" + comment)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}