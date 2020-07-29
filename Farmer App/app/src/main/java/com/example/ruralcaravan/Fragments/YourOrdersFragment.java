package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Adapters.OrdersAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;
import com.example.ruralcaravan.ResponseClasses.OrdersResponse;
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

public class YourOrdersFragment extends Fragment {

    private View rootView;
    private TextView textViewDeliveredOrders;
    private TextView textViewPendingOrders;
    private RecyclerView recyclerViewOrders;
    private ArrayList<OrdersResponse> ordersAdapterArrayList;
    private OrdersResponse[] deliveredOrders;
    private OrdersResponse[] pendingOrders;
    private int previousSelection;
    private int size;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_your_orders, container, false);

        textViewDeliveredOrders = rootView.findViewById(R.id.textViewDeliveredOrders);
        textViewPendingOrders = rootView.findViewById(R.id.textViewPendingOrders);
        recyclerViewOrders = rootView.findViewById(R.id.recyclerViewOrders);

        recyclerViewOrders = rootView.findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerDaily = new LinearLayoutManager(getActivity());
        recyclerViewOrders.setLayoutManager(linearLayoutManagerDaily);
        ordersAdapterArrayList = new ArrayList<>();
        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), ordersAdapterArrayList);
        recyclerViewOrders.setAdapter(ordersAdapter);
        recyclerViewOrders.setOverScrollMode(View.OVER_SCROLL_NEVER);

        previousSelection = Constants.PENDING_ORDERS;

        textViewDeliveredOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecyclerView(Constants.DELIVERED_ORDERS);
            }
        });

        textViewPendingOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecyclerView(Constants.PENDING_ORDERS);
            }
        });

        String ordersUrl = getResources().getString(R.string.base_end_point_ip) + "order/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        deliveredOrders = gson.fromJson(response.getJSONArray("completed_orders").toString(), OrdersResponse[].class);
                        pendingOrders = gson.fromJson(response.getJSONArray("pending_orders").toString(), OrdersResponse[].class);
                        handleResponse();
                    } else {
                        //handle exception
                    }
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
        JsonObjectRequest ordersRequest = new JsonObjectRequest(Request.Method.GET, ordersUrl, null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(ordersRequest);
        return rootView;
    }

    private void updateRecyclerView(int id) {
        if(id == Constants.DELIVERED_ORDERS && previousSelection == Constants.PENDING_ORDERS) {
            ordersAdapterArrayList.clear();
            ordersAdapterArrayList.addAll(Arrays.asList(deliveredOrders));
            recyclerViewOrders.getAdapter().notifyDataSetChanged();
            previousSelection = Constants.DELIVERED_ORDERS;
            textViewPendingOrders.setBackground(null);
            textViewDeliveredOrders.setBackground(getActivity().getDrawable(R.drawable.bottom_border));
        } else if(id == Constants.PENDING_ORDERS && previousSelection == Constants.DELIVERED_ORDERS) {
            ordersAdapterArrayList.clear();
            ordersAdapterArrayList.addAll(Arrays.asList(pendingOrders));
            recyclerViewOrders.getAdapter().notifyDataSetChanged();
            previousSelection = Constants.PENDING_ORDERS;
            textViewDeliveredOrders.setBackground(null);
            textViewPendingOrders.setBackground(getActivity().getDrawable(R.drawable.bottom_border));
        }
    }

    private void handleResponse() {
        size = deliveredOrders.length + pendingOrders.length;
        for (int i = 0; i < deliveredOrders.length; i++) {
            fetchNameAndImage(deliveredOrders[i]);
        }
        for (int i = 0; i < pendingOrders.length; i++) {
            fetchNameAndImage(pendingOrders[i]);
        }
    }

    private void fetchNameAndImage(final OrdersResponse order) {
        final String itemDetailsUrl = getResources().getString(R.string.base_end_point_ip) + "product/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", order.getItemId());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    ItemDetailedResponse itemDetailedResponse = gson.fromJson(response.toString(), ItemDetailedResponse.class);
                    order.setName(itemDetailedResponse.getName());
                    order.setImage(itemDetailedResponse.getImage());
                    --size;
                    if(size == 0) {
                        updateRecyclerView(Constants.DELIVERED_ORDERS);
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

}
