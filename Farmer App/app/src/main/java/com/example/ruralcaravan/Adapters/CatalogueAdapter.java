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
import com.example.ruralcaravan.Activities.ItemDetailsActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemsResponse;
import com.example.ruralcaravan.Utilities.Constants;

import java.util.List;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.CatalogueViewHolder> {

    private Context context;
    private List<ItemsResponse> itemsResponses;

    public CatalogueAdapter(Context context, List<ItemsResponse> itemsResponses) {
        this.context = context;
        this.itemsResponses = itemsResponses;
    }

    @NonNull
    @Override
    public CatalogueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogue_row_layout, parent, false);
        CatalogueAdapter.CatalogueViewHolder catalogueViewHolder = new CatalogueViewHolder(view);
        return catalogueViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueViewHolder holder, int position) {
        final ItemsResponse item = itemsResponses.get(position);
        Glide.with(context)
                .load(context.getResources().getString(R.string.socket_address) + "/" + item.getImage() + "/")
                .placeholder(R.drawable.app_logo)
                .into(holder.imageViewItem);
        holder.textViewItemName.setText(item.getName());
        holder.textViewItemRate.setText(item.getRate());
        holder.cardViewCatalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra(Constants.KEY_PRODUCT_ID, item.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsResponses.size();
    }

    public class CatalogueViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewItem;
        private TextView textViewItemName;
        private TextView textViewItemRate;
        private CardView cardViewCatalogue;
        public CatalogueViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemRate = itemView.findViewById(R.id.textViewItemRate);
            cardViewCatalogue = itemView.findViewById(R.id.cardViewCatalogue);
        }
    }
}
