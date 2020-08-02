package com.example.ruralcaravan.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.ruralcaravan.Activities.PlanActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.PlansResponse;
import com.example.ruralcaravan.Utilities.ResponseStatusCodeHandler;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class PlanDetailsFragment extends Fragment {

    private View rootView;
    private TextView textViewPlanName;
    private ImageView imageViewPlan;
    private TextView textViewMaximumCapacity;
    private TextView textViewPercentageAcquired;
    private TextView textViewSubscriberCount;
    private TextView textViewGuidance;
    private TextView textViewFacilities;
    private TextView textViewInvestments;
    private Button buttonPlanStatus;
    private PlansResponse planDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_plan_details, container, false);

        textViewPlanName = rootView.findViewById(R.id.textViewPlanName);
        imageViewPlan = rootView.findViewById(R.id.imageViewPlan);
        textViewMaximumCapacity = rootView.findViewById(R.id.textViewMaximumCapacity);
        textViewPercentageAcquired = rootView.findViewById(R.id.textViewPercentageAcquired);
        textViewSubscriberCount = rootView.findViewById(R.id.textViewSubscriberCount);
        textViewGuidance = rootView.findViewById(R.id.textViewGuidance);
        textViewFacilities = rootView.findViewById(R.id.textViewFacilities);
        textViewInvestments = rootView.findViewById(R.id.textViewInvestments);
        buttonPlanStatus = rootView.findViewById(R.id.buttonPlanStatus);

        planDetails = ((PlanActivity)getActivity()).getPlanDetails();

        textViewPlanName.setText(planDetails.getName());
        Glide.with(getActivity())
                .load(getActivity().getString(R.string.socket_address) + planDetails.getImage())
                .placeholder(R.drawable.app_logo)
                .into(imageViewPlan);
        textViewMaximumCapacity.setText(planDetails.getMaxCap().toString());
        double acquiredPercentage = 100.0*planDetails.getCurrentAmount()/planDetails.getMaxCap();
        textViewPercentageAcquired.setText(String.format("%.2f", acquiredPercentage) + "%");
        textViewSubscriberCount.setText(planDetails.getSubscribers().toString());
        textViewGuidance.setText(Html.fromHtml(planDetails.getGuidance()));
        textViewFacilities.setText(Html.fromHtml(planDetails.getFacilities()));
        textViewInvestments.setText(Html.fromHtml(planDetails.getInvestments()));

        setButtonState(((PlanActivity)getActivity()).getIsPlanSubscribed());

        buttonPlanStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.confirmation))
                        .setMessage(getString(R.string.are_you_sure_you_want_to) + ((((PlanActivity)getActivity()).getIsPlanSubscribed()) ? " unsubscribe" : " subscribe") + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(!((PlanActivity)getActivity()).getIsPlanSubscribed()) {
                                    enterLandArea();
                                } else {
                                    notifyServerAboutSubscription(false, null);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        return rootView;
    }

    private void setButtonState(boolean isPlanSubscribed) {
        if(isPlanSubscribed) {
            buttonPlanStatus.setText(getString(R.string.unsubscribe).toUpperCase());
            buttonPlanStatus.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            buttonPlanStatus.setText(getString(R.string.subscribe).toUpperCase());
            buttonPlanStatus.setBackgroundColor(getResources().getColor(R.color.green));
        }
        ((PlanActivity)getActivity()).setIsPlanSubscribed(isPlanSubscribed);
    }

    private void enterLandArea() {
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.enter_land_area_in_bigha))
                .setNeutralButton(getString(R.string.subscribe), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyServerAboutSubscription(true, input.getText().toString());
                    }
                })
                .setView(input)
                .show();
    }

    private void notifyServerAboutSubscription(final boolean status, String landArea) {

        final ACProgressFlower dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String updatePlanSubscriptionUrl = getString(R.string.base_end_point_ip) + "confcrop/" + planDetails.getId() + "/";
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(ResponseStatusCodeHandler.isSuccessful(response.getString("statuscode"))) {
                        setButtonState(status);
                    } else {
                        Toast.makeText(getActivity(), ResponseStatusCodeHandler.getMessage(response.getString("statuscode")), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
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
        JsonObjectRequest updatePlanSubscriptionRequest = null;
        if(status) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("landarea", landArea);
                updatePlanSubscriptionRequest = new JsonObjectRequest(Request.Method.POST, updatePlanSubscriptionUrl, jsonBody, responseListener, errorListener){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> params = new HashMap<>();
                        params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                        return params;
                    }
                };
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            updatePlanSubscriptionRequest = new JsonObjectRequest(Request.Method.DELETE, updatePlanSubscriptionUrl, null, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(getActivity()));
                    return params;
                }
            };
        }
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(updatePlanSubscriptionRequest);
    }

}
