package notesapp.main.activities;

import static notesapp.main.utils.Constants.ADD_NOTE;
import static notesapp.main.utils.Constants.REQUEST_CODE_ADD;
import static notesapp.main.utils.Constants.REQUEST_CODE_EDIT;
import static notesapp.main.utils.Constants.REQUEST_CODE_LOGIN;
import static notesapp.main.utils.Constants.REQUEST_CODE_SIGNUP;
import static notesapp.main.utils.UtilityFunctions.showToast;
import static notesapp.main.utils.UtilityFunctions.singleItemList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import notesapp.main.NotesApp;
import notesapp.main.R;
import notesapp.main.adapters.NotesAdapter;
import notesapp.main.apiclient.DataManager;
import notesapp.main.apiclient.TokenManager;
import notesapp.main.apiclient.responses.NotesResponse;
import notesapp.main.entities.Note;
import notesapp.main.roomdb.NotesDAO;
import notesapp.main.roomdb.NotesDB;

public class MainActivity extends AppCompatActivity {

    private static List<Note> notesList;
    int pos;

    private RecyclerView notesRecyclerView;
    private static NotesAdapter notesAdapter;

    NotesDB db = null;
    private static NotesDAO dao = null;

    private static boolean offline = true;

    static String authHeader = null;
    private DataManager dataManager;

    static Button offlineStateButton;
    static TextView offlineStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        db = NotesDB.getInstance(getApplicationContext());
        dao = db.getNotesDAO();

//        dao.deleteAll();

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

                            List<String> notesToBeDeleted = new ArrayList<>();
                            notesToBeDeleted.add(noteToBeDeleted.getUuId());
                            dataManager.deleteNotes(notesToBeDeleted);

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

        offlineStateButton = findViewById(R.id.offlineStateButton);
        offlineStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCredentials();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivityForResult(intent,REQUEST_CODE_LOGIN);
            }
        });

        offlineStateTextView = findViewById(R.id.offlineStateTextView);

        initCloudConnection();

        dataManager = new DataManager(MainActivity.this);
        dataManager.resetAuthHeader();

        // Begin cloud sync
        if(!offline)
            dataManager.fetchIdsAndHashes();
    }

    public static void resetCredentials(){
        TokenManager tokenManager = new TokenManager(NotesApp.getInstance().getApplicationContext());
        tokenManager.clearToken();
        tokenManager.clearUsername();
        offline=true;
        offlineStateButton.setText("Sign in");
        offlineStateTextView.setText("Guest/offline mode");


        notesList = new ArrayList<>();
        notesList.addAll(dao.getAll());
        notesAdapter.notifyDataSetChanged();
    }

    public static void initCloudConnection(){
        TokenManager tokenManager = new TokenManager(NotesApp.getInstance().getApplicationContext());
        String token = tokenManager.getToken();
        String username = tokenManager.getUsername();

        if(token == null){
            offline=true;
            offlineStateButton.setText("Sign in");
            offlineStateTextView.setText("Guest/offline mode");
        } else{
            offlineStateButton.setText("Sign out");
            offlineStateTextView.setText("Signed in as : "+username);
            offline=false;
            authHeader = "Bearer "+token;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_ADD && resultCode == RESULT_OK && data !=null){
            Note noteToBeAdded = (Note)data.getSerializableExtra(ADD_NOTE);
            Note newNote=new Note(noteToBeAdded);
            int position = notesList.size();
            notesList.add(newNote);

            dataManager.uploadNotes(singleItemList(newNote));
            notesAdapter.notifyItemInserted(position);
        }

        if(requestCode==REQUEST_CODE_EDIT && resultCode == RESULT_OK && data !=null){
            Note newNoteData = (Note)data.getSerializableExtra(ADD_NOTE);
            Note inMemoryNote = getNoteFromMainListByUuId(newNoteData.getUuId());
            if(inMemoryNote == null)
                return;
            inMemoryNote.setTitle(newNoteData.getTitle());
            inMemoryNote.setContent(newNoteData.getContent());
            inMemoryNote.setColor(newNoteData.getColor());
            inMemoryNote.updateHash();

            dataManager.updateNotes(singleItemList(inMemoryNote));
            notesAdapter.notifyItemChanged(pos);
        }

        if(requestCode==REQUEST_CODE_LOGIN && resultCode == RESULT_OK){
            initCloudConnection();
            dataManager.resetAuthHeader();
            List<Note> tempNotes = new ArrayList<>();
            tempNotes.addAll(dao.getAll());
            dao.deleteAll();
            notesList.clear();

            notesList.addAll(tempNotes);
            dao.insert(tempNotes);
            notesAdapter.notifyDataSetChanged();
            dataManager.fetchIdsAndHashes();
        }

    }

    public static Note getNoteFromMainListByUuId(String UuId){
        for(Note note : notesList){
            if(note!=null)
                if(Objects.equals(note.getUuId(), UuId))
                    return note;
        }
        return null;
    }

    public static NotesAdapter getNotesAdapter(){
        return notesAdapter;
    }

    public static List<Note> getNotesList() {
        return notesList;
    }

    public static boolean isOffline(){
        return offline;
    }

    public static NotesDAO getDao(){
        return dao;
    }

    public static String getAuthHeader(){
        return authHeader;
    }

}