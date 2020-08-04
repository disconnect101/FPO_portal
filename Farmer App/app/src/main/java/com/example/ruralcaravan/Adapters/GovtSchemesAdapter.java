package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.Activities.GovtSchemeDetailsActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.GovtSchemesResponse;
import com.example.ruralcaravan.Utilities.Constants;

import java.util.ArrayList;

public class GovtSchemesAdapter extends RecyclerView.Adapter<GovtSchemesAdapter.GovtSchemesViewHolder> {

    private Context context;
    private ArrayList<GovtSchemesResponse> govtSchemesResponses;

    public GovtSchemesAdapter(Context context, ArrayList<GovtSchemesResponse> govtSchemesResponses) {
        this.context = context;
        this.govtSchemesResponses = govtSchemesResponses;
    }

    @NonNull
    @Override
    public GovtSchemesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.govt_schemes_row_layout, parent, false);
        GovtSchemesAdapter.GovtSchemesViewHolder govtSchemesViewHolder = new GovtSchemesViewHolder(view);
        return govtSchemesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GovtSchemesViewHolder holder, int position) {
        final GovtSchemesResponse govtScheme = govtSchemesResponses.get(position);
        holder.textViewGovtSchemeName.setText(govtScheme.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GovtSchemeDetailsActivity.class);
                intent.putExtra(Constants.GOVT_SCHEME_NAME, govtScheme.getName());
                intent.putExtra(Constants.GOVT_SCHEME_DETAILS, govtScheme.getDescription());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return govtSchemesResponses.size();
    }

    public class GovtSchemesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewGovtSchemeName;
        private CardView cardView;
        public GovtSchemesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGovtSchemeName = itemView.findViewById(R.id.textViewGovtSchemeName);
            cardView = itemView.findViewById(R.id.cardViewGovtScheme);
        }
    }
}
