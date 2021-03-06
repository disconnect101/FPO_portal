package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Adapters.CatalogueAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemsResponse;
import com.example.ruralcaravan.Utilities.Constants;
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

public class ListItemsActivity extends AppCompatActivity {

    private RecyclerView listItemsRecyclerView;
    private ArrayList<ItemsResponse> catalogueAdapterArrayList;
    private ACProgressFlower dialog;
    private TextView textViewEmptyList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        textViewEmptyList = findViewById(R.id.textViewEmptyList);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItemsRecyclerView = findViewById(R.id.listItemsRecyclerView);
        listItemsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListItemsActivity.this);
        listItemsRecyclerView.setLayoutManager(linearLayoutManager);
        catalogueAdapterArrayList = new ArrayList<>();
        CatalogueAdapter catalogueAdapter = new CatalogueAdapter(ListItemsActivity.this, catalogueAdapterArrayList);
        listItemsRecyclerView.setAdapter(catalogueAdapter);

        Intent intent = getIntent();
        int whatToList = intent.getIntExtra(Constants.KEY_CATEGORY, Constants.DEFAULT_INTEGER);
        String itemsUrl = getResources().getString(R.string.base_end_point_ip) + "catalogue/";
        JSONObject jsonBody = new JSONObject();
        try { switch (whatToList) {
                case Constants.LIST_SEED:
                    jsonBody.put("category", "SED");
                    getSupportActionBar().setTitle(getString(R.string.seeds));
                    break;
                case Constants.LIST_FERTILIZER:
                    jsonBody.put("category", "FER");
                    getSupportActionBar().setTitle(getString(R.string.fertilisers));
                    break;
                case Constants.LIST_PESTICIDES:
                    jsonBody.put("category", "PES");
                    getSupportActionBar().setTitle(R.string.pesticides);
                    break;
                case Constants.LIST_EQUIPMENTS:
                    jsonBody.put("category", "EQP");
                    getSupportActionBar().setTitle(R.string.equipments);
                    break;
                case Constants.LIST_OTHERS:
                    jsonBody.put("category", "OTH");
                    getSupportActionBar().setTitle(R.string.others);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("category", String.valueOf(jsonBody));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        ItemsResponse[] itemsResponses = gson.fromJson(response.getJSONArray("data").toString(), ItemsResponse[].class);
                        handleItemsResponse(itemsResponses);
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListItemsActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };
        dialog = new ACProgressFlower.Builder(ListItemsActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        JsonObjectRequest itemsListRequest = new JsonObjectRequest(Request.Method.POST, itemsUrl, jsonBody, responseListener, errorListener){
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(ListItemsActivity.this));
                return params;
            }
        };
        VolleySingleton.getInstance(ListItemsActivity.this).addToRequestQueue(itemsListRequest);
    }

    private void handleItemsResponse(ItemsResponse[] response) {
        if(response.length == 0) {
            textViewEmptyList.setVisibility(View.VISIBLE);
        } else {
            textViewEmptyList.setVisibility(View.GONE);
            catalogueAdapterArrayList.clear();
            catalogueAdapterArrayList.addAll(Arrays.asList(response));
            listItemsRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
