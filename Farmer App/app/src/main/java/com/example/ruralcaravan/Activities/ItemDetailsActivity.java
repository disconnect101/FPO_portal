package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailsActivity extends AppCompatActivity {

    private ImageView imageViewItem;
    private TextView textViewItemName;
    private TextView textViewItemRate;
    private TextView textViewItemDescription;
    private TextView textViewQuantity;
    private String productId;

    private Button buttonDecreaseQuantity;

    private int paymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        imageViewItem = findViewById(R.id.imageViewItem);
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemRate = findViewById(R.id.textViewItemRate);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        buttonDecreaseQuantity = findViewById(R.id.buttonDecreaseQuantity);

        if(textViewQuantity.getText().toString().equals("0")) {
            buttonDecreaseQuantity.setEnabled(false);
        }

        //place orders options
        Intent intent = getIntent();
        productId = intent.getStringExtra(Constants.KEY_PRODUCT_ID);
        String itemDetailsUrl = getResources().getString(R.string.base_end_point_ip) + "product/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id",productId);
            Log.e("json", jsonBody.toString());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    ItemDetailedResponse itemDetailedResponse = gson.fromJson(response.toString(), ItemDetailedResponse.class);
                    handleResponse(itemDetailedResponse);
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            };
            JsonObjectRequest itemDetailsRequest = new JsonObjectRequest(Request.Method.POST, itemDetailsUrl, jsonBody, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(ItemDetailsActivity.this));
                    return params;
                }
            };
            VolleySingleton.getInstance(ItemDetailsActivity.this).addToRequestQueue(itemDetailsRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(final ItemDetailedResponse itemDetailedResponse) {
        Log.e("response", itemDetailedResponse.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(ItemDetailsActivity.this)
                        .load(getResources().getString(R.string.socket_address) + "/" + itemDetailedResponse.getImage())
                        .placeholder(R.drawable.app_logo)
                        .into(imageViewItem);
                textViewItemName.setText(itemDetailedResponse.getName());
                textViewItemRate.setText(itemDetailedResponse.getRate().toString());
                textViewItemDescription.setText(Html.fromHtml(itemDetailedResponse.getDescription()));
            }
        });
    }

    public void increaseQuantityPressed(View view) {
        textViewQuantity.setText(String.valueOf(Integer.parseInt((String) textViewQuantity.getText()) + 1));
        buttonDecreaseQuantity.setEnabled(true);
    }

    public void decreaseQuantityPressed(View view) {
        textViewQuantity.setText(String.valueOf(Integer.parseInt((String) textViewQuantity.getText()) - 1));
        if(textViewQuantity.getText().toString().equals("0")) {
            buttonDecreaseQuantity.setEnabled(false);
        }
    }

    public void buyNowPressed(View view) {
        if(textViewQuantity.getText().toString().equals("0")) {
            makeMeShake(textViewQuantity, 20, 5);
            return;
        }

        int quantity = Integer.parseInt(textViewQuantity.getText().toString());
        double rate = Double.parseDouble(textViewItemRate.getText().toString());
        double amount = quantity * rate;

        new AlertDialog.Builder(ItemDetailsActivity.this)
                .setTitle(getString(R.string.confirm_order))
                .setMessage(getString(R.string.confirm_order_message) + "\n\n" + getString(R.string.amount) + ": \u20B9" + amount)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        choosePaymentMethod();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_confirm_order)
                .show();
    }

    private void choosePaymentMethod() {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ItemDetailsActivity.this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle(getString(R.string.payment_mode));
        builder.setSingleChoiceItems(new String[]{getString(R.string.cash_on_delivery), getString(R.string.pay_with_e_wallet), getString(R.string.pay_at_FPO_office)}, 0, new DialogInterface.OnClickListener() {
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
        String buyItemUrl = getString(R.string.base_end_point_ip) + "order/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", productId);
            jsonBody.put("quantity", textViewQuantity.getText().toString());
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
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ItemDetailsActivity.this);
                        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
                        if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                            builder.setMessage(getString(R.string.order_successful));
                        } else {
                            builder.setMessage(ResponseStatusCodeHandler.getMessage(response.getString("statuscode")));
                        }
                        builder.show();
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
            JsonObjectRequest buyItemRequest = new JsonObjectRequest(Request.Method.POST, buyItemUrl, jsonBody, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(ItemDetailsActivity.this));
                    return params;
                }
            };
            VolleySingleton.getInstance(ItemDetailsActivity.this).addToRequestQueue(buyItemRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addToCartPressed(View view) {
        if(textViewQuantity.getText().toString().equals("0")) {
            makeMeShake(textViewQuantity, 20, 5);
            return;
        }
        String addToCartUrl = getResources().getString(R.string.base_end_point_ip) + "kart/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", productId);
            jsonBody.put("quantity", textViewQuantity.getText());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ItemDetailsActivity.this);
                        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
                        if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                            builder.setMessage(getString(R.string.item_added_to_cart));
                        } else {
                            builder.setMessage(ResponseStatusCodeHandler.getMessage(response.getString("statuscode")));
                        }
                        builder.show();
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
            JsonObjectRequest addToCartRequest = new JsonObjectRequest(Request.Method.PUT, addToCartUrl, jsonBody, responseListener, errorListener) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(ItemDetailsActivity.this));
                    return params;
                }
            };
            VolleySingleton.getInstance(ItemDetailsActivity.this).addToRequestQueue(addToCartRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static View makeMeShake(View view, int duration, int offset) {
        Animation anim = new TranslateAnimation(-offset,offset,0,0);
        anim.setDuration(duration);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(5);
        view.startAnimation(anim);
        return view;
    }

}
