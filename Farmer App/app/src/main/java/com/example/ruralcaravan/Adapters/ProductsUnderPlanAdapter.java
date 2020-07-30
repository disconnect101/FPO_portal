package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.Activities.ItemDetailsActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;
import com.example.ruralcaravan.Utilities.Constants;

import java.util.List;

public class ProductsUnderPlanAdapter extends RecyclerView.Adapter<ProductsUnderPlanAdapter.ProductsUnderPlanViewHolder> {

    private Context context;
    private List<ItemDetailedResponse> itemDetailedResponses;

    public ProductsUnderPlanAdapter(Context context, List<ItemDetailedResponse> itemDetailedResponses) {
        this.context = context;
        this.itemDetailedResponses = itemDetailedResponses;
    }

    @NonNull
    @Override
    public ProductsUnderPlanAdapter.ProductsUnderPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogue_row_layout, parent, false);
        ProductsUnderPlanAdapter.ProductsUnderPlanViewHolder productsUnderPlanViewHolder = new ProductsUnderPlanViewHolder(view);
        return productsUnderPlanViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsUnderPlanAdapter.ProductsUnderPlanViewHolder holder, int position) {
        final ItemDetailedResponse item = itemDetailedResponses.get(position);
        Glide.with(context)
                .load(context.getResources().getString(R.string.socket_address) + "/" + item.getImage() + "/")
                .placeholder(R.drawable.app_logo)
                .into(holder.imageViewItem);
        holder.textViewItemName.setText(item.getName());
        holder.textViewItemRate.setText(item.getRate().toString());
        holder.cardViewCatalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT_ID, item.getId().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDetailedResponses.size();
    }

    public class ProductsUnderPlanViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewItem;
        private TextView textViewItemName;
        private TextView textViewItemRate;
        private CardView cardViewCatalogue;
        public ProductsUnderPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemRate = itemView.findViewById(R.id.textViewItemRate);
            cardViewCatalogue = itemView.findViewById(R.id.cardViewCatalogue);
        }
    }
}
