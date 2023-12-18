package com.fyp.inab.repository;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.Book;
import com.fyp.inab.object.RequestBook;
import com.fyp.inab.object.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LibraryRepo {

    private Application application;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private CollectionReference requestList, bookRef;

    private MutableLiveData<ArrayList<Map<String, Object>>> historyRequestBookListData;
    private MutableLiveData<ArrayList<Map<String, Object>>> currentRequestBookListData;
    private MutableLiveData<Book> bookData;

    private ArrayList<Map<String, Object>> historyRequestBookList;
    private ArrayList<Map<String, Object>> currentRequestBookList;

    public LibraryRepo(Application application) {
        this.application = application;

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = auth.getCurrentUser();
        requestList = firestore.collection("requestList");
        bookRef = firestore.collection("books");

        // init mutable livedata
        historyRequestBookListData = new MutableLiveData<>();
        currentRequestBookListData = new MutableLiveData<>();
        bookData = new MutableLiveData<>();
    }

    public void sendRequestBorrowBook(String bookId, String bookTitle) {

        String date = Util.get14DaysReturnDate();
        RequestBook requestBook = new RequestBook(auth.getUid(), firebaseUser.getDisplayName(), bookId, bookTitle, "pending", date);

        // update availability of book
        Map<String, Object> updateBook = new HashMap<>();
        updateBook.put("availability", false);

        bookRef.document(bookId)
                .update(updateBook)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: update book!");
                        }
                    }
                });

        // send borrow request
        requestList.document(System.currentTimeMillis() + "")
                .set(requestBook)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: request sent");
                        }
                    }
                });
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getHistoryBorrowBook() {

        historyRequestBookList = new ArrayList<>();

        requestList.whereEqualTo("student_id", auth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() == 0) {
                                historyRequestBookListData.postValue(historyRequestBookList);
                            } else {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String bookId = documentSnapshot.getString("book_id");

                                    Map<String, Object> requestInfo = new HashMap<>();
                                    requestInfo.put("return_date", documentSnapshot.getString("return_date"));
                                    requestInfo.put("status_request", documentSnapshot.getString("status_request"));
                                    requestInfo.put("id", documentSnapshot.getId());
                                    requestInfo.put("book_id", documentSnapshot.getString("book_id"));


                                    bookRef.document(bookId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot docs = task.getResult();
                                                        requestInfo.put("book_title", docs.getString("title"));
                                                        requestInfo.put("book_author", docs.getString("author"));

                                                        historyRequestBookList.add(requestInfo);
                                                        historyRequestBookListData.postValue(historyRequestBookList);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

        return historyRequestBookListData;
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getRequestCurrentBorrowBook() {

        currentRequestBookList = new ArrayList<>();

        requestList.whereEqualTo("student_id", auth.getUid())
                .whereEqualTo("status_request", "approved")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() == 0) {
                                currentRequestBookListData.postValue(currentRequestBookList);
                            } else {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String bookId = documentSnapshot.getString("book_id");

                                    Map<String, Object> requestInfo = new HashMap<>();
                                    requestInfo.put("return_date", documentSnapshot.getString("return_date"));
                                    requestInfo.put("status_request", "approved");
                                    requestInfo.put("id", documentSnapshot.getId());
                                    requestInfo.put("book_id", documentSnapshot.getString("book_id"));

                                    bookRef.document(bookId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot docs = task.getResult();
                                                        requestInfo.put("book_title", docs.getString("title"));
                                                        requestInfo.put("book_author", docs.getString("author"));

                                                        currentRequestBookList.add(requestInfo);
                                                        currentRequestBookListData.postValue(currentRequestBookList);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

        return currentRequestBookListData;
    }

    public MutableLiveData<Book> getBookDetails (String bookId) {
        bookRef.document(bookId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            bookData.postValue(task.getResult().toObject(Book.class));
                        }
                    }
                });
        return bookData;
    }

    public void updateBookAvailability (String bookId, String requestId) {

        Map<String, Object> updateBook = new HashMap<>();
        updateBook.put("availability", true);

        bookRef.document(bookId)
                .update(updateBook)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: book returned");
                    }
                });

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("status_request", "returned");

        requestList.document(requestId)
                .update(updateRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: update request");
                    }
                });

    }
}
