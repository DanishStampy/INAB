package com.fyp.inab.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.inab.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class BorrowListAdapter extends RecyclerView.Adapter<BorrowListAdapter.DesignViewHolder>{

    private ArrayList<Map<String, Object>> requestList;
    private OnClickBorrowedItem onClickBorrowedItem;
    private int currentState;

    public final static int HISTORY = 0;
    public final static int CURRENT = 1;

    private static final int VIEW_TYPE_LIST = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public BorrowListAdapter(ArrayList<Map<String, Object>> requestList, int current, OnClickBorrowedItem onClickBorrowedItem) {
        this.requestList = requestList;
        this.currentState = current;
        this.onClickBorrowedItem = onClickBorrowedItem;
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.error_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        }

        return new DesignViewHolder(view, onClickBorrowedItem);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull DesignViewHolder holder, int position) {

        if (!requestList.isEmpty()) {

            // first letter uppercase
            String str = (String) requestList.get(position).get("status_request");
            String status = str.substring(0, 1).toUpperCase(Locale.ROOT) + (String) ((String) requestList.get(position).get("status_request")).substring(1);

            // set date
            long milis = Long.parseLong((String) requestList.get(position).get("id"));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(milis);

            if (currentState == CURRENT) {
                holder.tvDateRequest.setText("Return date: " + requestList.get(position).get("return_date"));
                if (checkCurrentDate((String) requestList.get(position).get("return_date"))) {
                    holder.tvWarning.setVisibility(View.VISIBLE);
                    holder.tvRequestStatus.setVisibility(View.INVISIBLE);
                } else {
                    holder.tvWarning.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.tvDateRequest.setText("Request date: " + format.format(date));
            }

            holder.tvTitleAuthor.setText(requestList.get(position).get("book_title") + " by " + requestList.get(position).get("book_author"));
            holder.tvRequestStatus.setText(status);

        } else {
            holder.tvErrorMessage.setText((currentState == HISTORY) ? "No borrow request history" : "No current borrow request");
        }
    }

    @SuppressLint("SimpleDateFormat")
    private boolean checkCurrentDate(String return_date) {
        String dateString = return_date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long millis = 0;

        try {
            Date date = format.parse(dateString);
            millis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis < System.currentTimeMillis();
    }

    @Override
    public int getItemViewType(int position) {
        return (requestList.size() == 0) ? VIEW_TYPE_EMPTY : VIEW_TYPE_LIST;
    }

    @Override
    public int getItemCount() {
        return (requestList.size() == 0) ? 1 : requestList.size();
    }

    public class DesignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitleAuthor, tvRequestStatus, tvDateRequest, tvWarning, tvErrorMessage;
        OnClickBorrowedItem onClickBorrowedItem;

        public DesignViewHolder(@NonNull View itemView, OnClickBorrowedItem onClickBorrowedItem) {
            super(itemView);
            this.onClickBorrowedItem = onClickBorrowedItem;

            if (!requestList.isEmpty()) {
                // init component
                tvTitleAuthor = itemView.findViewById(R.id.tv_request_title_author);
                tvRequestStatus = itemView.findViewById(R.id.tv_request_status);
                tvDateRequest = itemView.findViewById(R.id.tv_request_date);
                tvWarning = itemView.findViewById(R.id.tv_warning);

                // click listener
                itemView.setOnClickListener(this);
            } else {
                // init component
                tvErrorMessage = itemView.findViewById(R.id.tv_error_message_general);
            }
        }

        @Override
        public void onClick(View view) {
            onClickBorrowedItem.onClickBorrowedItemListener(
                    (String) requestList.get(getAbsoluteAdapterPosition()).get("book_id"),
                    (String) requestList.get(getAbsoluteAdapterPosition()).get("id"),
                    (String) requestList.get(getAbsoluteAdapterPosition()).get("book_title")
            );
        }
    }

    public interface OnClickBorrowedItem {
        void onClickBorrowedItemListener(String bookId, String requestId, String bookName);
    }
}
