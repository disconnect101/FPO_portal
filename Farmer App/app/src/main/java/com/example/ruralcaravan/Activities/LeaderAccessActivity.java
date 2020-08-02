package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Adapters.LeaderAccessAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.LeaderAccessResponse;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LeaderAccessActivity extends AppCompatActivity implements LeaderAccessAdapter.OnFarmerSelectedListener {

    private RecyclerView recyclerViewFarmers;
    private ArrayList<LeaderAccessResponse> leaderAccessAdapterArrayList;
    private ACProgressFlower dialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_access);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.leader_access);

        recyclerViewFarmers = findViewById(R.id.recyclerViewFarmers);
        recyclerViewFarmers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LeaderAccessActivity.this);
        recyclerViewFarmers.setLayoutManager(linearLayoutManager);
        leaderAccessAdapterArrayList = new ArrayList<>();
        final LeaderAccessAdapter leaderAccessAdapter = new LeaderAccessAdapter(LeaderAccessActivity.this, leaderAccessAdapterArrayList, this);
        recyclerViewFarmers.setAdapter(leaderAccessAdapter);

        dialog = new ACProgressFlower.Builder(LeaderAccessActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String leaderAccessUrl = getString(R.string.base_end_point_ip) + "leaderaccess/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        Log.e("Leader access response", response.toString());
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        LeaderAccessResponse[] leaderAccessResponses = gson.fromJson(response.getJSONArray("farmerlist").toString(), LeaderAccessResponse[].class);
                        handleResponse(leaderAccessResponses);
                    } else {
                        Toast.makeText(LeaderAccessActivity.this, ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LeaderAccessActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LeaderAccessActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                dialog.dismiss();
            }
        };
        JsonObjectRequest leaderAccessRequest = new JsonObjectRequest(Request.Method.GET, leaderAccessUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getLeaderToken(LeaderAccessActivity.this));
                return params;
            }
        };
        VolleySingleton.getInstance(LeaderAccessActivity.this).addToRequestQueue(leaderAccessRequest);
    }

    public void handleResponse(LeaderAccessResponse[] leaderAccessResponses) {
        leaderAccessAdapterArrayList.clear();
        leaderAccessAdapterArrayList.addAll(Arrays.asList(leaderAccessResponses));
        recyclerViewFarmers.getAdapter().notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    public void onFarmerSelected(int position) {

        SharedPreferenceUtils.clearUserData(LeaderAccessActivity.this, false);
        SharedPreferenceUtils.setToken(LeaderAccessActivity.this, leaderAccessAdapterArrayList.get(position).getToken());

        Intent intent = new Intent(LeaderAccessActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leader_access, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.addNewFarmer:
                intent = new Intent(LeaderAccessActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.switchToLeaderProfile:
                SharedPreferenceUtils.clearUserData(LeaderAccessActivity.this, false);
                SharedPreferenceUtils.setToken(LeaderAccessActivity.this,
                        SharedPreferenceUtils.getLeaderToken(LeaderAccessActivity.this));
                intent = new Intent(LeaderAccessActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
