package com.fyp.inab.view.student.nilam.manual;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fyp.inab.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NilamManualStepOneFragment extends Fragment {

    private EditText etTitle, etAuthor, etPages, etGenre;
    private Button btnNext;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nilam_manual_step_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        etTitle = view.findViewById(R.id.et_nilam_title);
        etAuthor = view.findViewById(R.id.et_nilam_author);
        etPages = view.findViewById(R.id.et_nilam_pages);
        etGenre = view.findViewById(R.id.et_nilam_genre);
        btnNext = view.findViewById(R.id.btn_next_step_nilam);
        navController = Navigation.findNavController(view);

        // click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (formValidation()) {
                    // data
                    String title = etTitle.getText().toString().trim();
                    String author = etAuthor.getText().toString().trim();
                    int pages = Integer.parseInt(etPages.getText().toString());
                    String genre = etGenre.getText().toString().trim();

                    JSONObject nilamObj = new JSONObject();

                    try {
                        nilamObj.put("title", title);
                        nilamObj.put("author", author);
                        nilamObj.put("pages", pages);
                        nilamObj.put("genre", genre);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String nilamInfo = nilamObj.toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("nilamInfo", nilamInfo);
                    navController.navigate(R.id.action_nilamManualStepOneFragment2_to_nilamManualStepTwoFragment2, bundle);
                }
            }

            private boolean formValidation() {
                if (etTitle.getText().toString().isEmpty()) {
                    etTitle.requestFocus();
                    etTitle.setError("Please enter material title");
                    return false;

                } else if (etAuthor.getText().toString().isEmpty()) {
                    etAuthor.requestFocus();
                    etAuthor.setError("Please enter material author.");
                    return false;

                } else if (etGenre.getText().toString().isEmpty()) {
                    etGenre.requestFocus();
                    etGenre.setError("Please enter material genre.");
                    return false;

                } else if (etPages.getText().toString().isEmpty()) {
                    etPages.requestFocus();
                    etPages.setError("Please enter material pages number.");
                    return false;

                }
                return true;
            }
        });
    }
}