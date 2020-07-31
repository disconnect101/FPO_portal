package com.example.ruralcaravan.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class UserDetailsActivity extends AppCompatActivity {

    private TextInputEditText editTextFirstName;
    private TextInputEditText editTextLastName;
    private EditText editTextDistrict;
    private EditText editTextVillage;
    private ArrayList<String> districts;
    private ArrayList<String> villages;
    private JSONObject locationResponse;
    private TextView textViewErrorMessage;
    private ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_details);
        SharedPreferenceUtils.setActivityState(UserDetailsActivity.this, Constants.ACTIVITY_USER_DETAILS);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextDistrict = findViewById(R.id.editTextDistrict);
        editTextVillage = findViewById(R.id.editTextVillage);
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage);
        districts = new ArrayList<>();
        villages = new ArrayList<>();
        String getLocationListUrl = getResources().getString(R.string.base_end_point_ip) + "villages/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                locationResponse = response;
                handleLocationListResponse(response);
                dialog.dismiss();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewErrorMessage.setText("SERVER error");
                dialog.dismiss();
            }
        };

        dialog = new ACProgressFlower.Builder(UserDetailsActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        JsonObjectRequest locationListRequest = new JsonObjectRequest(Request.Method.GET, getLocationListUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(UserDetailsActivity.this));
                return params;
            }
        };
        VolleySingleton.getInstance(UserDetailsActivity.this).addToRequestQueue(locationListRequest);
    }

    private void handleLocationListResponse(JSONObject response) {
        Log.e("Response", response.toString());
        Iterator<String> keys;
        try {
            JSONArray jsonArrayLocation = response.getJSONArray("data");
            for(int i=0;i<jsonArrayLocation.length();i++) {
                keys = jsonArrayLocation.getJSONObject(i).keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    districts.add(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseDistrict(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
        builder.setTitle("Choose a district");
        final ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(UserDetailsActivity.this,
                android.R.layout.simple_dropdown_item_1line, districts);
        builder.setAdapter(districtAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextDistrict.setText(districts.get(which));
                setVillageList(districts.get(which));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setVillageList(String district) {
        villages = new ArrayList<>();
        try {
            JSONArray jsonArrayLocation = locationResponse.getJSONArray("data");
            for(int i=0;i<jsonArrayLocation.length();i++) {
                if(jsonArrayLocation.getJSONObject(i).keys().next().compareTo(district) == 0) {
                    JSONArray jsonArrayVillage = jsonArrayLocation.getJSONObject(i).getJSONArray(district);
                    for(int j=0;j<jsonArrayVillage.length();j++)
                        villages.add(jsonArrayVillage.getString(j));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Villages", villages.toString());
    }

    public void chooseVillage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
        builder.setTitle("Choose a village");
        final ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(UserDetailsActivity.this,
                android.R.layout.simple_list_item_1, villages);
        builder.setAdapter(districtAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextVillage.setText(villages.get(which));
                setVillageList(villages.get(which));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void moveToMainActivity(View view) {
        //TODO: Check the inputs

        dialog = new ACProgressFlower.Builder(UserDetailsActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String postUserDataUrl = getResources().getString(R.string.base_end_point_ip) + "userdata/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("first_name", editTextFirstName.getText().toString());
            jsonBody.put("last_name", editTextLastName.getText().toString());
            jsonBody.put("village", editTextVillage.getText().toString());
            jsonBody.put("district", editTextDistrict.getText().toString());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    handlePostUserDataResponse(response);
                    dialog.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textViewErrorMessage.setText("SERVER Error");
                    dialog.dismiss();
                }
            };
            JsonObjectRequest postUserDetails = new JsonObjectRequest(Request.Method.POST, postUserDataUrl, jsonBody, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(UserDetailsActivity.this));
                    return params;
                }
            };
            VolleySingleton.getInstance(UserDetailsActivity.this).addToRequestQueue(postUserDetails);
        } catch (JSONException e) {
            textViewErrorMessage.setText(e.toString());
            e.printStackTrace();
            dialog.dismiss();
        }
    }

    private void handlePostUserDataResponse(JSONObject response) {
        try {
            if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                textViewErrorMessage.setText(ResponseStatusCodeHandler.getMessage(response.getString("statuscode")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
