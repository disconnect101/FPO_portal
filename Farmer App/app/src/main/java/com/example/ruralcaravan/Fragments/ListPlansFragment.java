package com.example.ruralcaravan.Fragments;

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
import com.example.ruralcaravan.Adapters.ListPlansAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.PlansResponse;
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

public class ListPlansFragment extends Fragment {

    private View rootView;
    private TextView textViewNewPlans;
    private TextView textViewSubscribedPlans;
    private RecyclerView recyclerViewPlans;
    private ArrayList<PlansResponse> plansResponseAdapterArrayList;
    private int currentSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_plans, container, false);
        textViewNewPlans = rootView.findViewById(R.id.textViewNewPlans);
        textViewSubscribedPlans = rootView.findViewById(R.id.textViewSubscribedPlans);

        recyclerViewPlans = rootView.findViewById(R.id.recyclerViewPlans);
        recyclerViewPlans.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerDaily = new LinearLayoutManager(getActivity());
        recyclerViewPlans.setLayoutManager(linearLayoutManagerDaily);

        plansResponseAdapterArrayList = new ArrayList<>();
        ListPlansAdapter listPlansAdapter = new ListPlansAdapter(getActivity(), plansResponseAdapterArrayList);
        recyclerViewPlans.setAdapter(listPlansAdapter);

        currentSelection = Constants.SUBSCRIBED_PLANS;
        fetchPlans(Constants.NEW_PLANS);

        textViewNewPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSelection == Constants.SUBSCRIBED_PLANS) {
                    fetchPlans(Constants.NEW_PLANS);
                }
            }
        });

        textViewSubscribedPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSelection == Constants.NEW_PLANS) {
                    fetchPlans(Constants.SUBSCRIBED_PLANS);
                }
            }
        });
        return rootView;
    }

    private void fetchPlans(final int id) {
        String plansUrl = getString(R.string.base_end_point_ip) + "crops/0";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        PlansResponse[] plans;
                        if(id == Constants.NEW_PLANS)
                            plans = gson.fromJson(response.getJSONArray("not_subscribed").toString(), PlansResponse[].class);
                        else
                            plans = gson.fromJson(response.getJSONArray("subscriptions").toString(), PlansResponse[].class);
                        updatePlansView(plans, id);
                    } else {
                        Toast.makeText(getActivity(), ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG).show();
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
        JsonObjectRequest plansRequest = new JsonObjectRequest(Request.Method.GET, plansUrl, null, responseListener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(plansRequest);
    }

    private void updatePlansView(PlansResponse[] plans, int id) {
        updateHeader(id);
        plansResponseAdapterArrayList.clear();
        plansResponseAdapterArrayList.addAll(Arrays.asList(plans));
        ListPlansAdapter listPlansAdapter = (ListPlansAdapter) recyclerViewPlans.getAdapter();
        listPlansAdapter.notifyDataSetChanged();
        listPlansAdapter.setIsPlanSubscribed(id==1);
    }

    private void updateHeader(int id) {
        currentSelection = id;
        if(id == Constants.NEW_PLANS) {
            textViewSubscribedPlans.setBackground(null);
            textViewNewPlans.setBackground(getActivity().getDrawable(R.drawable.bottom_border));
        } else {
            textViewNewPlans.setBackground(null);
            textViewSubscribedPlans.setBackground(getActivity().getDrawable(R.drawable.bottom_border));
        }
    }

}
