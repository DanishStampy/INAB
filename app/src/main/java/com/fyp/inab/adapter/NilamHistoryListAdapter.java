package com.fyp.inab.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.inab.R;
import com.fyp.inab.object.Nilam;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NilamHistoryListAdapter extends FirestoreRecyclerAdapter<Nilam, NilamHistoryListAdapter.DesignViewHolder> {

    private final OnClickNilamItem onClickNilamItem;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NilamHistoryListAdapter(@NonNull FirestoreRecyclerOptions<Nilam> options, OnClickNilamItem onClickNilamItem) {
        super(options);
        this.onClickNilamItem = onClickNilamItem;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull DesignViewHolder holder, int position, @NonNull Nilam model) {
        holder.tvBookAuthor.setText(model.getTitle() + " by " + model.getAuthor());
        holder.tvMaterial.setText(model.getMaterial());

        String initial;
        if (model.getApproval().equals("approved")) {
            initial = "Approved on ";
            holder.tvDateApproved.setText(initial + model.getDate_approved());
        } else if (model.getApproval().equals("rejected")){
            initial = "Rejected on ";
            holder.tvDateApproved.setText(initial + model.getDate_approved());
        } else {
            holder.tvDateApproved.setText("Wait approval from teacher");
        }


    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nilam_history_item, parent, false);
        return new DesignViewHolder(view, onClickNilamItem);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class DesignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnClickNilamItem onClickNilamItem;
        TextView tvBookAuthor, tvMaterial, tvDateApproved;

        public DesignViewHolder(@NonNull View itemView, OnClickNilamItem onClickNilamItem) {
            super(itemView);
            this.onClickNilamItem = onClickNilamItem;

            // init component
            tvBookAuthor = itemView.findViewById(R.id.tv_book_title_history);
            tvMaterial = itemView.findViewById(R.id.tv_book_material_history);
            tvDateApproved = itemView.findViewById(R.id.tv_nilam_approved_history);

            // click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickNilamItem.onClickDisplayComment(
                    getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).getString("comment"),
                    getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).getString("summary"),
                    getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).getString("date_approved")
            );
        }
    }

    public interface OnClickNilamItem {
        void onClickDisplayComment(String comment, String summary, String date);
    }
}
