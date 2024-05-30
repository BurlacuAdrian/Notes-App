package notesapp.main.apiclient;

import notesapp.main.apiclient.requests.LoginRequest;
import notesapp.main.apiclient.responses.FullNotesResponse;
import notesapp.main.apiclient.responses.LoginResponse;
import notesapp.main.apiclient.responses.NotesResponse;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIService {
    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    //Both login and signup have the same request and response bodies, so no need for new request or response classes, just the endpoint
    @POST("signup")
    Call<LoginResponse> signupUser(@Body  LoginRequest loginRequest);

    @GET("notes")
    Call<NotesResponse> getNotes(@Header("Authorization") String authToken);

    @GET
    Call<FullNotesResponse> getFullNotes(@Url String url, @Header("Authorization") String authToken);

    @DELETE
    Call<ResponseBody> deleteNotes(@Header("Authorization") String authToken, @Url String url);

    @POST("notes")
    Call<ResponseBody> postNotes(@Header("Authorization") String authToken, @Body RequestBody requestBody);

    @PATCH("notes")
    Call<ResponseBody> updateNotes(@Header("Authorization") String authToken, @Body RequestBody requestBody);
}

