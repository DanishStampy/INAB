package com.fyp.inab.view.student.library.allbooks;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fyp.inab.R;
import com.fyp.inab.adapter.BookListAdapter;
import com.fyp.inab.object.Book;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BookListFragment extends Fragment implements BookListAdapter.OnClickBookItem {

    private FirebaseFirestore firestore;
    private CollectionReference bookRef;

    private RecyclerView rcBookList;
    private EditText etQueryBook;
    private ImageButton btnSearchQuery;

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init firebase
        firestore = FirebaseFirestore.getInstance();
        bookRef = firestore.collection("books");

        // init component
        rcBookList = view.findViewById(R.id.rc_book_list);
        etQueryBook = view.findViewById(R.id.et_book_query);
        btnSearchQuery = view.findViewById(R.id.btn_submit_query_book);
        navController = Navigation.findNavController(view);

        // recycler view
        setupRecyclerView();

        // init query
        Query query = bookRef;
        queryBooks(query);

        // click listener
        btnSearchQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookQuery  = etQueryBook.getText().toString().trim();

                Query query = bookRef.orderBy("title").startAt(bookQuery).endAt(bookQuery+'\uf8ff');
                queryBooks(query);
            }
        });

    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcBookList.setLayoutManager(linearLayoutManager);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void queryBooks(Query query) {
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        BookListAdapter bookListAdapter = new BookListAdapter(options, this);
        rcBookList.setAdapter(bookListAdapter);
        bookListAdapter.startListening();
        bookListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickBook(Book book, String id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("book_details", book);
        bundle.putString("book_id", id);
        navController.navigate(R.id.action_bookListFragment_to_bookDetailsFragment, bundle);
    }
}