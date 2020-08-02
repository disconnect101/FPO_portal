package com.example.ruralcaravan.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.ruralcaravan.Adapters.CartItemsAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.CartItemsResponse;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;
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


public class YourCartFragment extends Fragment implements CartItemsAdapter.OnItemDeleteListener, CartItemsAdapter.OnQuantityDecreaseListener, CartItemsAdapter.OnQuantityIncreaseListener {

    private View rootView;
    private RecyclerView recyclerViewCart;
    private ArrayList<CartItemsResponse> cartItemsAdapterArrayList;
    private int jsonResponseSize;
    private CartItemsResponse[] cartItems;
    private TextView proceedToBuy;
    private int paymentMode;
    private ACProgressFlower dialog;
    private TextView textViewTotal;
    private TextView textViewEmptyCartMessage;
    private String total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_your_cart, container, false);
        rootView.setVisibility(View.INVISIBLE);

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        recyclerViewCart = rootView.findViewById(R.id.recyclerViewCart);
        proceedToBuy = rootView.findViewById(R.id.proceedToBuy);
        textViewEmptyCartMessage = rootView.findViewById(R.id.textViewEmptyCartMessage);

        textViewTotal = rootView.findViewById(R.id.textViewTotal);

        recyclerViewCart = rootView.findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerDaily = new LinearLayoutManager(getActivity());
        recyclerViewCart.setLayoutManager(linearLayoutManagerDaily);
        cartItemsAdapterArrayList = new ArrayList<>();
        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(getActivity(), cartItemsAdapterArrayList, this, this, this);
        recyclerViewCart.setAdapter(cartItemsAdapter);
        recyclerViewCart.setOverScrollMode(View.OVER_SCROLL_NEVER);

        proceedToBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.confirm_order))
                        .setMessage(getString(R.string.confirm_order_message) + "\n\n" + getString(R.string.amount) + ": \u20B9" + total)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                choosePaymentMethod();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_confirm_order)
                        .show();
                }
            });

        String cartItemsUrl = getString(R.string.base_end_point_ip) + "kart/";
        Log.e("cartItemsUrl", cartItemsUrl);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    cartItems = gson.fromJson(response.getJSONArray("data").toString(), CartItemsResponse[].class);
                    jsonResponseSize = cartItems.length;
                    if(jsonResponseSize == 0) {
                        textViewEmptyCartMessage.setVisibility(View.VISIBLE);
                        proceedToBuy.setVisibility(View.GONE);
                        rootView.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    } else {
                        textViewEmptyCartMessage.setVisibility(View.GONE);
                        total = response.getString("total");
                        textViewTotal.setText(getString(R.string.total) + " \u20B9" + total);
                        handleResponse(cartItems);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                dialog.dismiss();
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
            fetchNameImageAndRate(cartItems[i]);
        }
    }

    private void updateRecyclerView() {
        cartItemsAdapterArrayList.clear();
        cartItemsAdapterArrayList.addAll(Arrays.asList(cartItems));
        recyclerViewCart.getAdapter().notifyDataSetChanged();
    }

    private void fetchNameImageAndRate(final CartItemsResponse cartItem) {
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
                    cartItem.setRate(itemDetailedResponse.getRate().toString());
                    --jsonResponseSize;
                    if(jsonResponseSize == 0) {
                        updateRecyclerView();
                        dialog.dismiss();
                        rootView.setVisibility(View.VISIBLE);
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
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

    public void updateCartItem(String cartId, final TextView textViewQuantity, final int change) {

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String quantityUpdateUrl = getResources().getString(R.string.base_end_point_ip) + "kart/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", cartId);
            jsonBody.put("quantity", String.valueOf(Integer.parseInt(textViewQuantity.getText().toString()) + change));
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                            textViewQuantity.setText(String.valueOf(Integer.parseInt(textViewQuantity.getText().toString()) + change));
                        } else {
                            Toast.makeText(getContext(), ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG);
                        }
                        dialog.dismiss();
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            };
            JsonObjectRequest updateCartItemRequest = new JsonObjectRequest(Request.Method.POST, quantityUpdateUrl, jsonBody, responseListener, errorListener) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                    return params;
                }
            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(updateCartItemRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteCartItem(String cartId, final int position) {

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String cartDeleteUrl = getResources().getString(R.string.base_end_point_ip) + "kart/delete/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", cartId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        cartItemsAdapterArrayList.remove(position);
                        if(cartItemsAdapterArrayList.size() == 0) {
                            proceedToBuy.setVisibility(View.GONE);
                            textViewEmptyCartMessage.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewCart.getAdapter().notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                ResponseStatusCodeHandler.getMessage(response.getString("statuscode")),
                                Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        JsonObjectRequest deleteCartItemRequest = new JsonObjectRequest(Request.Method.POST, cartDeleteUrl, jsonBody, responseListener, errorListener){
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
    public void onItemDelete(String cartId, int position) {
        deleteCartItem(cartId, position);
    }

    @Override
    public void onQuantityDecreased(String cartId, TextView textViewQuantity, Button buttonDecreaseQuantity, int position) {
        if(textViewQuantity.getText().toString().equals("1")) {
            deleteCartItem(cartId, position);
        } else {
            if(textViewQuantity.getText().toString().equals("2")) {
                buttonDecreaseQuantity.setBackground(getActivity().getDrawable(R.drawable.ic_delete));
            }
            updateCartItem(cartId, textViewQuantity, -1);
        }
    }

    @Override
    public void onQuantityIncreased(String cartId, final TextView textViewQuantity, Button buttonDecreaseQuantity, Button buttonIncreaseQuantity) {
        if(textViewQuantity.getText().toString().equals("1")) {
            buttonDecreaseQuantity.setBackground(buttonIncreaseQuantity.getBackground());
        }
        updateCartItem(cartId, textViewQuantity, 1);
    }

    private void choosePaymentMethod() {
        paymentMode = Constants.PAYMENT_COD;
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle(getString(R.string.payment_mode));
        builder.setSingleChoiceItems(new String[]{getString(R.string.cash_on_delivery), getString(R.string.pay_with_account), getString(R.string.pay_at_FPO_office)}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                paymentMode = which;
            }
        });
        builder.addButton(getString(R.string.pay), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                buyItem();
            }
        });
        builder.addButton(getString(R.string.cancel), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void buyItem() {

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String buyItemsUrl = getString(R.string.base_end_point_ip) + "order/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", "-1");
            switch (paymentMode) {
                case Constants.PAYMENT_COD:
                    jsonBody.put("type", "COD");
                    break;
                case Constants.PAYMENT_PEW:
                    jsonBody.put("type", "PEW");
                    break;
                case Constants.PAYMENT_CAS:
                    jsonBody.put("type", "CAS");
                    break;
            }
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
                        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
                        if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                            cartItemsAdapterArrayList.clear();
                            recyclerViewCart.getAdapter().notifyDataSetChanged();
                            textViewTotal.setVisibility(View.GONE);
                            proceedToBuy.setVisibility(View.GONE);
                            textViewEmptyCartMessage.setVisibility(View.VISIBLE);
                            builder.setMessage(getString(R.string.order_successful));
                        } else {
                            builder.setMessage(ResponseStatusCodeHandler.getMessage(response.getString("statuscode")));
                        }
                        builder.show();
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            };
            JsonObjectRequest buyItemRequest = new JsonObjectRequest(Request.Method.POST, buyItemsUrl, jsonBody, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                    return params;
                }
            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(buyItemRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
