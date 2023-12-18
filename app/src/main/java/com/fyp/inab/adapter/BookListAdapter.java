package com.fyp.inab.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.inab.R;
import com.fyp.inab.object.Book;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class BookListAdapter extends FirestoreRecyclerAdapter<Book, BookListAdapter.DesignViewHolder> {

    private OnClickBookItem onClickBookItem;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookListAdapter(@NonNull FirestoreRecyclerOptions<Book> options, OnClickBookItem onClickBookItem) {
        super(options);
        this.onClickBookItem = onClickBookItem;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull DesignViewHolder holder, int position, @NonNull Book model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvAuthor.setText("By " + model.getAuthor());
        holder.tvIsAvailable.setText( model.isAvailability() ? "Available" : "Unavailable" );
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new DesignViewHolder(view, onClickBookItem);
    }

    public class DesignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle, tvAuthor, tvIsAvailable;
        OnClickBookItem onClickBookItem;

        public DesignViewHolder(@NonNull View itemView, OnClickBookItem onClickBookItem) {
            super(itemView);
            this.onClickBookItem = onClickBookItem;

            // init component
            tvTitle = itemView.findViewById(R.id.book_title);
            tvAuthor = itemView.findViewById(R.id.book_author);
            tvIsAvailable = itemView.findViewById(R.id.book_isAvailable);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickBookItem.onClickBook(
                    getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).toObject(Book.class),
                    getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).getId()
            );
        }
    }

    public interface OnClickBookItem {
        void onClickBook(Book book, String id);
    }
}
