package com.fyp.inab.view.student.nilam.barcode;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fyp.inab.R;
import com.fyp.inab.object.Book;
import com.fyp.inab.object.Nilam;
import com.fyp.inab.viewmodel.LibraryViewModel;

public class NilamBarcodeScannerFragment extends Fragment {

    private TextView tvTitle, tvAuthor, tvSummary, tvPage, tvLang, tvType;
    private Button btnWriteSummary;

    private String bookId;
    private Book tempBook;

    private LibraryViewModel libraryViewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.bookId = getArguments().getString("book_id");
        }

        if (getActivity().getApplication() != null) {
            libraryViewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(LibraryViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nilam_barcode_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        tvTitle = view.findViewById(R.id.tv_book_title_details_nilam);
        tvAuthor = view.findViewById(R.id.tv_book_author_details_nilam);
        tvSummary = view.findViewById(R.id.tv_book_summary_details_nilam);
        tvPage = view.findViewById(R.id.tv_book_pages_details_nilam);
        tvLang = view.findViewById(R.id.tv_book_lang_details_nilam);
        tvType = view.findViewById(R.id.tv_book_type_details_nilam);
        btnWriteSummary = view.findViewById(R.id.btn_submit_summary_nilam);
        navController = Navigation.findNavController(view);

        // setup data
        bookDetails();

        // click listener
        btnWriteSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + tempBook.getTitle());
                Nilam nilam = new Nilam(tempBook.getTitle(), tempBook.getPages(), tempBook.getAuthor(), tempBook.getGenre(), tempBook.getLang(), tempBook.getType(), "Book", "", "pending");
                Bundle bundle = new Bundle();
                bundle.putParcelable("nilam_info", nilam);
                navController.navigate(R.id.action_nilamBarcodeScannerFragment_to_nilamSummaryFragment, bundle);
            }
        });
    }

    private void bookDetails() {
        libraryViewModel.getBookDetails(bookId).observe(getActivity(), new Observer<Book>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Book book) {
                tempBook = book;
                tvTitle.setText(book.getTitle());
                tvAuthor.setText("By " +book.getAuthor());
                tvSummary.setText(book.getSummary());
                tvPage.setText("" +book.getPages());
                tvType.setText(book.getGenre());
                tvLang.setText(book.getLang());
            }
        });
    }
}