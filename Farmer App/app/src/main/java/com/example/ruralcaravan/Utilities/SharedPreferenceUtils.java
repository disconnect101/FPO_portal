package com.example.ruralcaravan.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {

    public static void setActivityState(Context context, int state) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.KEY_STATE, state);
        editor.commit();
    }

    public static int getActivityState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_STATE, Constants.ACTIVITY_START_UP);
    }

    public static void setToken(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_TOKEN, token);
        editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getString(Constants.KEY_TOKEN, Constants.TOKEN_NOT_FOUND);
    }

}