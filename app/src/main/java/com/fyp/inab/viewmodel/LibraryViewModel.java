package com.fyp.inab.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.Book;
import com.fyp.inab.repository.LibraryRepo;

import java.util.ArrayList;
import java.util.Map;

public class LibraryViewModel extends AndroidViewModel {

    private LibraryRepo repository;

    public LibraryViewModel(@NonNull Application application) {
        super(application);

        repository = new LibraryRepo(application);
    }

    public void sendBookBorrowRequest(String bookId, String bookTitle) {
        repository.sendRequestBorrowBook(bookId, bookTitle);
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getHistoryList() {
        return repository.getHistoryBorrowBook();
    }

    public MutableLiveData<Book> getBookDetails (String id) {
        return repository.getBookDetails(id);
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getCurrentBorrowedList() {
        return repository.getRequestCurrentBorrowBook();
    }

    public void returnBook (String bookId, String requestId) {
        repository.updateBookAvailability(bookId, requestId);
    }
}
