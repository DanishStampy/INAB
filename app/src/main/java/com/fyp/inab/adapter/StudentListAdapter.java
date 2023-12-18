package com.fyp.inab.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.inab.R;
import com.fyp.inab.object.User;

import java.util.ArrayList;
import java.util.Map;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.DesignViewHolder> {

    private ArrayList<Map<String, Object>> studentList;
    private OnClickStudentItem onClickStudentItem;

    private static final int VIEW_TYPE_LIST = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public StudentListAdapter(ArrayList<Map<String, Object>> studentList, OnClickStudentItem onClickStudentItem) {
        this.studentList = studentList;
        this.onClickStudentItem = onClickStudentItem;
        Log.d(TAG, "StudentListAdapter: " + studentList.size());
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.error_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        }
        return new DesignViewHolder(view, onClickStudentItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DesignViewHolder holder, int position) {
        if (!studentList.isEmpty()) {
            User user = (User) studentList.get(position).get("user");
            holder.tvFullName.setText(user.getFullName());
            holder.tvEmail.setText(user.getEmail());
            holder.tvPhoneNumber.setText("+60" + user.getPhoneNum());
        } else {
            holder.tvErrorMessage.setText("No student in this class yet");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (studentList.size() == 0) ? VIEW_TYPE_EMPTY : VIEW_TYPE_LIST;
    }

    @Override
    public int getItemCount() {
        return (studentList.size() == 0) ? 1 : studentList.size();
    }

    public class DesignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvFullName, tvEmail, tvPhoneNumber, tvErrorMessage;
        OnClickStudentItem onClickStudentItem;

        public DesignViewHolder(@NonNull View itemView, OnClickStudentItem onClickStudentItem) {
            super(itemView);

            this.onClickStudentItem = onClickStudentItem;

            if (!studentList.isEmpty()) {
                // init component
                tvFullName = itemView.findViewById(R.id.tv_student_name);
                tvEmail = itemView.findViewById(R.id.tv_student_email);
                tvPhoneNumber = itemView.findViewById(R.id.tv_student_phoneNum);

                // click listener
                itemView.setOnClickListener(this);
            } else {
                // init component
                tvErrorMessage = itemView.findViewById(R.id.tv_error_message_general);
            }
        }

        @Override
        public void onClick(View view) {
            onClickStudentItem.onClickStudent(
                    (User) studentList.get(getAbsoluteAdapterPosition()).get("user"),
                    (String) studentList.get(getAbsoluteAdapterPosition()).get("id")
            );
        }
    }

    public interface OnClickStudentItem {
        void onClickStudent(User user, String id);
    }
}
