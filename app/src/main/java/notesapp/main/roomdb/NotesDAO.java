package notesapp.main.roomdb;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import notesapp.main.entities.Note;

@Dao
public interface NotesDAO {

    @Insert
    void insert(Note note);

    @Insert
    void insert(List<Note> notesList);

    @Query("select * from notes")
    public List<Note> getAll();

    @Delete
    void delete(Note note);

    @Query("delete from notes")
    void deleteAll();

    @Update
    void update(Note note);
}
