package notesapp.main.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import notesapp.main.entities.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class NotesDB extends RoomDatabase {

    public static final String DB_NAME = "notes.db";

    private static NotesDB instance;

    public static synchronized NotesDB getInstance(Context context) {
        if(instance==null)
            instance = Room.databaseBuilder(context, NotesDB.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }

    public abstract NotesDAO getNotesDAO();
}
