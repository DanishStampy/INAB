package com.fyp.inab.view.student.library.borrowed;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fyp.inab.R;
import com.fyp.inab.adapter.BookFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookBorrowedListFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               navController = Navigation.findNavController(getView());
               navController.navigate(R.id.action_bookBorrowedListFragment_to_libraryHomeFragment);
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_borrowed_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init component
        tabLayout = view.findViewById(R.id.tabLayoutBook);
        viewPager2 = view.findViewById(R.id.viewPagerBook);
        swipeRefreshLayout = view.findViewById(R.id.swipeFreshLayout);
        navController = Navigation.findNavController(view);

        // init and add fragment inside adapter
        BookFragmentAdapter bookFragmentAdapter = new BookFragmentAdapter(getParentFragmentManager(), getLifecycle());
        bookFragmentAdapter.addFragment(new BookHistoryRequestFragment());
        bookFragmentAdapter.addFragment(new BookCurrentBorrowedFragment());

        // config viewpager2
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setAdapter(bookFragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("History");
                    break;
                case 1:
                    tab.setText("Current Borrowed");
                    break;
            }
        })).attach();

        // refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
           navController.navigate(R.id.action_bookBorrowedListFragment_self);
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}