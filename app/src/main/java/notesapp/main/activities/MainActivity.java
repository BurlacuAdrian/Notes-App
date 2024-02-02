package notesapp.main.activities;

import static notesapp.main.utils.Constants.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notesapp.main.entities.Note;
import notesapp.main.R;
import notesapp.main.adapters.NotesAdapter;

public class MainActivity extends AppCompatActivity {

    List<Note> notesList;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    int pos;

    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesList = new ArrayList<>();

        // Sample data
        notesList.add(new Note(new Date(System.currentTimeMillis() - 3 * 60 * 1000),"Today's to do list","Go grocery shopping\nDo laundry","yellow"));
        notesList.add(new Note(new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),"Plans for Saturday","Picnic","red"));


        notesRecyclerView=findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2,StaggeredGridLayoutManager.VERTICAL
                )
        );

        notesAdapter = new NotesAdapter(notesList, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here
                pos = position;
                Note clickedNote = notesList.get(position);
                // Implement your desired behavior
                Intent intent = new Intent(getApplicationContext(), ViewNote.class);
                intent.putExtra("NOTE", clickedNote);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }

            @Override
            public void onItemLongClick(int position) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notesList.remove(position);
                                notesAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Did not delete",Toast.LENGTH_LONG).show();

                            }
                        })
                        .create();
                dialog.show();

            }
        });

        notesRecyclerView.setAdapter(notesAdapter);


        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ViewNote.class);
                startActivityForResult(intent,REQUEST_CODE_ADD);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_ADD && resultCode == RESULT_OK && data !=null){
            notesList.add((Note)data.getSerializableExtra(ADD_NOTE));
            notesAdapter.notifyDataSetChanged();
            //listView.setAdapter(new CustomAdapter(getApplicationContext(),R.layout.custom_item,notes,getLayoutInflater()));

        }

        if(requestCode==REQUEST_CODE_EDIT && resultCode == RESULT_OK && data !=null){
            Note old = notesList.get(pos);
            Note newNote = (Note)data.getSerializableExtra(ADD_NOTE);
            old.setTitle(newNote.getTitle());
            old.setContent(newNote.getContent());
            old.setColor(newNote.getColor());
            notesAdapter.notifyDataSetChanged();

        }
    }

}