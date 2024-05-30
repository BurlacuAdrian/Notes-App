package notesapp.main.activities;

import static notesapp.main.utils.UtilityFunctions.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import notesapp.main.R;
import notesapp.main.apiclient.APIClient;
import notesapp.main.apiclient.APIService;
import notesapp.main.apiclient.TokenManager;
import notesapp.main.apiclient.requests.LoginRequest;
import notesapp.main.apiclient.responses.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText,passwordRedoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        Button sendToLoginButton = findViewById(R.id.sendToLoginButton);
        sendToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button guestModeButton = findViewById(R.id.guestModeButton);
        guestModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    private void handleLogin(){

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordRedoEditText = findViewById(R.id.passwordRedoEditText);

        String username = usernameEditText.getText().toString(),
                password = passwordEditText.getText().toString(),
                passwordRedo = passwordRedoEditText.getText().toString();

        if(!(password.equals(passwordRedo))){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }else{
            sendSignupRequest(username,password);
        }

    }

    private void sendSignupRequest(String username, String password){
        Retrofit retrofit = APIClient.getClient();


        APIService apiService = retrofit.create(APIService.class);
        LoginRequest loginRequest = new LoginRequest(username,password);


        Call<LoginResponse> call = apiService.signupUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    String message = loginResponse.getMessage();
                    String token = loginResponse.getToken();

                    TokenManager tokenManager = new TokenManager(SignupActivity.this);
                    tokenManager.saveToken(token);
                    tokenManager.saveUsername(username);
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    showToast("Error on signup");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showToast("Network error on singup");
                t.printStackTrace();
            }
        });
    }
}