package se.chalmers.bookreviewclient;

import android.app.Application;
import android.preference.PreferenceManager;

import se.chalmers.bookreviewclient.net.WebRequestManager;

public class BookReviewApplication extends Application {
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String USERNAME = "USERNAME";

    @Override
    public void onCreate() {
        super.onCreate();

        WebRequestManager.getInstance().initialize(this);
    }

    public String getUsername() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(USERNAME, null);
    }

    public void saveUsername(String username) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(USERNAME, username).apply();
    }

    public String getUserToken() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, null);
    }

    public void saveUserToken(String token) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(USER_TOKEN, token).apply();
    }

    public void logout() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
    }
}
