package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.Activities.PlanActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.PlansResponse;

import java.util.ArrayList;

import cc.cloudist.acplibrary.ACProgressFlower;

public class ListPlansAdapter extends RecyclerView.Adapter<ListPlansAdapter.PlansViewHolder> {

    private Context context;
    private ArrayList<PlansResponse> plansResponseArrayList;
    private boolean isPlanSubscribed;

    public ListPlansAdapter(Context context, ArrayList<PlansResponse> plansResponseArrayList) {
        this.context = context;
        this.plansResponseArrayList = plansResponseArrayList;
        this.isPlanSubscribed = false;
    }

    @NonNull
    @Override
    public PlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_image_row_layout, parent, false);
        ListPlansAdapter.PlansViewHolder plansViewHolder = new PlansViewHolder(view);
        return plansViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlansViewHolder holder, int position) {
        final PlansResponse plan = plansResponseArrayList.get(position);
        Glide.with(context)
                .load(context.getString(R.string.socket_address) + plan.getImage())
                .placeholder(R.drawable.app_logo)
                .into(holder.imageViewImage);
        holder.textViewName.setText(plan.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("planId", plan.getId().toString());
                intent.putExtra("isPlanSubscribed", isPlanSubscribed);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plansResponseArrayList.size();
    }

    public class PlansViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewImage;
        private TextView textViewName;
        private CardView cardView;
        public PlansViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewImage = itemView.findViewById(R.id.imageViewImage);
            textViewName = itemView.findViewById(R.id.textViewName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void setIsPlanSubscribed(boolean state) {
        this.isPlanSubscribed = state;
    }

}
