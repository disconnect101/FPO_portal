package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.PlansResponse;

import java.util.ArrayList;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlansViewHolder> {

    private Context context;
    private ArrayList<PlansResponse> plansResponseArrayList;

    public PlansAdapter(Context context, ArrayList<PlansResponse> plansResponseArrayList) {
        this.context = context;
        this.plansResponseArrayList = plansResponseArrayList;
    }

    @NonNull
    @Override
    public PlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_image_row_layout, parent, false);
        PlansAdapter.PlansViewHolder plansViewHolder = new PlansViewHolder(view);
        return plansViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlansViewHolder holder, int position) {
        PlansResponse plan = plansResponseArrayList.get(position);
        Glide.with(context)
                .load(context.getString(R.string.socket_address) + plan.getImage())
                .placeholder(R.drawable.app_logo)
                .into(holder.imageViewImage);
        holder.textViewName.setText(plan.getName());
    }

    @Override
    public int getItemCount() {
        return plansResponseArrayList.size();
    }

    public class PlansViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewImage;
        private TextView textViewName;
        public PlansViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewImage = itemView.findViewById(R.id.imageViewImage);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}
