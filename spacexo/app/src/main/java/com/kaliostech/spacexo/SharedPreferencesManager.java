package com.kaliostech.spacexo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager implements PreferenceHelper {

    private static final String PREFS_NAME = "MyAppPreferences";
    private static final String KEY_IS_PLAYING = "isPlaying";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Constructor to initialize SharedPreferences
    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public void setIsPlaying(boolean isPlaying) {
        editor.putBoolean(KEY_IS_PLAYING, isPlaying);
        editor.apply();  // Save changes
    }

    @Override
    public boolean getIsPlaying() {
        return sharedPreferences.getBoolean(KEY_IS_PLAYING, true);  // Default is false
    }
}

