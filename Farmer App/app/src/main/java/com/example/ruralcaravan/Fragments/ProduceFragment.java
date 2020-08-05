package com.example.ruralcaravan.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Activities.MainActivity;
import com.example.ruralcaravan.Adapters.MeetingsAdapter;
import com.example.ruralcaravan.Adapters.ProduceAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ProduceResponse;
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

public class ProduceFragment extends Fragment {

    private View rootView;
    private ACProgressFlower dialog;
    private RecyclerView recyclerViewProduce;
    private ArrayList<ProduceResponse> produceAdapterArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_produce, container, false);

        recyclerViewProduce = rootView.findViewById(R.id.recyclerViewProduce);
        recyclerViewProduce.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewProduce.setLayoutManager(linearLayoutManager);
        produceAdapterArrayList = new ArrayList<>();
        ProduceAdapter produceAdapter = new ProduceAdapter(getActivity(), produceAdapterArrayList);
        recyclerViewProduce.setAdapter(produceAdapter);

        String produceUrl = getString(R.string.base_end_point_ip) + "produce/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        ProduceResponse[] produceResponses = gson.fromJson(response.getJSONArray("data").toString(), ProduceResponse[].class);
                        handlerResponse(produceResponses);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(),
                                ResponseStatusCodeHandler.getMessage(response.getString("statuscode")),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
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
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        JsonObjectRequest produceRequest = new JsonObjectRequest(Request.Method.GET, produceUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(produceRequest);
        return rootView;
    }

    private void handlerResponse(ProduceResponse[] produceResponses) {
        produceAdapterArrayList.clear();
        produceAdapterArrayList.addAll(Arrays.asList(produceResponses));
        recyclerViewProduce.getAdapter().notifyDataSetChanged();
    }

}
