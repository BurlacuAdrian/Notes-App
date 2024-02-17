package notesapp.main.apiclient;

import static notesapp.main.utils.UtilityFunctions.createJsonString;

import android.util.Log;
import android.widget.Toast;

import java.util.List;

import notesapp.main.activities.MainActivity;
import notesapp.main.entities.Note;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataManager {

    private final APIService apiService;

    public DataManager() {
        Retrofit retrofit = APIClient.getClient();
        apiService = retrofit.create(APIService.class);
    }


    public static void updateNotes(List<Note> notesToBeUpdated){

        if(notesToBeUpdated.size()==0)
            return;

        for(Note note : notesToBeUpdated)
            dao.update(note);

        if(offline)
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
                    Toast.makeText(MainActivity.this, "updateNotes successful!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "updateNotes error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "updateNotes onFailure"+t.toString(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
