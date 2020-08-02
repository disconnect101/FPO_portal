package com.example.ruralcaravan.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Activities.MainActivity;
import com.example.ruralcaravan.Adapters.BalanceSheetAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.BalanceSheetResponse;
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

public class BalanceSheetFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerViewBalanceSheet;
    private ArrayList<BalanceSheetResponse> balanceSheetAdapterArrayList;
    private ACProgressFlower dialog;
    private TextView textViewAvailableBalance;
    private TextView textViewNoTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_balance_sheet, container, false);
        textViewAvailableBalance = rootView.findViewById(R.id.textViewAvailableBalance);
        recyclerViewBalanceSheet = rootView.findViewById(R.id.recyclerViewBalanceSheet);
        textViewNoTransaction = rootView.findViewById(R.id.textViewNoTransactions);

        recyclerViewBalanceSheet.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewBalanceSheet.setLayoutManager(linearLayoutManager);

        balanceSheetAdapterArrayList = new ArrayList<>();
        BalanceSheetAdapter balanceSheetAdapter = new BalanceSheetAdapter(getActivity(), balanceSheetAdapterArrayList);
        recyclerViewBalanceSheet.setAdapter(balanceSheetAdapter);

        String balanceSheetUrl = getString(R.string.base_end_point_ip) + "balancesheet/";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        textViewAvailableBalance.setText(response.getJSONObject("current_amount").getString("amount"));
                        BalanceSheetResponse[] balanceSheet = gson.fromJson(response.getJSONArray("balancesheet").toString(), BalanceSheetResponse[].class);
                        handleResponse(balanceSheet);
                    } else {
                        Toast.makeText(getActivity(), ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        JsonObjectRequest balanceSheetRequest = new JsonObjectRequest(Request.Method.GET, balanceSheetUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(balanceSheetRequest);
        return rootView;
    }

    private void handleResponse(BalanceSheetResponse[] balanceSheet) {
        if(balanceSheet.length == 0) {
            textViewNoTransaction.setVisibility(View.VISIBLE);
        } else {
            balanceSheetAdapterArrayList.clear();
            balanceSheetAdapterArrayList.addAll(Arrays.asList(balanceSheet));
            recyclerViewBalanceSheet.getAdapter().notifyDataSetChanged();
        }
    }
}
