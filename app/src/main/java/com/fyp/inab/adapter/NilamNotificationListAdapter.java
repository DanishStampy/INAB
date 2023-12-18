package com.fyp.inab.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.inab.R;

import java.util.ArrayList;
import java.util.Map;

public class NilamNotificationListAdapter extends RecyclerView.Adapter<NilamNotificationListAdapter.DesignViewHolder> {

    private ArrayList<Map<String, Object>> notificationList;
    private OnClickNotificationItem onClickNotificationItem;

    private static final int VIEW_TYPE_LIST = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public NilamNotificationListAdapter(ArrayList<Map<String, Object>> notificationList, OnClickNotificationItem onClickNotificationItem) {
        this.notificationList = notificationList;
        this.onClickNotificationItem = onClickNotificationItem;
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.error_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        }
        return new DesignViewHolder(view, onClickNotificationItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DesignViewHolder holder, int position) {
        if (!notificationList.isEmpty()) {
            holder.tvDate.setText((CharSequence) notificationList.get(position).get("notification_date"));
            holder.tvSender.setText((CharSequence) notificationList.get(position).get("sender"));
            holder.tvBookTitle.setText(notificationList.get(position).get("title") + " by " + notificationList.get(position).get("author"));
        } else {
            holder.tvErrorMessage.setText("No notification yet");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (notificationList.size() == 0) ? VIEW_TYPE_EMPTY : VIEW_TYPE_LIST;
    }

    @Override
    public int getItemCount() {
        return (notificationList.size() == 0) ? 1 : notificationList.size();
    }

    public class DesignViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        TextView tvSender, tvDate, tvBookTitle, tvErrorMessage;
        OnClickNotificationItem onClickNotificationItem;

        public DesignViewHolder(@NonNull View itemView, OnClickNotificationItem onClickNotificationItem) {
            super(itemView);
            this.onClickNotificationItem = onClickNotificationItem;

            if (!notificationList.isEmpty()) {
                // init component
                tvSender = itemView.findViewById(R.id.tv_sender_notification);
                tvDate = itemView.findViewById(R.id.tv_date_nilam_notification);
                tvBookTitle = itemView.findViewById(R.id.tv_book_title_notification);

                // click listener
                itemView.setOnClickListener(this);
            } else {
                // init component
                tvErrorMessage = itemView.findViewById(R.id.tv_error_message_general);
            }
        }

        @Override
        public void onClick(View view) {
            onClickNotificationItem.onClickNotification(
                    (String) notificationList.get(getAbsoluteAdapterPosition()).get("path"),
                    (String) notificationList.get(getAbsoluteAdapterPosition()).get("class_id"),
                    (String) notificationList.get(getAbsoluteAdapterPosition()).get("id"),
                    (String) notificationList.get(getAbsoluteAdapterPosition()).get("sender")
            );
        }
    }

    public interface OnClickNotificationItem {
        void onClickNotification(String path, String classId, String notiId, String sender);
    }
}
