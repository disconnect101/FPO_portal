package com.example.ruralcaravan.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.ruralcaravan.ResponseClasses.UserResponse;
import com.google.gson.Gson;

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
        return preferences.getString(Constants.KEY_TOKEN, Constants.DEFAULT_STRING);
    }

    public static boolean isUserDataRequired(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        String jsonUserString = preferences.getString(Constants.KEY_USER, Constants.DEFAULT_STRING);
        return jsonUserString.equals(Constants.DEFAULT_STRING);
    }

    public static void setUserData(Context context, String userJSONString) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_USER, userJSONString);
        editor.commit();
    }

    public static UserResponse getUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        String jsonUserData = preferences.getString(Constants.KEY_USER, Constants.DEFAULT_STRING);
        if(jsonUserData.equals(Constants.DEFAULT_STRING))
            return null;
        Gson gson = new Gson();
        UserResponse user = gson.fromJson(jsonUserData, UserResponse.class);
        return user;
    }

    public static void clearUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.KEY_STATE);
        editor.remove(Constants.KEY_TOKEN);
        editor.remove(Constants.KEY_USER);
        editor.commit();
    }

}