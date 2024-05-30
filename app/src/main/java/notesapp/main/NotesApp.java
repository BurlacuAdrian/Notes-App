package notesapp.main;

import android.app.Application;

public class NotesApp extends Application {

    private static NotesApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static NotesApp getInstance() {
        return instance;
    }
}
