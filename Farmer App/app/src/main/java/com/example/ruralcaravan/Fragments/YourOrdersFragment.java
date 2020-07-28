package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class YourOrdersFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_your_orders, container, false);

        String cartItemsUrl = getResources().getString(R.string.base_end_point_ip) + "order/";
        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("id","1"); //product_id
//            jsonBody.put("quantity","50");
//            jsonBody.put("type", "COD");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
        JsonObjectRequest cartItemsRequest = new JsonObjectRequest(Request.Method.GET, cartItemsUrl, jsonBody, responseListener, errorListener){
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(cartItemsRequest);

        return rootView;
    }

}
