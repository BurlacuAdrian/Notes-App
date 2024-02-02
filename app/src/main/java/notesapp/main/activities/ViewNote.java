package notesapp.main.activities;

import static notesapp.main.utils.Constants.*;
import static notesapp.main.utils.UtilityFunctions.formatDateWithSuffix;
import static notesapp.main.utils.UtilityFunctions.getTimeAgo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import notesapp.main.entities.Note;
import notesapp.main.R;

public class ViewNote extends AppCompatActivity {

    private String currentSelectedColor = "None";
    View selectedColorView = null;
    ImageView selectedColorCheck = null;
    List<ImageView> checkedOptions;

    EditText editTextTitle, editTextContent;
    TextView textViewDate,lastEditedTextView;
    Note note=null;
    Intent intent = null;
    boolean changesOccured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent=findViewById(R.id.editTextContent);

        intent = getIntent();

        if(intent.hasExtra("NOTE")){
            note =(Note) intent.getSerializableExtra("NOTE");
            editTextTitle.setText(note.getTitle());
            editTextContent.setText(note.getContent());
//            textViewDate.setText(note.getDate().toString());
            lastEditedTextView = findViewById(R.id.lastEditedTextView);
            lastEditedTextView.setVisibility(View.VISIBLE);
            lastEditedTextView.setText("Last edited : "+getTimeAgo(note.getLastEdited()));
            String colorName = note.getColor();
            switch (colorName){
                case "red":
                    changeColorIndicator(R.drawable.color_indicator_red);
                    break;
                case "blue":
                    changeColorIndicator(R.drawable.color_indicator);
                    break;
                case "yellow":
                    changeColorIndicator(R.drawable.color_indicator_yellow);
                    break;
            }

        }

        ImageButton floppyDiskImageButton = findViewById(R.id.floppyDiskImageButton);

        floppyDiskImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();

            }
        });

        ImageView backArrowImageView = findViewById(R.id.backArrowImageView);

        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackButtonAction();
            }
        });

        getOnBackPressedDispatcher().addCallback(ViewNote.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButtonAction();
            }
        });

        initializeNoteMenu();

        View optionYellow = findViewById(R.id.optionYellow),
                optionRed = findViewById(R.id.optionRed),
                optionBlue = findViewById(R.id.optionBlue);

        ImageView checkedOptionYellow = findViewById(R.id.checkedOptionYellow),
                checkedOptionRed = findViewById(R.id.checkedOptionRed),
                checkedOptionBlue = findViewById(R.id.checkedOptionBlue);

        checkedOptions = new ArrayList<>(Arrays.asList(checkedOptionYellow, checkedOptionBlue, checkedOptionRed));

        setColorOptionBehavior(optionYellow,checkedOptionYellow, R.drawable.color_indicator_yellow, "yellow");
        setColorOptionBehavior(optionRed,checkedOptionRed, R.drawable.color_indicator_red, "red");
        setColorOptionBehavior(optionBlue,checkedOptionBlue, R.drawable.color_indicator, "blue");

    }

    private void changeColorIndicator(int colorId){
        ImageView colorIndicatorImageView = findViewById(R.id.colorIndicatorImageView);
        Drawable colorDrawable = getDrawable(colorId);
        colorIndicatorImageView.setImageDrawable(colorDrawable);
    }

    private void setColorOptionBehavior(View colorOption, ImageView colorCheck, int colorId, String colorName){
        colorOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for ( ImageView imageView : checkedOptions){
                    imageView.setVisibility(View.INVISIBLE);
                }
                colorCheck.setVisibility(View.VISIBLE);

                changeColorIndicator(colorId);
                note.setColor(colorName);
                changesOccured=true;

            }
        });
    }

    private void saveNote(){
        if(intent==null)
            return;

        Note returnedNote;

        if(note==null){
            returnedNote=new Note();
            returnedNote.setDate(new Date());
            returnedNote.setLastEdited(note.getDate());
            returnedNote.setColor("blue");
        }
        else
            returnedNote=note;

        returnedNote.setTitle(editTextTitle.getText().toString());
        returnedNote.setContentAndHash(editTextContent.getText().toString());
        returnedNote.setLastEdited(new Date());

        intent.putExtra(ADD_NOTE,returnedNote);

        setResult(RESULT_OK,intent);
        finish();
    }

    private void handleBackButtonAction() {
        if(note != null){
            if(changesOccured || note.hasContentChanged(editTextContent.getText().toString())){
                AlertDialog dialog = new AlertDialog.Builder(ViewNote.this)
                        .setTitle("Confirm action")
                        .setMessage("You have unsaved changes. Choose what to do with the current note:")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveNote();
                            }
                        })
                        .setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }

                        })
                        .create();
                dialog.show();
            }else
                finish();
        }else{
            AlertDialog dialog = new AlertDialog.Builder(ViewNote.this)
                    .setTitle("Confirm action")
                    .setMessage("You have unsaved changes. Choose what to do with the current note:")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                        }
                    })
                    .setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }

                    })
                    .create();
            dialog.show();
        }

    }

    // Default behaviour is swipe only, this allows clicking as well
    private void initializeNoteMenu(){
        final LinearLayout noteOptionsLayout = findViewById(R.id.noteOptionsLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(noteOptionsLayout);
        noteOptionsLayout.findViewById(R.id.clickableOptionsDragBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        if(note!=null){
            TextView createdOnTextView = findViewById(R.id.createdOnTextView);
            SimpleDateFormat dateFormat = new SimpleDateFormat("d 'of' MMMM, yyyy", Locale.getDefault());
            createdOnTextView.setText("Created on : "+formatDateWithSuffix(note.getDate()));
        }

    }

    private void setNoteColor(){

    }
}