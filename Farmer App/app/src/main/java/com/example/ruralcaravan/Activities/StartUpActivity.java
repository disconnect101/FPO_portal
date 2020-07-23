package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ruralcaravan.R;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_up);
    }

    public void moveToLoginActivity(View view) {
        Intent intent = new Intent(StartUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void moveToRegisterActivity(View view) {
        Intent intent = new Intent(StartUpActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
