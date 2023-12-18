package com.fyp.inab.view.student.library.borrowed;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fyp.inab.R;
import com.fyp.inab.adapter.BorrowListAdapter;
import com.fyp.inab.viewmodel.LibraryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BookCurrentBorrowedFragment extends Fragment implements BorrowListAdapter.OnClickBorrowedItem {

    private RecyclerView rcCurrentBorrowed;
    private TextView tvEmptyMessage;
    private MaterialAlertDialogBuilder builder;

    private LibraryViewModel libraryViewModel;
    private BorrowListAdapter borrowListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getApplication() != null) {
            libraryViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(LibraryViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_current_borrowed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        rcCurrentBorrowed = view.findViewById(R.id.rc_book_current_borrowed_list);
        tvEmptyMessage = view.findViewById(R.id.tv_error_book_current);
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogFont);

        // recycler view
        setupRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcCurrentBorrowed.setLayoutManager(linearLayoutManager);

        libraryViewModel.getCurrentBorrowedList().observe(getActivity(), maps -> {
            borrowListAdapter = new BorrowListAdapter(maps, BorrowListAdapter.CURRENT, this);
            rcCurrentBorrowed.setAdapter(borrowListAdapter);
            borrowListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onClickBorrowedItemListener(String bookId, String requestId, String bookName) {
        builder.setTitle("Returning Book Confirmation")
                .setMessage("Are you sure want to return " + bookName + " today?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        libraryViewModel.returnBook(bookId, requestId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}