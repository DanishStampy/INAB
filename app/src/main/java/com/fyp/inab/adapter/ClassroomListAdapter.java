package com.fyp.inab.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.inab.R;
import com.fyp.inab.object.Classroom;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ClassroomListAdapter extends FirestoreRecyclerAdapter<Classroom, ClassroomListAdapter.DesignViewHolder> {

    private OnClickClassRoomItem onClickClassRoomItem;
    private int TYPE_VIEW;

    /*
    TYPE_VIEW:
    0 = NILAM
    1 = CLASSROOM LIST
     */

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ClassroomListAdapter(@NonNull FirestoreRecyclerOptions<Classroom> options, OnClickClassRoomItem onClickClassRoomItem, int typeView) {
        super(options);
        this.onClickClassRoomItem = onClickClassRoomItem;
        this.TYPE_VIEW = typeView;
    }

    @Override
    protected void onBindViewHolder(@NonNull DesignViewHolder holder, int position, @NonNull Classroom model) {
        holder.imgBtnDelete.setVisibility((TYPE_VIEW == 1) ? View.VISIBLE : View.GONE);
        holder.tvClassTitle.setText(model.getClassTitle());
        holder.tvClassDescription.setText(model.getClassDescription());
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classroom_item, parent, false);
        return new DesignViewHolder(view, onClickClassRoomItem);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.d("roomList", "onError: " + e.getMessage());
    }

    public class DesignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvClassTitle, tvClassDescription;
        ImageButton imgBtnDelete;
        OnClickClassRoomItem onClickClassRoomItem;

        public DesignViewHolder(@NonNull View itemView, OnClickClassRoomItem onClickClassRoomItem) {
            super(itemView);

            this.onClickClassRoomItem = onClickClassRoomItem;

            // init component
            tvClassTitle = itemView.findViewById(R.id.tv_class_title);
            tvClassDescription = itemView.findViewById(R.id.tv_class_description);
            imgBtnDelete = itemView.findViewById(R.id.imgBtn_delete_classroom);

            // click listener
            itemView.setOnClickListener(this);
            imgBtnDelete.setOnClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            Classroom classroom = getSnapshots().getSnapshot(position).toObject(Classroom.class);

            switch (view.getId()) {
                case R.id.imgBtn_delete_classroom:
                    onClickClassRoomItem.onClickDeleteClassroom(
                            getSnapshots().getSnapshot(position).getId(),
                            classroom.getTeacherId()
                    );
                    break;

                default:
                    onClickClassRoomItem.onClickClassroom(
                            classroom,
                            getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).getId(),
                            classroom.getTeacherId()
                    );
                    break;
            }
        }
    }

    public interface OnClickClassRoomItem {
        void onClickClassroom(Classroom classroom, String id, String teacherId);
        void onClickDeleteClassroom(String id, String teacherId);
    }
}
