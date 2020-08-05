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
import com.example.ruralcaravan.Adapters.GovtSchemesAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.GovtSchemesResponse;
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

public class GovtSchemesFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerViewGovtSchemes;
    private ACProgressFlower dialog;
    private ArrayList<GovtSchemesResponse> govtSchemesAdapterArrayList;
    private TextView textViewNoGovernmentSchemes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_govt_schemes, container, false);
        textViewNoGovernmentSchemes = rootView.findViewById(R.id.textViewNoGovtSchemes);
        recyclerViewGovtSchemes = rootView.findViewById(R.id.recyclerViewGovtSchemes);
        recyclerViewGovtSchemes.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewGovtSchemes.setLayoutManager(linearLayoutManager);
        govtSchemesAdapterArrayList = new ArrayList<>();
        GovtSchemesAdapter govtSchemesAdapter = new GovtSchemesAdapter(getActivity(), govtSchemesAdapterArrayList);
        recyclerViewGovtSchemes.setAdapter(govtSchemesAdapter);

        String govtSchemesUrl = getString(R.string.base_end_point_ip) + "govt/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        GovtSchemesResponse[] govtSchemesResponses = gson.fromJson(response.getJSONArray("data").toString(), GovtSchemesResponse[].class);
                        handleResponse(govtSchemesResponses);
                    } else {
                        Toast.makeText(getActivity(), ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG);
                    }
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

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        JsonObjectRequest govtSchemesRequest =  new JsonObjectRequest(Request.Method.GET, govtSchemesUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(govtSchemesRequest);
        return rootView;
    }

    private void handleResponse(GovtSchemesResponse[] govtSchemesResponses) {
        if(govtSchemesResponses.length == 0) {
            textViewNoGovernmentSchemes.setVisibility(View.VISIBLE);
        } else {
            govtSchemesAdapterArrayList.clear();
            govtSchemesAdapterArrayList.addAll(Arrays.asList(govtSchemesResponses));
            recyclerViewGovtSchemes.getAdapter().notifyDataSetChanged();
        }
    }

}
