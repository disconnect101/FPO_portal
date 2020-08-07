package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.LoginResponse;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextUserName;
    private TextInputEditText editTextLoginPassword;
    private TextView textViewErrorMessage;
    private ACProgressFlower dialog;

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
        textViewErrorMessage.setText("");
        dialog = new ACProgressFlower.Builder(LoginActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
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
                    dialog.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textViewErrorMessage.setText("Unable to connect to server");
                    dialog.dismiss();
                }
            };
            JsonObjectRequest loginServerRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, responseListener, errorListener);
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(loginServerRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    private void handleLoginResponse(LoginResponse loginResponse) {
        if(ResponseStatusCodeHandler.isSuccessful(loginResponse.getStatuscode())) {
            SharedPreferenceUtils.setToken(LoginActivity.this, loginResponse.getToken());
            if(loginResponse.getCategory().equals(Constants.LEADER)) {
                Toast.makeText(LoginActivity.this, getString(R.string.welcome_leader), Toast.LENGTH_LONG).show();
                SharedPreferenceUtils.setLeaderToken(LoginActivity.this, loginResponse.getToken());
            }
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
