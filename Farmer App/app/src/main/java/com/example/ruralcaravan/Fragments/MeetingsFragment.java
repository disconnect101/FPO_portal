package com.example.ruralcaravan.Fragments;

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
import com.example.ruralcaravan.Adapters.MeetingsAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.MeetingsResponse;
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
import info.hoang8f.widget.FButton;

public class MeetingsFragment extends Fragment implements MeetingsAdapter.OnMeetingAcceptedListener {

    private RecyclerView meetingsRecyclerView;
    private ArrayList<MeetingsResponse> meetingsAdapterArrayList;
    private ACProgressFlower dialog;
    private View rootView;
    private TextView textViewNoUpcomingMeetings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_meetings, container, false);
        textViewNoUpcomingMeetings = rootView.findViewById(R.id.textViewNoUpcomingMeetings);
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        rootView.setVisibility(View.INVISIBLE);
        meetingsRecyclerView = rootView.findViewById(R.id.recyclerViewMeetings);
        meetingsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        meetingsRecyclerView.setLayoutManager(linearLayoutManager);
        meetingsAdapterArrayList = new ArrayList<>();
        MeetingsAdapter meetingsAdapter = new MeetingsAdapter(getActivity(), meetingsAdapterArrayList, this);
        meetingsRecyclerView.setAdapter(meetingsAdapter);

        String meetingsUrl = getResources().getString(R.string.base_end_point_ip) + "meetings/";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        MeetingsResponse[] meetings = gson.fromJson(response.getJSONArray("data").toString(), MeetingsResponse[].class);
                        handleMeetingsResponse(meetings);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(),
                                ResponseStatusCodeHandler.getMessage(response.getString("statuscode")),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };
        JsonObjectRequest meetingsRequest = new JsonObjectRequest(Request.Method.GET, meetingsUrl,null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(meetingsRequest);
        return rootView;
    }

    private void handleMeetingsResponse(MeetingsResponse[] meetings) {
        if(meetings.length == 0) {
            textViewNoUpcomingMeetings.setVisibility(View.VISIBLE);
        } else {
            textViewNoUpcomingMeetings.setVisibility(View.GONE);
            meetingsAdapterArrayList.clear();
            meetingsAdapterArrayList.addAll(Arrays.asList(meetings));
            meetingsRecyclerView.getAdapter().notifyDataSetChanged();
        }
        rootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMeetingAccepted(int position, final FButton button) {

        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String rsvpUrl = getString(R.string.base_end_point_ip) + "meetings/rsvp/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("meetingtoken", meetingsAdapterArrayList.get(position).getMeetingtoken());
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                            button.setText(getString(R.string.accepted));
                            button.setButtonColor(getResources().getColor(R.color.orange_buy_now));
                        } else {
                            Toast.makeText(getActivity(), ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    dialog.dismiss();
                }
            };
            JsonObjectRequest rsvpRequest = new JsonObjectRequest(Request.Method.POST, rsvpUrl, jsonBody, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                    return params;
                }
            };
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(rsvpRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
