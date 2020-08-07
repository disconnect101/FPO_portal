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

import com.example.ruralcaravan.Activities.ListItemsActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.Constants;

import java.util.ArrayList;

public class CatalogueCategoryAdapter extends RecyclerView.Adapter<CatalogueCategoryAdapter.CatalogueCategoryViewHolder> {

    private Context context;
    private ArrayList<String> categories;
    private ArrayList<Integer> categoryIcons;

    public CatalogueCategoryAdapter(Context context, ArrayList<String> categories, ArrayList<Integer> categoryIcons) {
        this.context = context;
        this.categories = categories;
        this.categoryIcons = categoryIcons;
    }

    @NonNull
    @Override
    public CatalogueCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_image_row_layout, parent, false);
        CatalogueCategoryAdapter.CatalogueCategoryViewHolder catalogueCategoryViewHolder = new CatalogueCategoryViewHolder(view);
        return catalogueCategoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueCategoryViewHolder holder, final int position) {
        holder.imageViewCategoryImage.setImageResource(categoryIcons.get(position));
        holder.textViewCategory.setText(categories.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListItemsActivity.class);
                intent.putExtra(Constants.KEY_CATEGORY, position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CatalogueCategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewCategoryImage;
        private TextView textViewCategory;
        private CardView cardView;
        public CatalogueCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategoryImage = itemView.findViewById(R.id.imageViewImage);
            textViewCategory = itemView.findViewById(R.id.textViewName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
