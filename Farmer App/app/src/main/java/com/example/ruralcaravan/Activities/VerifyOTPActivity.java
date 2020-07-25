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
import com.chaos.view.PinView;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.OTPValidationResponse;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyOTPActivity extends AppCompatActivity {

    private PinView pinViewOTP;
    private TextView textViewOTPDescriptionText;
    private TextView textViewErrorMessage;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_otp);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        pinViewOTP = findViewById(R.id.pinViewOTP);
        textViewOTPDescriptionText = findViewById(R.id.textViewOTPDescriptionText);
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage);

        textViewOTPDescriptionText.setText(textViewOTPDescriptionText.getText() + phoneNumber);
    }

    public void moveToStartUpScreen(View view) {
        Intent intent = new Intent(VerifyOTPActivity.this, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void verifyCodeButtonPressed(View view) {
        textViewErrorMessage.setText("");
        String otp = pinViewOTP.getText().toString();
        if (otp.length() == 6) {
            String verifyOTPUrl = getResources().getString(R.string.base_end_point_ip) + "register/OTP/";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("otp", pinViewOTP.getText().toString());
                jsonBody.put("contact", phoneNumber);
//                jsonBody.put("contact", "8439740130");
                Log.e("url", verifyOTPUrl);
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        OTPValidationResponse otpValidationResponse = gson.fromJson(response.toString(), OTPValidationResponse.class);
                        handleOTPValidation(otpValidationResponse);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textViewErrorMessage.setText("Unable to connect with server");
                        Log.e("Error", error.toString());
                    }
                };
                Log.e("json", String.valueOf(jsonBody));
                JsonObjectRequest otpValidationRequest = new JsonObjectRequest(Request.Method.POST, verifyOTPUrl, jsonBody, responseListener, errorListener);
                VolleySingleton.getInstance(VerifyOTPActivity.this).addToRequestQueue(otpValidationRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOTPValidation(OTPValidationResponse otpValidationResponse) {
        if (ResponseStatusCodeHandler.isSuccessful(otpValidationResponse.getStatuscode())) {
            SharedPreferenceUtils.setToken(VerifyOTPActivity.this, otpValidationResponse.getToken());
            Intent intent = new Intent(VerifyOTPActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        } else {
            textViewErrorMessage.setText(ResponseStatusCodeHandler.getMessage(otpValidationResponse.getStatuscode()));
        }
    }
}
