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
import com.example.ruralcaravan.ResponseClasses.RegistrationDetailsValidationResponse;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextPhoneNumber;
    private TextInputEditText editTextUserName;
    private TextInputEditText editTextPassword;
    private TextView textViewErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage);
    }

    public void backButtonPressed(View view) {
        finish();
    }

    public void validateDetails(View view) {
        textViewErrorMessage.setText("");
        if(checkInput()) {
            //Make request to the REST Server
            String url = getResources().getString(R.string.base_end_point_ip) + "register/";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", editTextUserName.getText().toString());
                jsonBody.put("password", editTextPassword.getText().toString());
                jsonBody.put("contact", editTextPhoneNumber.getText().toString());
                jsonBody.put("category","F");
                Log.e("url",url);
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        RegistrationDetailsValidationResponse registrationDetailsValidationResponse = gson.fromJson(response.toString(), RegistrationDetailsValidationResponse.class);
                        handleRegisterValidation(registrationDetailsValidationResponse);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textViewErrorMessage.setText("Unable to connect with server");
                        Log.e("Error",error.toString());
                    }
                };
                Log.e("json", String.valueOf(jsonBody));
                JsonObjectRequest registrationDetailsValidationRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, responseListener, errorListener);
                VolleySingleton.getInstance(RegisterActivity.this).addToRequestQueue(registrationDetailsValidationRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRegisterValidation(RegistrationDetailsValidationResponse registrationDetailsValidationResponse) {
        if(ResponseStatusCodeHandler.isSuccessful(registrationDetailsValidationResponse.getStatuscode())) {
            //Move on to the next activity
            Log.e("Success","Hello");
            Intent intent = new Intent(RegisterActivity.this, VerifyOTPActivity.class);
            intent.putExtra("phoneNumber", editTextPhoneNumber.getText().toString());
            startActivity(intent);
        } else {
            //Prompt error
            textViewErrorMessage.setText(ResponseStatusCodeHandler.getMessage(registrationDetailsValidationResponse.getStatuscode()));
        }
    }

    private boolean checkInput() {
        if(editTextPhoneNumber.getText().toString().isEmpty()) {
            textViewErrorMessage.setText("Phone number can't be empty");
            return false;
        }
        if(editTextUserName.getText().toString().isEmpty()) {
            textViewErrorMessage.setText("User name can't be empty");
            return false;
        }
        if(editTextPassword.getText().toString().isEmpty()) {
            textViewErrorMessage.setText("Password can't be empty");
            return false;
        }
        return true;
    }

    public void moveToLoginActivity(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
