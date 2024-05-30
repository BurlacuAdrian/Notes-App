package notesapp.main.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import notesapp.main.entities.Note;
import notesapp.main.R;

public class NotesAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private List<Note> notes;
    private OnItemClickListener onItemClickListener;
//    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public NotesAdapter(List<Note> notes, OnItemClickListener onItemClickListener) {
        this.notes = notes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_recycler_container, parent, false);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNoteInfo(notes.get(position));

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(clickedPosition);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int longClickedPosition = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(longClickedPosition);
                }
                return true; // Returning true indicates that the long click event is consumed
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
