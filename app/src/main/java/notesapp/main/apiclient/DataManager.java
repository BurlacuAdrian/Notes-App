package notesapp.main.apiclient;

import static notesapp.main.activities.MainActivity.getNoteFromMainListByUuId;
import static notesapp.main.utils.UtilityFunctions.createJsonString;
import static notesapp.main.utils.UtilityFunctions.showToast;
import static notesapp.main.utils.UtilityFunctions.singleItemList;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import notesapp.main.activities.MainActivity;
import notesapp.main.apiclient.requests.DeleteNotesRequest;
import notesapp.main.apiclient.responses.FullNotesResponse;
import notesapp.main.apiclient.responses.NotesResponse;
import notesapp.main.entities.Note;
import notesapp.main.roomdb.NotesDAO;
import notesapp.main.roomdb.NotesDB;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataManager {

    private final APIService apiService = APIClient.getClient().create(APIService.class);
    private static NotesDAO dao = MainActivity.getDao();
    private String authHeader=MainActivity.getAuthHeader();
    private Context context;

    public DataManager(Context context){
        this.context = context;
    }

    public void resetAuthHeader(){
        authHeader=MainActivity.getAuthHeader();
    }


    public void updateNotes(List<Note> notesToBeUpdated){

        if(notesToBeUpdated.size()==0)
            return;

        for(Note note : notesToBeUpdated)
            dao.update(note);

        if(MainActivity.isOffline())
            return;

        Retrofit retrofit = APIClient.getClient();
        APIService apiService = retrofit.create(APIService.class);

        String json = createJsonString(notesToBeUpdated);
        Log.i("JSON",json);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Call<ResponseBody> call = apiService.updateNotes(authHeader, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                } else {
                    showToast("Error while updating notes");
                    Log.d("Fetch",response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Network error while updating notes");
                t.printStackTrace();
            }
        });
    }

    public void uploadNotes(List<Note> notesToBeUploaded){

        if(notesToBeUploaded.size()==0)
            return;

        for(Note note : notesToBeUploaded)
            try {
                dao.insert(note);
            }catch (Exception e){
                showToast("Error while uploading note "+note.getTitle());
            }


        if(MainActivity.isOffline())
            return;

        Retrofit retrofit = APIClient.getClient();
        APIService apiService = retrofit.create(APIService.class);

        String json = createJsonString(notesToBeUploaded);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Call<ResponseBody> call = apiService.postNotes(authHeader, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                } else {
                    showToast("Error while uploading notes");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Network error while uploading notes");
                t.printStackTrace();
            }
        });
    }

    public void deleteNotes(List<String> uuids){

        if(uuids.size()==0)
            return;

        for(String uuid : uuids){
            Note foundNote = getNoteFromMainListByUuId(uuid);
            if(foundNote!=null)
                dao.delete(foundNote);
        }

        if(MainActivity.isOffline())
            return;

        StringBuilder queryBuilder = new StringBuilder();

        if (uuids.size() > 0) {
            queryBuilder.append("notes?");
            for (String uuid : uuids) {
                queryBuilder.append("uuid=").append(uuid).append("&");
            }
            // Remove the trailing "&"
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        }

        String urlQuery = queryBuilder.toString();

        Retrofit retrofit = APIClient.getClient();
        APIService apiService = retrofit.create(APIService.class);
        DeleteNotesRequest deleteNotesRequest = new DeleteNotesRequest(uuids);
        Call<ResponseBody> call = apiService.deleteNotes(authHeader,urlQuery);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                } else {
                    showToast("Error while deleting notes");
//                    Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Network error while deleting notes");
            }
        });
    }

    public void fetchFullNotes(List<String> uuids){

        if(uuids.size()==0)
            return;

        StringBuilder queryBuilder = new StringBuilder();

        List<Note> notesList = MainActivity.getNotesList();

        if (uuids.size() > 0) {
            queryBuilder.append("full-notes?");
            for (String uuid : uuids) {
                queryBuilder.append("uuid=").append(uuid).append("&");
            }
            // Remove the trailing "&"
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        }

        String urlQuery = queryBuilder.toString();

        Retrofit retrofit = APIClient.getClient();
        APIService apiService = retrofit.create(APIService.class);
        Call<FullNotesResponse> call = apiService.getFullNotes(urlQuery,authHeader);

        call.enqueue(new Callback<FullNotesResponse>() {
            @Override
            public void onResponse(Call<FullNotesResponse> call, Response<FullNotesResponse> response) {
                if (response.isSuccessful()) {
                    FullNotesResponse data = response.body();
                    List<FullNotesResponse.NoteItem> notes = data.getNotes();
                    int startPosition = notesList.size()-1;
                    notes.forEach(noteItem -> {
                        Note noteToBeAdded = fromNoteItem(noteItem);
                        if(noteToBeAdded!=null){
                            notesList.add(noteToBeAdded);
                            try {
                                dao.insert(noteToBeAdded);
                            }catch (Exception e){

                            }

                        }

                    });
                    int endPosition = notesList.size()-1;
//                    MainActivity.getNotesAdapter().notifyItemRangeInserted(startPosition,endPosition);
                    MainActivity.getNotesAdapter().notifyDataSetChanged();
                } else {
                    showToast("Error while downloading notes");
                }
            }

            @Override
            public void onFailure(Call<FullNotesResponse> call, Throwable t) {
                showToast("Network error while downloading notes");
            }
        });
    }

    public void fetchIdsAndHashes(){
        Retrofit retrofit = APIClient.getClient();
        APIService apiService = retrofit.create(APIService.class);
        Call<NotesResponse> call = apiService.getNotes(authHeader);

        call.enqueue(new Callback<NotesResponse>() {
            @Override
            public void onResponse(Call<NotesResponse> call, Response<NotesResponse> response) {
                if (response.isSuccessful()) {
                    NotesResponse notesResponse = response.body();

                    List<NotesResponse.NoteItem> notes = notesResponse.getNotes();
                    handleFetchedNotes(notes);

                } else {
                    showToast("Error while getting notes info");
                    showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<NotesResponse> call, Throwable t) {
                showToast("Network error while getting notes info");
                t.printStackTrace();
            }
        });

    }

    private void handleFetchedNotes(List<NotesResponse.NoteItem> fetchedNotesList) {
        List<String> notesToBeDownloaded = new ArrayList<>();
        List<NotesResponse.NoteItem> differentVersionNotes = new ArrayList<>();

        List<Note> notesList = MainActivity.getNotesList();

        for (NotesResponse.NoteItem fetchedNote : fetchedNotesList) {
            String fetchedUuid = fetchedNote.getUuid();
            String fetchedHash = fetchedNote.getHash();

            // Check if the fetched note's UUID is present in notesList
            boolean found = false;
            for (Note existingNote : notesList) {
                String existingUuid = existingNote.getUuId();

                if (fetchedUuid.equals(existingUuid)) {
                    found = true;

                    // Compare hashes
                    String existingHash = existingNote.getHash();
                    if (!fetchedHash.equals(existingHash)) {
                        // Hashes don't match, add to differentVersionNotes
                        differentVersionNotes.add(fetchedNote);
                    }
                    break; // Stop searching, UUID found
                }
            }

            // If the UUID is not found, add to notesToBeDownloaded
            if (!found) {
                notesToBeDownloaded.add(fetchedNote.getUuid());
            }
        }

        List<Note> notesToBeUploaded = new ArrayList<>();

        for (Note existingNote : notesList) {
            String existingUuid = existingNote.getUuId();
            boolean found = false;

            for (NotesResponse.NoteItem fetchedNote : fetchedNotesList) {
                String fetchedUuid = fetchedNote.getUuid();

                if (existingUuid.equals(fetchedUuid)) {
                    found = true;
                    break; // Stop searching, UUID found
                }
            }

            // If the UUID is not found in fetchedNotesList, add to notesToBeUploaded
            if (!found) {
                notesToBeUploaded.add(existingNote);
            }
        }

        uploadNotes(notesToBeUploaded);

        fetchFullNotes(notesToBeDownloaded);

        differentVersionNotes.forEach(noteItem -> alertDialogShow(context,noteItem));

    }

    public void alertDialogShow(Context context, NotesResponse.NoteItem noteItem) {

        List<Note> notesList = MainActivity.getNotesList();
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage("The local and cloud versions of this note are different. What would you like to do?")
                .setPositiveButton("Use local version", (dialog1, which) -> {
                    // Update cloud version with local data
                    // Room: no changes
                    updateNotes(singleItemList(getNoteFromMainListByUuId(noteItem.getUuid())));
                    MainActivity.getNotesAdapter().notifyDataSetChanged();
                })
                .setNeutralButton("Keep both versions", (dialog2, which) -> {
                    // Create _local copy of outdated note, Delete outdated one from local, fetch updated from cloud
                    // Room: delete & insert handled inside fetch function
                    Note noteToBeModified = getNoteFromMainListByUuId(noteItem.getUuid());
                    Note localVersionNote = new Note(noteToBeModified);
                    localVersionNote.setTitle(localVersionNote.getTitle() + "_local");
                    notesList.add(localVersionNote);
                    notesList.remove(noteToBeModified);
                    List<String> notesToBeFetched = new ArrayList<>();
                    notesToBeFetched.add(noteItem.getUuid());
                    fetchFullNotes(notesToBeFetched);
                    MainActivity.getNotesAdapter().notifyDataSetChanged();

                    List<Note> localNotesToBeUploaded = new ArrayList<>();
                    localNotesToBeUploaded.add(localVersionNote);
                    uploadNotes(localNotesToBeUploaded);

                })
                .setNegativeButton("Use cloud version", (dialog3, which) -> {
                    // Delete local version of outdated note, fetch updated version
                    // Room: delete & insert handled inside fetch function
                    Note noteToBeDeleted = getNoteFromMainListByUuId(noteItem.getUuid());
                    notesList.remove(noteToBeDeleted);
                    MainActivity.getNotesAdapter().notifyDataSetChanged();
                    List<String> notesToBeDeletedList = new ArrayList<>();
                    notesToBeDeletedList.add(noteItem.getUuid());
                    fetchFullNotes(notesToBeDeletedList);
                    MainActivity.getNotesAdapter().notifyDataSetChanged();
                })
                .create();
        dialog.show();
    }

    private static Note fromNoteItem(FullNotesResponse.NoteItem noteItem){
        Note newNote = new Note(noteItem.getUuid(),new Date(),new Date(),noteItem.getTitle(),noteItem.getContent(),noteItem.getColor(),noteItem.getHash());
        return newNote;
    }
}
