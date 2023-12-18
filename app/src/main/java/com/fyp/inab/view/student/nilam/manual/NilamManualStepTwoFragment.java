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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.fyp.inab.R;
import com.fyp.inab.object.Nilam;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

public class NilamManualStepTwoFragment extends Fragment {

    private AutoCompleteTextView dropdownLang, dropdownMaterial;
    private EditText etSummary;
    private RadioButton radioFiction;
    private Button btnNext;
    private MaterialAlertDialogBuilder builder;

    private String nilamJson, lang, material;
    private ArrayAdapter<String> adapter_nilamLang, adapter_nilamMaterial;

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.nilamJson = getArguments().getString("nilamInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nilam_manual_step_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        dropdownLang = view.findViewById(R.id.dropdown_lang_nilam);
        radioFiction = view.findViewById(R.id.radioFiction);
        btnNext = view.findViewById(R.id.btn_next_last_step_nilam);
        etSummary = view.findViewById(R.id.et_nilam_summary);
        dropdownMaterial = view.findViewById(R.id.dropdown_material);

        // nav controller
        navController = Navigation.findNavController(view);


        // init dropdown
        String[] arrLang = new String[] {"Malay", "English", "Tamil", "Mandarin"};
        adapter_nilamLang = new ArrayAdapter<>(
                getContext(),
                R.layout.dropdown_item,
                arrLang
        );
        dropdownLang.setAdapter(adapter_nilamLang);

        String[] arrMaterial = new String[] {"Journal", "Novel", "Comic", "Article"};
        adapter_nilamMaterial = new ArrayAdapter<>(
                getContext(),
                R.layout.dropdown_item,
                arrMaterial
        );
        dropdownMaterial.setAdapter(adapter_nilamMaterial);


        // dropdown click listener
        dropdownLang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lang = dropdownLang.getText().toString().trim();
            }
        });

        dropdownMaterial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                material = dropdownMaterial.getText().toString().trim();
            }
        });


        // click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson nilamGson = new Gson();
                Nilam nilam = nilamGson.fromJson(nilamJson, Nilam.class);

                if (formValidation()) {
                    // data
                    String summary = etSummary.getText().toString().trim();
                    String isFiction = radioFiction.isChecked() ? "fiction" : "non fiction";

                    // set
                    nilam.setSummary(summary);
                    nilam.setMaterial(material);
                    nilam.setType(isFiction);
                    nilam.setLang(lang);
                    nilam.setApproval("pending");

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("nilam", nilam);
                    navController.navigate(R.id.action_nilamManualStepTwoFragment2_to_nilamManualLastStepFragment, bundle);
                }
            }

            private boolean formValidation() {
                if (etSummary.getText().toString().isEmpty()) {
                    etSummary.requestFocus();
                    etSummary.setError("Please enter material summary");
                    return false;

                } else if (lang.isEmpty()) {
                    dropdownLang.requestFocus();
                    dropdownLang.setError("Please select language");
                    return false;

                } else if (material.isEmpty()) {
                    dropdownMaterial.requestFocus();
                    dropdownMaterial.setError("Please select material type");
                    return false;

                }
                return true;
            }
        });
    }
}