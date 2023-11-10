package notesapp.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class ViewNote extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        EditText editTextContent=findViewById(R.id.editTextContent);
        TextView textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(new Date().toString());

        Intent intent = getIntent();

        if(intent.hasExtra("NOTE")){
            Note note =(Note) intent.getSerializableExtra("NOTE");
            editTextTitle.setText(note.getTitle());
            editTextContent.setText(note.getContent());
            textViewDate.setText(note.getDate().toString());
        }

        Button button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(MainActivity.ADD_NOTE,
                        new Note(new Date(), editTextTitle.getText().toString(),
                                editTextContent.getText().toString()));
            setResult(RESULT_OK,intent);
            finish();

            }
        });





    }
}