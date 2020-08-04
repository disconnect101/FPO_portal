package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        Locale locale = new Locale(SharedPreferenceUtils.getLanguage(SplashScreen.this));
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, displayMetrics);

        Intent intent;
        Log.d("STATE", String.valueOf(SharedPreferenceUtils.getActivityState(SplashScreen.this)));
        switch (SharedPreferenceUtils.getActivityState(SplashScreen.this)) {
            case Constants.ACTIVITY_USER_DETAILS:
                intent = new Intent(SplashScreen.this, UserDetailsActivity.class);
                break;
            case Constants.ACTIVITY_HOME:
                intent = new Intent(SplashScreen.this,MainActivity.class);
                break;
            default:
                intent = new Intent(SplashScreen.this, StartUpActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
