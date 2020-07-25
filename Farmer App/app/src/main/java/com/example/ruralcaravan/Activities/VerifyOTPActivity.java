package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.chaos.view.PinView;
import com.example.ruralcaravan.R;

public class VerifyOTPActivity extends AppCompatActivity {

    private PinView pinViewOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_otp);
        pinViewOTP = findViewById(R.id.pinViewOTP);
    }

}
