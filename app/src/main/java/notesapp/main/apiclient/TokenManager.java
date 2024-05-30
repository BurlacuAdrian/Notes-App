package notesapp.main.apiclient;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String TOKEN_KEY = "jwt";
    public static final String USERNAME_KEY = "username";
    private static final String PREFERENCES_NAME = "app_preferences";

    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public void saveUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME_KEY, null);
    }

    public void clearUsername() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USERNAME_KEY);
        editor.apply();
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }
}

