package notesapp.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;


public class CustomAdapter extends ArrayAdapter<Note> {

    private Context context;
    private int resource;
    private List<Note> noteList;
    LayoutInflater layoutInflater;


    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects, LayoutInflater layoutInflater) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.noteList=objects;
        this.layoutInflater=layoutInflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(resource,parent,false);
        Note note = noteList.get(position);
        if(note!=null){
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");
            TextView customItemTitle=view.findViewById(R.id.customItemTitle);
            customItemTitle.setText(note.getTitle()+" on "+sdf.format(note.getDate()));
            TextView customItemContent=view.findViewById(R.id.customItemContent);
            customItemContent.setText(note.getContent());
        }

        return view;
    }
}
