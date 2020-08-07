package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.RegistrationDetailsValidationResponse;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextPhoneNumber;
    private TextInputEditText editTextUserName;
    private TextInputEditText editTextPassword;
    private TextView textViewErrorMessage;
    private ACProgressFlower dialog;
    private AutoCompleteTextView dropdownText;
    private TextInputLayout registerPhoneNumber;
    private CountryCodePicker countryCodePicker;
    private LinearLayout linearLayoutPhoneStatus;
    private int phoneStatus;

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
        dropdownText = findViewById(R.id.dropdownText);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        linearLayoutPhoneStatus = findViewById(R.id.linearLayoutPhoneStatus);
        String[] options = new String[]{
                getString(R.string.smart_phone),
                getString(R.string.feature_phone),
                getString(R.string.no_phone)
        };

        if(!SharedPreferenceUtils.isLeader(RegisterActivity.this)) {
            linearLayoutPhoneStatus.setVisibility(View.GONE);
        }

        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(RegisterActivity.this, R.layout.dropdown_item, options);
        dropdownText.setAdapter(dropdownAdapter);
        dropdownText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2) {
                    registerPhoneNumber.setVisibility(View.GONE);
                    countryCodePicker.setVisibility(View.GONE);
                } else {
                    registerPhoneNumber.setVisibility(View.VISIBLE);
                    countryCodePicker.setVisibility(View.VISIBLE);
                }
                phoneStatus = position;
            }
        });
    }

    public void backButtonPressed(View view) {
        finish();
    }

    public void validateDetails(View view) {
        dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        textViewErrorMessage.setText("");
        if(checkInput()) {
            //Make request to the REST Server
            String url = getString(R.string.base_end_point_ip) + "register/";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", editTextUserName.getText().toString());
                jsonBody.put("password", editTextPassword.getText().toString());
                jsonBody.put("contact", editTextPhoneNumber.getText().toString());
                switch (phoneStatus) {
                    case Constants.FEATURE_PHONE:
                        jsonBody.put("category", "P");
                        break;
                    case Constants.NO_PHONE:
                        jsonBody.put("category", "N");
                        break;
                    default:
                        jsonBody.put("category","F");
                }
                Log.e("url",url);
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString());
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        RegistrationDetailsValidationResponse registrationDetailsValidationResponse = gson.fromJson(response.toString(), RegistrationDetailsValidationResponse.class);
                        handleRegisterValidation(registrationDetailsValidationResponse);
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textViewErrorMessage.setText(getString(R.string.server_error));
                        Log.e("Error",error.toString());
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                };
                JsonObjectRequest registrationDetailsValidationRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, responseListener, errorListener);
                VolleySingleton.getInstance(RegisterActivity.this).addToRequestQueue(registrationDetailsValidationRequest);
            } catch (JSONException e) {
                Toast.makeText(RegisterActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }

    private void handleRegisterValidation(RegistrationDetailsValidationResponse registrationDetailsValidationResponse) {
        if(ResponseStatusCodeHandler.isSuccessful(registrationDetailsValidationResponse.getStatuscode())) {
            //Move on to the next activity
            dialog.dismiss();
            Intent intent;
            if(phoneStatus == Constants.NO_PHONE) {
                SharedPreferenceUtils.clearUserData(RegisterActivity.this, false);
                SharedPreferenceUtils.setToken(RegisterActivity.this, registrationDetailsValidationResponse.getToken());
                intent = new Intent(RegisterActivity.this, UserDetailsActivity.class);
            } else {
                intent = new Intent(RegisterActivity.this, VerifyOTPActivity.class);
                intent.putExtra("phoneNumber", editTextPhoneNumber.getText().toString());
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            textViewErrorMessage.setText(ResponseStatusCodeHandler.getMessage(registrationDetailsValidationResponse.getStatuscode()));
            dialog.dismiss();
        }
    }

    private boolean checkInput() {
        if(editTextPhoneNumber.getText().toString().isEmpty() && !SharedPreferenceUtils.isLeader(RegisterActivity.this)) {
            textViewErrorMessage.setText("Phone number can't be empty");
            dialog.dismiss();
            return false;
        }
        if(editTextUserName.getText().toString().isEmpty()) {
            textViewErrorMessage.setText("User name can't be empty");
            dialog.dismiss();
            return false;
        }
        if(editTextPassword.getText().toString().isEmpty()) {
            textViewErrorMessage.setText("Password can't be empty");
            dialog.dismiss();
            return false;
        }
        return true;
    }

    public void moveToLoginActivity(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
