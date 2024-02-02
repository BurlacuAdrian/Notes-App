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

import java.util.ArrayList;
import java.util.List;

import notesapp.main.entities.Note;
import notesapp.main.R;
import notesapp.main.adapters.NotesAdapter;
import notesapp.main.roomdb.NotesDAO;
import notesapp.main.roomdb.NotesDB;

public class MainActivity extends AppCompatActivity {

    List<Note> notesList;
    int pos;

    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;

    NotesDB db = null;
    NotesDAO dao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = NotesDB.getInstance(getApplicationContext());
        dao = db.getNotesDAO();

        notesList = new ArrayList<>();
        notesList.addAll(dao.getAll());


        notesRecyclerView=findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2,StaggeredGridLayoutManager.VERTICAL
                )
        );

        notesAdapter = new NotesAdapter(notesList, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                pos = position;
                Note clickedNote = notesList.get(position);
                Intent intent = new Intent(getApplicationContext(), ViewNote.class);
                intent.putExtra("NOTE", clickedNote);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }

            @Override
            public void onItemLongClick(int position) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", (dialog1, which) -> {
                            Note noteToBeDeleted = notesList.get(position);
                            dao.delete(noteToBeDeleted);
                            notesList.remove(position);
                            notesAdapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("No", (dialog12, which) ->
                                Toast.makeText(getApplicationContext(),"Did not delete",Toast.LENGTH_LONG).show())
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
            Note noteToBeAdded = (Note)data.getSerializableExtra(ADD_NOTE);
            int position = notesList.size();
            notesList.add(noteToBeAdded);
            dao.insert(noteToBeAdded);
            notesAdapter.notifyItemInserted(position);
        }

        if(requestCode==REQUEST_CODE_EDIT && resultCode == RESULT_OK && data !=null){
            Note old = notesList.get(pos);
            Note newNote = (Note)data.getSerializableExtra(ADD_NOTE);
            old.setTitle(newNote.getTitle());
            old.setContentAndHash(newNote.getContent());
            old.setColor(newNote.getColor());
            dao.update(old);
            notesAdapter.notifyItemRemoved(pos);

        }
    }

}