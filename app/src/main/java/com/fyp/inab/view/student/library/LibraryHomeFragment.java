package com.fyp.inab.view.student.library;

import static com.fyp.inab.object.Util.backHome;

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

import com.fyp.inab.R;

public class LibraryHomeFragment extends Fragment {

    private Button btnAllBooks, btnRequestList;

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // onBackPress callback
        backHome(this, getActivity(), 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        btnAllBooks = view.findViewById(R.id.btn_all_books);
        btnRequestList = view.findViewById(R.id.btn_book_request_list);
        navController = Navigation.findNavController(view);

        // click listener
        btnAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_libraryHomeFragment_to_bookListFragment);
            }
        });

        btnRequestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_libraryHomeFragment_to_bookBorrowedListFragment);
            }
        });
    }
}