package com.fyp.inab.view.student.library.allbooks;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.inab.R;
import com.fyp.inab.object.Book;
import com.fyp.inab.viewmodel.LibraryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookDetailsFragment extends Fragment {

    private TextView tvTitle, tvAuthor, tvSummary, tvPages, tvType, tvLang;
    private ImageView imgViewBookAvailability;
    private Button btnBorrowBook;
    private ToolTipsManager toolTipsManager;
    private ConstraintLayout constraintLayout;
    private MaterialAlertDialogBuilder builder;
    private NavController navController;

    private LibraryViewModel libraryViewModel;

    private Book book;
    private String bookId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.book = getArguments().getParcelable("book_details");
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
        return inflater.inflate(R.layout.fragment_book_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        tvTitle = view.findViewById(R.id.tv_book_title_details);
        tvAuthor = view.findViewById(R.id.tv_book_author_details);
        tvSummary = view.findViewById(R.id.tv_book_summary_details);
        tvPages = view.findViewById(R.id.tv_book_pages_details);
        tvType = view.findViewById(R.id.tv_book_type_details);
        tvLang = view.findViewById(R.id.tv_book_lang_details);
        imgViewBookAvailability = view.findViewById(R.id.imgView_book_availability);
        btnBorrowBook = view.findViewById(R.id.btn_borrow_book);
        constraintLayout = view.findViewById(R.id.constrainLayout_book_details);
        toolTipsManager = new ToolTipsManager();
        navController = Navigation.findNavController(view);

        // setup data
        bookDetails();

        // hide button borrow
        btnBorrowBook.setVisibility(!book.isAvailability() ? View.INVISIBLE : View.VISIBLE);

        // click listener
        btnBorrowBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = getReturnDate();
                builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("Borrow Request")
                        .setMessage(book.getTitle() + " will expected return before/during " + date)
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                libraryViewModel.sendBookBorrowRequest(bookId, book.getTitle());
                                Toast.makeText(getActivity(), "Request borrow sent", Toast.LENGTH_SHORT).show();
                                navController.popBackStack(R.id.bookListFragment, false);
                            }
                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {dialogInterface.dismiss();})
                        .show();
            }
            // get date for 14 days from now
            @SuppressLint("SimpleDateFormat")
            private String getReturnDate() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 14);
                Date newDate = calendar.getTime();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                return format.format(newDate);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bookDetails() {
        tvTitle.setText(book.getTitle());
        tvAuthor.setText("By " +book.getAuthor());
        tvSummary.setText(book.getSummary());
        tvPages.setText("" +book.getPages());
        tvType.setText(book.getGenre());
        tvLang.setText(book.getLang());

        if (book.isAvailability()) {
            imgViewBookAvailability.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);
            // tooltip click
            tooltipClick("This book is available");

        } else {
            imgViewBookAvailability.setBackgroundResource(R.drawable.ic_baseline_error_24);
            // tooltip click
            tooltipClick("This book unavailable at the moment!");

        }
    }

    private void tooltipClick(String msg) {
        // tooltip design
        ToolTip.Builder builder = new ToolTip.Builder(
                getContext(),
                imgViewBookAvailability,
                constraintLayout,
                msg,
                ToolTip.POSITION_ABOVE
        )
                .setTextAppearance(R.style.TooltipTextAppearance)
                .setBackgroundColor(getResources().getColor(R.color.cadet));

        imgViewBookAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolTipsManager.show(builder.build());
            }
        });
    }
}