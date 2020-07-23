package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.ruralcaravan.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register4);
    }

    public void stateBtnPressed(View view) {
        Log.e("State","Button pressed");
        final TextInputEditText textInputEditText = findViewById(R.id.stateEditText);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textInputEditText.setText("Uttarakahnd");
            }
        });
    }

    public void backBtnPressed (View view) {
        Log.e("Button:","Back button pressed");
    }
}
