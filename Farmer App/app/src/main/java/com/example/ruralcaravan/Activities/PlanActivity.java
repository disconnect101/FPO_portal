package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Fragments.PlanDetailsFragment;
import com.example.ruralcaravan.Fragments.ProductsUnderPlan;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;
import com.example.ruralcaravan.ResponseClasses.PlansResponse;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class PlanActivity extends AppCompatActivity {

    private PlansResponse planDetails;
    private ItemDetailedResponse[] itemsDetails;
    private boolean firstTabSelected;
    private boolean isPlanSubscribed;
    private TextView textViewPlanDetails;
    private TextView textViewProductsUnderPlan;
    private int count;
    private ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        Intent intent = getIntent();
        String planId = intent.getStringExtra("planId");
        isPlanSubscribed = intent.getBooleanExtra("isPlanSubscribed", false);

        textViewPlanDetails = findViewById(R.id.textViewPlanDetails);
        textViewProductsUnderPlan = findViewById(R.id.textViewProductsUnderPlan);

        firstTabSelected = false;

        count = 2;
        dialog = new ACProgressFlower.Builder(PlanActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        fetchPlanDetails(planId);
        fetchProductsUnderPlan(planId);

    }

    private void fetchProductsUnderPlan(String planId) {
        String productsUnderPlanUrl = getString(R.string.base_end_point_ip) + "cropproducts/" + planId + "/";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        itemsDetails = gson.fromJson(response.getJSONArray("data").toString(), ItemDetailedResponse[].class);
                        count--;
                        if(count == 0)
                            dialog.dismiss();
                    } else {
                        Toast.makeText(PlanActivity.this, ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    Toast.makeText(PlanActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(PlanActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };

        JsonObjectRequest productsUnderPlanRequest = new JsonObjectRequest(Request.Method.GET, productsUnderPlanUrl, null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(PlanActivity.this));
                return params;
            }
        };
        VolleySingleton.getInstance(PlanActivity.this).addToRequestQueue(productsUnderPlanRequest);
    }

    private void fetchPlanDetails(String planId) {
        final String planDetailsUrl = getString(R.string.base_end_point_ip) + "crops/" + planId + "/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        planDetails = gson.fromJson(response.getJSONObject("data").toString(), PlansResponse.class);
                        count--;
                        if(count == 0) {
                            dialog.dismiss();
                        }
                        showPlanDetails(null);
                    } else {
                        Toast.makeText(PlanActivity.this, ResponseStatusCodeHandler.getMessage(response.getString("stauscode")), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    Toast.makeText(PlanActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(PlanActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };
        JsonObjectRequest planDetailsRequest = new JsonObjectRequest(Request.Method.GET, planDetailsUrl, null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(PlanActivity.this));
                return params;
            }
        };
        VolleySingleton.getInstance(PlanActivity.this).addToRequestQueue(planDetailsRequest);
    }

    public PlansResponse getPlanDetails() {
        return planDetails;
    }

    public ItemDetailedResponse[] getItemsDetails() {
        return itemsDetails;
    }

    public boolean getIsPlanSubscribed() {
        return isPlanSubscribed;
    }

    public void setIsPlanSubscribed(boolean status) {
        isPlanSubscribed = status;
    }

    public void showPlanDetails(View view) {
        if(!firstTabSelected) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.planFragmentContainer, new PlanDetailsFragment())
                    .commit();
            firstTabSelected = true;
            textViewProductsUnderPlan.setBackground(null);
            textViewPlanDetails.setBackground(getDrawable(R.drawable.bottom_border));
        }
    }

    public void showProductsUnderPlan(View view) {
        if(firstTabSelected) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.planFragmentContainer, new ProductsUnderPlan())
                    .commit();
            firstTabSelected = false;
            textViewPlanDetails.setBackground(null);
            textViewProductsUnderPlan.setBackground(getDrawable(R.drawable.bottom_border));
        }
    }
}
