package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ruralcaravan.Adapters.CartItemsAdapter;
import com.example.ruralcaravan.Adapters.DailyWeatherAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.CartItemsResponse;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;
import com.example.ruralcaravan.ResponseClasses.OrdersResponse;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class YourCartFragment extends Fragment implements CartItemsAdapter.OnItemDeleteListener, CartItemsAdapter.OnQuantityDecreaseListener, CartItemsAdapter.OnQuantityIncreaseListener {

    private View rootView;
    private RecyclerView recyclerViewCart;
    private ArrayList<CartItemsResponse> cartItemsAdapterArrayList;
    private int jsonResponseSize;
    private CartItemsResponse[] cartItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_your_cart, container, false);
        recyclerViewCart = rootView.findViewById(R.id.recyclerViewCart);

        recyclerViewCart = rootView.findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerDaily = new LinearLayoutManager(getActivity());
        recyclerViewCart.setLayoutManager(linearLayoutManagerDaily);
        cartItemsAdapterArrayList = new ArrayList<>();
        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(getActivity(), cartItemsAdapterArrayList, this, this, this);
        recyclerViewCart.setAdapter(cartItemsAdapter);
        recyclerViewCart.setOverScrollMode(View.OVER_SCROLL_NEVER);

        String cartItemsUrl = getResources().getString(R.string.base_end_point_ip) + "kart/";
        Log.e("cartItemsUrl", cartItemsUrl);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    cartItems = gson.fromJson(response.getJSONArray("data").toString(), CartItemsResponse[].class);
                    jsonResponseSize = cartItems.length;
                    handleResponse(cartItems);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JsonObjectRequest cartItemsRequest = new JsonObjectRequest(Request.Method.GET, cartItemsUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(cartItemsRequest);
        return rootView;
    }

    private void handleResponse(CartItemsResponse[] cartItems) {
        for(int i=0;i<cartItems.length;i++) {
            fetchNameAndImage(cartItems[i]);
        }
    }

    private void updateRecyclerView() {
        cartItemsAdapterArrayList.clear();
        cartItemsAdapterArrayList.addAll(Arrays.asList(cartItems));
        recyclerViewCart.getAdapter().notifyDataSetChanged();
    }

    private void fetchNameAndImage(final CartItemsResponse cartItem) {
        final String itemDetailsUrl = getResources().getString(R.string.base_end_point_ip) + "product/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", cartItem.getItemId());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    ItemDetailedResponse itemDetailedResponse = gson.fromJson(response.toString(), ItemDetailedResponse.class);
                    cartItem.setName(itemDetailedResponse.getName());
                    cartItem.setImage(itemDetailedResponse.getImage());
                    --jsonResponseSize;
                    if(jsonResponseSize == 0) {
                        updateRecyclerView();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            };
            JsonObjectRequest itemDetailsRequest = new JsonObjectRequest(Request.Method.POST, itemDetailsUrl, jsonBody, responseListener, errorListener) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                    return params;
                }
            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(itemDetailsRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onItemDelete(String cartId) {
        Log.e("delete", "Delete pressed");
    }

    @Override
    public void onQuantityDecreased(String cartId, String quantity) {
        Log.e("quantity", "decreased");
    }

    @Override
    public void onQuantityIncreased(String cartId, String quantity) {
        Log.e("quantity", "increased");
    }
}
