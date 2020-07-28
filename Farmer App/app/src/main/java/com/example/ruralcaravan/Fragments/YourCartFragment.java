package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class YourCartFragment extends Fragment {

    private View rootView;
    private Button button1;
    private Button button2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_your_cart, container, false);

        button1 = rootView.findViewById(R.id.button1);
        button2 = rootView.findViewById(R.id.button2);

        String cartItemsUrl = getResources().getString(R.string.base_end_point_ip) + "kart/";
        Log.e("cartItemsUrl", cartItemsUrl);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("cartItemsResponse", response.toString());
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JsonObjectRequest cartItemsRequest = new JsonObjectRequest(Request.Method.GET, cartItemsUrl, null, responseListener, errorListener){
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(cartItemsRequest);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCartItem();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCartItem();
            }
        });

        return rootView;
    }

    public void updateCartItem() {
        Log.e("Btn1", "Update cart item button pressed");

        String url = getResources().getString(R.string.base_end_point_ip) + "kart/";
        JSONObject jsonBody = new JSONObject();
        //Hard coded for now
        try {
            jsonBody.put("id", 4);
            jsonBody.put("quantity", 7);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("updateCartItemsResponse", response.toString());
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JsonObjectRequest updateCartItemRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, responseListener, errorListener){
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(updateCartItemRequest);
    }

    public void deleteCartItem() {
        Log.e("Btn2", "Delete cart item button pressed");

        String url = getResources().getString(R.string.base_end_point_ip) + "kart/delete/";
        Log.e("deleteUrl", url);
        JSONObject jsonBody = new JSONObject();
//        Hard coded for now
        try {
            jsonBody.put("id", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("deleteCartItemResponse", response.toString());
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JsonObjectRequest deleteCartItemRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(deleteCartItemRequest);
    }

}
