package com.fyp.inab.view.student.library.borrowed;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fyp.inab.R;
import com.fyp.inab.adapter.BorrowListAdapter;
import com.fyp.inab.viewmodel.LibraryViewModel;

public class BookHistoryRequestFragment extends Fragment implements BorrowListAdapter.OnClickBorrowedItem {

    private RecyclerView rcRequestList;
    private TextView tvEmptyMessage;

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
        return inflater.inflate(R.layout.fragment_book_history_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        rcRequestList = view.findViewById(R.id.rc_request_list);
        tvEmptyMessage = view.findViewById(R.id.tv_error_book_history);

        // recycler view
        setupRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcRequestList.setLayoutManager(linearLayoutManager);

        libraryViewModel.getHistoryList().observe(getActivity(), maps -> {
            borrowListAdapter = new BorrowListAdapter(maps, BorrowListAdapter.HISTORY, this);
            rcRequestList.setAdapter(borrowListAdapter);
            borrowListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onClickBorrowedItemListener(String bookId, String requestId, String bookName) {

    }
}