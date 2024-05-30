package notesapp.main.activities;

import static notesapp.main.utils.Constants.*;
import static notesapp.main.utils.UtilityFunctions.showToast;
import static notesapp.main.utils.UtilityFunctions.singleItemList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import notesapp.main.entities.Note;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// TODO : remove cleartext security config

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        Button sendToSignUpButton = findViewById(R.id.sendToSignUpButton);
        sendToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SIGNUP);
//                finish();
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

        String username = usernameEditText.getText().toString(),
                password = passwordEditText.getText().toString();

        Retrofit retrofit = APIClient.getClient();


        APIService apiService = retrofit.create(APIService.class);
        LoginRequest loginRequest = new LoginRequest(username,password);


        Call<LoginResponse> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    String message = loginResponse.getMessage();
                    String token = loginResponse.getToken();

                    TokenManager tokenManager = new TokenManager(LoginActivity.this);
                    tokenManager.saveToken(token);
                    tokenManager.saveUsername(username);
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error"+t.toString(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGNUP && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}