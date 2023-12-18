package com.fyp.inab.view.student.nilam;

import static android.content.ContentValues.TAG;

import static com.fyp.inab.object.Util.backHome;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.adapter.NilamFragmentAdapter;
import com.fyp.inab.object.LoadingDialog;
import com.fyp.inab.view.student.nilam.barcode.NilamCaptureAct;
import com.fyp.inab.view.student.nilam.history.NilamAllFragment;
import com.fyp.inab.view.student.nilam.history.NilamApprovedFragment;
import com.fyp.inab.view.student.nilam.history.NilamRejectedFragment;
import com.fyp.inab.viewmodel.NilamViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class NilamProgressFragment extends Fragment {

    private TabLayout tabLayoutNilam;
    private ViewPager2 viewPagerNilam;
    private ExtendedFloatingActionButton fabOptions;
    private FloatingActionButton fabBarcodeScanner, fabManually;
    private TextView tvStudentName, tvTotalBooks, tvBookGoal, tvNilamAward;
    private NavController navController;
    private ProgressBar progressBar;

    private boolean isOpen;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private LoadingDialog dialog;

    private NilamViewModel nilamViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // onBackPress callback
        backHome(this, getActivity(), 1);

        if (getActivity().getApplication() != null) {
            nilamViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(NilamViewModel.class);
        }

        this.dialog = new LoadingDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nilam_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // init component
        fabOptions = view.findViewById(R.id.fab_options);
        fabBarcodeScanner = view.findViewById(R.id.fab_barcode_scanner_nilam);
        fabManually = view.findViewById(R.id.fab_manually_nilam);
        navController = Navigation.findNavController(view);
        tvStudentName = view.findViewById(R.id.tv_nilam_student_name);
        tvTotalBooks = view.findViewById(R.id.tv_nilam_total_books);
        tvBookGoal = view.findViewById(R.id.tv_nilam_books_goal);
        progressBar = view.findViewById(R.id.progressBar_nilam);
        tabLayoutNilam = view.findViewById(R.id.tabLayoutNilam);
        viewPagerNilam = view.findViewById(R.id.viewPagerBook);
        tvNilamAward = view.findViewById(R.id.tv_nilam_award);
        dialog.showDialog();

        // setup tabLayout and viewpager
        setupPagerAndTab();

        // init button state
        isOpen = false;
        fabBarcodeScanner.hide();
        fabManually.hide();
        fabOptions.shrink();

        // set data
        progressNilam();
        tvStudentName.setText(user.getDisplayName());

        // click listener
        fabOptions.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                isOpen = !isOpen;
                if (isOpen) {
                    fabBarcodeScanner.show();
                    fabManually.show();
                    fabOptions.extend();
                    fabOptions.setIcon(getResources().getDrawable(R.drawable.ic_baseline_close_24));

                } else {
                    fabBarcodeScanner.hide();
                    fabManually.hide();
                    fabOptions.shrink();
                    fabOptions.setIcon(getResources().getDrawable(R.drawable.add));

                }
            }
        });

        fabManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_nilamProgressFragment_to_nilamCreateManualFragment);
            }
        });

        fabBarcodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
    }

    private void setupPagerAndTab() {
        NilamFragmentAdapter nilamFragmentAdapter = new NilamFragmentAdapter(getChildFragmentManager(), getLifecycle());
        nilamFragmentAdapter.addFragment(new NilamApprovedFragment());
        nilamFragmentAdapter.addFragment(new NilamRejectedFragment());
        nilamFragmentAdapter.addFragment(new NilamAllFragment());

        viewPagerNilam.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPagerNilam.setAdapter(nilamFragmentAdapter);
        viewPagerNilam.requestDisallowInterceptTouchEvent(true);

        new TabLayoutMediator(tabLayoutNilam, viewPagerNilam, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Approved");
                    break;
                case 1:
                    tab.setText("Rejected");
                    break;
                case 2:
                    tab.setText("All");
                    break;
            }
        })).attach();

        dialog.hideDialog();
    }

    private void progressNilam() {
        nilamViewModel.getNilamProgress(auth.getUid()).observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalBooks) {
                nilamGoals(totalBooks);
            }

            @SuppressLint("SetTextI18n")
            private void nilamGoals(Integer totalBooks) {
                int max = 0;
                int star = 0;

                if (totalBooks < 120) {
                    max = 120;
                } else if (totalBooks < 240) {
                    star = 1;
                    max = 240;
                } else if (totalBooks < 360) {
                    star = 2;
                    max = 360;
                } else if (totalBooks < 480) {
                    star = 3;
                    max = 480;
                } else if (totalBooks < 600) {
                    star = 4;
                    max = 600;
                } else if (totalBooks >= 600) {
                    star = 5;
                    max = 100;
                }

                progressBar.setMax(max);
                progressBar.setProgress((max == 100) ? max : totalBooks);
                tvTotalBooks.setText(String.valueOf(totalBooks));
                tvBookGoal.setText("/" + max);
                tvNilamAward.setText(String.valueOf(star));
            }
        });
    }

    /*
     Barcode scanner config
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setDesiredBarcodeFormats(ScanOptions.CODE_128);
        options.setPrompt("Scan a correct barcode book");
        options.setBeepEnabled(false);
        options.setCameraId(0);
        options.setTimeout(10000);
        options.setCaptureActivity(NilamCaptureAct.class);
        barcodeLauncher.launch(options);
    }

    /*
    Scanner launcher
     */
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String book_id = result.getContents();
                    Log.d(TAG, "id : " + book_id);
                    Bundle bundle = new Bundle();
                    bundle.putString("book_id", book_id);
                    Toast.makeText(getActivity(), "Scanned!", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_nilamProgressFragment_to_nilamBarcodeScannerFragment, bundle);
                } else {
                    Toast.makeText(getActivity(), "You need to make sure the barcode almost perfectly fit the width of the container", Toast.LENGTH_SHORT).show();
                }
            });
}