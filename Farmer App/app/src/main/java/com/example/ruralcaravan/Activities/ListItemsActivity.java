package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
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

public class ListItemsActivity extends AppCompatActivity {

    private RecyclerView listItemsRecyclerView;
    private ArrayList<ItemsResponse> catalogueAdapterArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

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
                    break;
                case Constants.LIST_FERTILIZER:
                    jsonBody.put("category", "FER");
                    break;
                case Constants.LIST_PESTICIDES:
                    jsonBody.put("category", "PES");
                    break;
                case Constants.LIST_EQUIPMENTS:
                    jsonBody.put("category", "EQP");
                    break;
                case Constants.LIST_OTHERS:
                    jsonBody.put("category", "OTH");
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
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
        catalogueAdapterArrayList.clear();
        catalogueAdapterArrayList.addAll(Arrays.asList(response));
        listItemsRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
