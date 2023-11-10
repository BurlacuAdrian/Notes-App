package notesapp.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Note> notes = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static final int REQUEST_CODE_ADD=1;
    public static final int REQUEST_CODE_EDIT=2;
    public static final String ADD_NOTE="ADD_NOTE";
    ListView listView ;
    ArrayAdapter<Note> arrayAdapter ;
    int pos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        notes.add(new Note(new Date(),"Today's to do list","Go grocery shopping\nDo laundry"));
        notes.add(new Note(new Date(),"Plans for Saturday","Picnic"));

        arrayAdapter=new ArrayAdapter<Note>(getApplicationContext(),
                android.R.layout.simple_list_item_1,notes);


        listView.setAdapter(new CustomAdapter(getApplicationContext(),R.layout.custom_item,notes,getLayoutInflater()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ViewNote.class);
                pos=position;
                intent.putExtra("NOTE",notes.get(position));
                startActivityForResult(intent,REQUEST_CODE_EDIT);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove(position);
                                listView.setAdapter(new CustomAdapter(getApplicationContext(),R.layout.custom_item,notes,getLayoutInflater()));
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

                return true;
            }
        });


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
            notes.add((Note)data.getSerializableExtra(ADD_NOTE));
            listView.setAdapter(new CustomAdapter(getApplicationContext(),R.layout.custom_item,notes,getLayoutInflater()));

        }

        if(requestCode==REQUEST_CODE_EDIT && resultCode == RESULT_OK && data !=null){
//            Toast.makeText(getApplicationContext(),((Note)data.getSerializableExtra(ADD_NOTE)).toString(),Toast.LENGTH_LONG).show();
            Note old = notes.get(pos);
            Note newNote = (Note)data.getSerializableExtra(ADD_NOTE);
            old.setTitle(newNote.getTitle());
            old.setContent(newNote.getContent());
            listView.setAdapter(new CustomAdapter(getApplicationContext(),R.layout.custom_item,notes,getLayoutInflater()));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        switch (title){
            case "Item1":
                Toast.makeText(getApplicationContext(),"Current date is : "+sdf.format(new Date()),Toast.LENGTH_LONG).show();
                break;
            case "Item2":
                break;
        }
        return true;
    }
}