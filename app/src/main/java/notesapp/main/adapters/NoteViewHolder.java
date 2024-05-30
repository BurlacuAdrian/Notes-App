package notesapp.main.adapters;

import static notesapp.main.utils.Constants.*;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import notesapp.main.entities.Note;
import notesapp.main.R;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    TextView titleTextView, contentTextView;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView=itemView.findViewById(R.id.titleTextView);
        contentTextView=itemView.findViewById(R.id.contentTextView);
    }

    void setNoteInfo(Note note){
        titleTextView.setText(note.toString());
        String noteContent = note.getContent();
        String displayedContent = null;
        if(noteContent.trim().isEmpty())
            contentTextView.setVisibility(View.GONE);
        else{
            if(noteContent.length()<=MAX_CHARACTERS_FOR_CONTENT_PREVIEW)
                displayedContent= noteContent;
            else
                displayedContent=noteContent.substring(0,MAX_CHARACTERS_FOR_CONTENT_PREVIEW)+"...";
        }
            contentTextView.setText(displayedContent);
    }
}
