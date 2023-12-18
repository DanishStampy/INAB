package com.fyp.inab.view.teacher.nilam;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.Nilam;
import com.fyp.inab.viewmodel.NilamViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TeacherNilamDetailsFragment extends Fragment {

    private TextView tvTitleAuthor, tvMaterial, tvPages, tvLanguage, tvGenre, tvSummary;
    private EditText etComment;
    private Button btnAccept, btnReject;
    private NavController navController;
    private MaterialAlertDialogBuilder builder;

    private String pathToNilam, classId, studentName, notiId;

    private NilamViewModel nilamViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.pathToNilam = getArguments().getString("path_nilam");
            this.classId = getArguments().getString("class_id");
            this.studentName = getArguments().getString("student_name");
            this.notiId = getArguments().getString("noti_id");
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
        return inflater.inflate(R.layout.fragment_teacher_nilam_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        tvTitleAuthor = view.findViewById(R.id.tv_nilam_book_title);
        tvMaterial = view.findViewById(R.id.tv_nilam_material);
        tvPages = view.findViewById(R.id.tv_nilam_pages);
        tvLanguage = view.findViewById(R.id.tv_nilam_language);
        tvGenre = view.findViewById(R.id.tv_nilam_genre);
        tvSummary = view.findViewById(R.id.tv_nilam_summary);
        btnAccept = view.findViewById(R.id.btn_accept_nilam);
        btnReject = view.findViewById(R.id.btn_reject_nilam);
        etComment = view.findViewById(R.id.et_nilam_comment);
        navController = Navigation.findNavController(view);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);

        // set data
        setData();

        // click listener
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNilam("approved");
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNilam("rejected");
            }
        });
    }

    private void updateNilam(String response) {
        builder.setTitle("Confirmation")
                .setMessage("Are you sure to "+ response +" this NILAM?")
                .setPositiveButton("Confirm", (dialogInterface, i) -> {

                    String comment = etComment.getText().toString().trim();
                    nilamViewModel.updateNilamApproval(pathToNilam, response, notiId, comment);
                    nilamViewModel.createLogNilam(pathToNilam, classId, studentName, response, comment);
                    Toast.makeText(getActivity(), "Status has been updated", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_teacherNilamDetailsFragment_to_teacherNilamNotificationFragment);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void setData() {
        nilamViewModel.getNilamData(pathToNilam).observe(getActivity(), new Observer<Nilam>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Nilam nilam) {
                tvTitleAuthor.setText(nilam.getTitle() + " by " + nilam.getAuthor());
                tvMaterial.setText(nilam.getMaterial());
                tvPages.setText(nilam.getPages() + "");
                tvLanguage.setText(nilam.getLang());
                tvGenre.setText(nilam.getGenre());
                tvSummary.setText(nilam.getSummary());
            }
        });
    }
}