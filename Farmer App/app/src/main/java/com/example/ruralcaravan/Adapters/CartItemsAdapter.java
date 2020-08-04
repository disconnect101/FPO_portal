package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.CartItemsResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemsViewHolder> {

    private Context context;
    private ArrayList<CartItemsResponse> cartItemsArrayList;
    private OnItemDeleteListener onItemDeleteListener;
    private OnQuantityDecreaseListener onQuantityDecreaseListener;
    private OnQuantityIncreaseListener onQuantityIncreaseListener;

    public CartItemsAdapter(Context context, ArrayList<CartItemsResponse> cartItemsArrayList,
                            OnItemDeleteListener onItemDeleteListener,
                            OnQuantityDecreaseListener onQuantityDecreaseListener,
                            OnQuantityIncreaseListener onQuantityIncreaseListener) {

        this.context = context;
        this.cartItemsArrayList = cartItemsArrayList;
        this.onItemDeleteListener = onItemDeleteListener;
        this.onQuantityDecreaseListener = onQuantityDecreaseListener;
        this.onQuantityIncreaseListener = onQuantityIncreaseListener;
    }

    @NonNull
    @Override
    public CartItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row_layout, parent, false);
        CartItemsAdapter.CartItemsViewHolder ordersViewHolder = new CartItemsViewHolder(view,
                onItemDeleteListener,
                onQuantityDecreaseListener,
                onQuantityIncreaseListener);
        return ordersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartItemsViewHolder holder, int position) {
        CartItemsResponse cartItem = cartItemsArrayList.get(position);
        holder.textViewItemName.setText(cartItem.getName());
        holder.textViewItemRate.setText(cartItem.getRate());
        holder.textViewQuantity.setText(cartItem.getQuantity().toString());
        Glide.with(context)
                .load(context.getString(R.string.socket_address) + cartItem.getImage())
                .placeholder(R.drawable.app_logo)
                .into(holder.imageViewItem);
        if(cartItem.getQuantity() == 1) {
            holder.buttonDecreaseQuantity.setImageDrawable(context.getDrawable(R.drawable.ic_delete));
        }
        holder.buttonDeleteItem.setButtonColor(context.getResources().getColor(R.color.light_grey));
    }

    @Override
    public int getItemCount() {
        return cartItemsArrayList.size();
    }

    public class CartItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textViewItemName;
        private TextView textViewItemRate;
        private FloatingActionButton buttonDecreaseQuantity;
        private FloatingActionButton buttonIncreaseQuantity;
        private TextView textViewQuantity;
        private FButton buttonDeleteItem;
        private ImageView imageViewItem;
        private OnItemDeleteListener onItemDeleteListener;
        private OnQuantityDecreaseListener onQuantityDecreaseListener;
        private OnQuantityIncreaseListener onQuantityIncreaseListener;

        public CartItemsViewHolder(@NonNull View itemView,
                                   OnItemDeleteListener onItemDeleteListener,
                                   OnQuantityDecreaseListener onQuantityDecreaseListener,
                                   OnQuantityIncreaseListener onQuantityIncreaseListener) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemRate = itemView.findViewById(R.id.textViewItemRate);
            buttonDecreaseQuantity = itemView.findViewById(R.id.buttonDecreaseQuantity);
            buttonIncreaseQuantity = itemView.findViewById(R.id.buttonIncreaseQuantity);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonDeleteItem = itemView.findViewById(R.id.buttonDeleteItem);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            this.onItemDeleteListener = onItemDeleteListener;
            this.onQuantityDecreaseListener = onQuantityDecreaseListener;
            this.onQuantityIncreaseListener = onQuantityIncreaseListener;
            buttonDecreaseQuantity.setOnClickListener(this);
            buttonIncreaseQuantity.setOnClickListener(this);
            buttonDeleteItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDeleteItem:
                    onItemDeleteListener.onItemDelete(cartItemsArrayList.get(getAdapterPosition()).getId().toString(), getAdapterPosition(), Integer.parseInt(textViewQuantity.getText().toString()));
                    break;
                case R.id.buttonDecreaseQuantity:
                    onQuantityDecreaseListener.onQuantityDecreased(cartItemsArrayList.get(getAdapterPosition()).getId().toString(), textViewQuantity, getAdapterPosition(), buttonDecreaseQuantity);
                    break;
                case R.id.buttonIncreaseQuantity:
                    onQuantityIncreaseListener.onQuantityIncreased(cartItemsArrayList.get(getAdapterPosition()).getId().toString(), textViewQuantity, getAdapterPosition(), buttonDecreaseQuantity);
                    break;
            }
        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(String cartId, int position, int quantity);
    }

    public interface OnQuantityDecreaseListener {
        void onQuantityDecreased(String cartId, TextView view, int position, FloatingActionButton button);
    }

    public interface OnQuantityIncreaseListener {
        void onQuantityIncreased(String cartId, TextView view, int position, FloatingActionButton button);
    }
}
