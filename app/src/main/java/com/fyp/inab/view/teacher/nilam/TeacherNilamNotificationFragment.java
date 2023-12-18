package com.fyp.inab.view.teacher.nilam;

import static android.content.ContentValues.TAG;
import static com.fyp.inab.object.Util.*;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.adapter.NilamNotificationListAdapter;
import com.fyp.inab.adapter.OpenPDFContract;
import com.fyp.inab.object.NilamLog;
import com.fyp.inab.object.Util;
import com.fyp.inab.viewmodel.NilamViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TeacherNilamNotificationFragment extends Fragment implements NilamNotificationListAdapter.OnClickNotificationItem {

    private RecyclerView rcNilamNotification;
    private NavController navController;
    private ExtendedFloatingActionButton fabGenerateLog;
    private MaterialAlertDialogBuilder builder;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private NilamNotificationListAdapter nilamNotificationListAdapter;
    private NilamViewModel nilamViewModel;

    private boolean isFirstTime = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // onBackPress callback
        backHome(this, getActivity(), 0);

        if (getActivity().getApplication() != null) {
            nilamViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(NilamViewModel.class);
        }

        OpenPDFContract openPDFContract = new OpenPDFContract();
        registerForActivityResult(openPDFContract, new ActivityResultCallback<Integer>() {
            @Override
            public void onActivityResult(Integer result) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_nilam_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // init component
        rcNilamNotification = view.findViewById(R.id.rc_nilam_notification);
        navController = Navigation.findNavController(view);
        fabGenerateLog = view.findViewById(R.id.fab_generate_log_report);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);

        // setup recycler view
        setupRecyclerView();

        // click listener
        fabGenerateLog.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                try {
                    generatePDFLogReport();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generatePDFLogReport() throws FileNotFoundException {
        nilamViewModel.getLogList().observe(getActivity(), nilamLogs -> {
            if (nilamLogs.isEmpty()) {
                builder.setTitle("No Log")
                        .setMessage("Currently, no approval NILAM log is available to generate a report.")
                        .setNeutralButton("Okay", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
            } else {
                Log.d(TAG, "generatePDFLogReport: " + nilamLogs.size());
                String fileName = System.currentTimeMillis() + "_log_report";

            /*
            check if android version > 10
                if yes: write inside document
                if not: write inside Android/data folder
             */
                File file = Util.getVersion(TeacherNilamNotificationFragment.this);

                if (file != null) {
                    File pdfFile = new File(file, fileName + ".pdf");
                    Log.d(TAG, "generatePDFLogReport: path" + pdfFile.getAbsolutePath());

                    PdfWriter pdfWriter = null;
                    try {
                        pdfWriter = new PdfWriter(pdfFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                    Document document = new Document(pdfDocument);
                    // Header
                    Paragraph paragraph = new Paragraph("Audit Log NILAM Report");
                    paragraph.setUnderline(1.5f, -1)
                            .setFontSize(24)
                            .setBold()
                            .add("\n\n");
                    document.add(paragraph);

                    //Table
                    float[] pointColumnWidths = {75F, 75F, 75F, 75F, 75F, 75F}; // 6 columns

                    // header**
                    Table table = new Table(UnitValue.createPercentArray(new float[]{5, 5, 5, 5, 5, 5}))
                            .setWidth(UnitValue.createPercentValue(100))
                            .addCell(new Cell().add(new Paragraph("Date").setBold()).setTextAlignment(TextAlignment.CENTER).setMargin(5))
                            .addCell(new Cell().add(new Paragraph("Book Title (Nilam)").setBold()).setTextAlignment(TextAlignment.CENTER).setMargin(5))
                            .addCell(new Cell().add(new Paragraph("Classroom Name").setBold()).setTextAlignment(TextAlignment.CENTER).setMargin(5))
                            .addCell(new Cell().add(new Paragraph("Student Name").setBold()).setTextAlignment(TextAlignment.CENTER).setMargin(5))
                            .addCell(new Cell().add(new Paragraph("Response").setBold()).setTextAlignment(TextAlignment.CENTER).setMargin(5))
                            .addCell(new Cell().add(new Paragraph("Comment").setBold()).setTextAlignment(TextAlignment.CENTER).setMargin(5));

                    // content**
                    for (int i = 0; i < nilamLogs.size(); i++) {
                        //NilamLog log = nilamLogs.get(i);
                        Log.d(TAG, "onChanged: nilam id" + nilamLogs.get(0));
                        table.addCell(new Cell().add(new Paragraph(nilamLogs.get(i).getDate())));
                        table.addCell(new Cell().add(new Paragraph(nilamLogs.get(i).getNilam_id())));
                        table.addCell(new Cell().add(new Paragraph(nilamLogs.get(i).getClassroom_name())));
                        table.addCell(new Cell().add(new Paragraph(nilamLogs.get(i).getStudent_name())));
                        table.addCell(new Cell().add(new Paragraph(nilamLogs.get(i).getStatus_nilam())));
                        table.addCell(new Cell().add(new Paragraph(nilamLogs.get(i).getComment())));
                    }
                    document.add(table);

                    // close
                    document.close();

                    builder.setTitle("Audit NILAM Report")
                            .setMessage("Report generated inside Download folder from internal phone storage. Do you want access the file?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.Q)
                                @SuppressLint("IntentReset")
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String path = pdfFile.getAbsolutePath();
                                    int startIndex = path.indexOf("0/") + "0/".length();
                                    String subPath = path.substring(startIndex);
                                    Log.d(TAG, "onClick: " + subPath);

                                    File newFile = new File(Environment.getExternalStorageDirectory(), pdfFile.getPath());
                                    Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.librarynilam.fileprovider", newFile);
                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
                                    intent.setType("application/pdf");
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(intent);


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                } else {
                    Toast.makeText(getActivity(), "Something wrong", Toast.LENGTH_SHORT).show();
                }

                nilamViewModel.getLogList().removeObservers(getActivity());
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcNilamNotification.setLayoutManager(linearLayoutManager);

        nilamViewModel.getListNotification().observe(getActivity(), maps -> {
            nilamNotificationListAdapter = new NilamNotificationListAdapter(maps, this);
            rcNilamNotification.setAdapter(nilamNotificationListAdapter);
        });

    }

    @Override
    public void onClickNotification(String path, String class_id, String noti_id, String sender) {
        Bundle bundle = new Bundle();
        bundle.putString("path_nilam", path);
        bundle.putString("class_id", class_id);
        bundle.putString("noti_id", noti_id);
        bundle.putString("student_name", sender);
        navController.navigate(R.id.action_teacherNilamNotificationFragment_to_teacherNilamDetailsFragment, bundle);
    }
}