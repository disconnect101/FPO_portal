package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.LoginResponse;
import com.example.ruralcaravan.SingletonClasses.VolleySingleton;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextUserName;
    private TextInputEditText editTextLoginPassword;
    private TextView textViewErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage);
    }

    public void loginButtonPressed(View view) {
        //Server request for login
        String url = getResources().getString(R.string.base_end_point_ip) + "login/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", editTextUserName.getText().toString());
            jsonBody.put("password", editTextLoginPassword.getText().toString());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    LoginResponse loginResponse = gson.fromJson(response.toString(), LoginResponse.class);
                    handleLoginResponse(loginResponse);
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Login","Error");
                }
            };
            JsonObjectRequest loginServerRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, responseListener, errorListener);
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(loginServerRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleLoginResponse(LoginResponse loginResponse) {
        if(ResponseStatusCodeHandler.isLoginSuccessful(loginResponse.getStatuscode())) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            textViewErrorMessage.setText(ResponseStatusCodeHandler.getMessage(loginResponse.getStatuscode()));
        }
    }

    public void moveToRegisterActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void backButtonPressed(View view) {
        finish();
    }

}
