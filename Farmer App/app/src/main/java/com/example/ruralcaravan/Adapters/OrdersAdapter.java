package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.Activities.ItemDetailsActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.OrdersResponse;
import com.example.ruralcaravan.Utilities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

    private Context context;
    private ArrayList<OrdersResponse> ordersResponseArrayList;

    public OrdersAdapter(Context context, ArrayList<OrdersResponse> ordersResponseArrayList) {
        this.context = context;
        this.ordersResponseArrayList = ordersResponseArrayList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_layout, parent, false);
        OrdersAdapter.OrdersViewHolder ordersViewHolder = new OrdersViewHolder(view);
        return ordersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        final OrdersResponse order = ordersResponseArrayList.get(position);
        holder.textViewOrderName.setText(order.getName());
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdfDate.parse(order.getDate());
            String dateTime = new SimpleDateFormat("dd MMM yyyy").format(date);
            holder.textViewOrderDate.setText(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.textViewOrderMode.setText(getModeOfPayment(order.getType()));
        Glide.with(context)
                .load(context.getString(R.string.socket_address) + order.getImage())
                .placeholder(R.drawable.app_logo)
                .into(holder.imageViewOrder);
        if(order.getDelivered()) {
            holder.linearLayoutPaymentStatus.setVisibility(View.GONE);
        } else {
            holder.linearLayoutPaymentStatus.setVisibility(View.VISIBLE);
            holder.textViewIsPaid.setText(order.getPaid() ? context.getString(R.string.paid) : context.getString(R.string.not_paid));
        }
        holder.textViewAmount.setText("\u20B9" + order.getPrice());
        holder.textViewRate.setText("\u20B9" + order.getRate());
        holder.textViewQuantity.setText(order.getQuantity().toString());
        holder.cardViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.KEY_PRODUCT_ID, order.getItemId().toString());
                intent.putExtra(Constants.KEY_PRODUCT_NAME, order.getName());
                context.startActivity(intent);
            }
        });
    }

    private String getModeOfPayment(String type) {
        switch (type) {
            case "PEW":
                return context.getString(R.string.pay_with_account);
            case "CAS":
                return context.getString(R.string.pay_at_FPO_office);
            default:
                return context.getString(R.string.cash_on_delivery);
        }
    }

    @Override
    public int getItemCount() {
        return ordersResponseArrayList.size();
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOrderName;
        private TextView textViewOrderDate;
        private TextView textViewOrderMode;
        private ImageView imageViewOrder;
        private TextView textViewIsPaid;
        private CardView cardViewOrder;
        private TextView textViewAmount;
        private TextView textViewRate;
        private TextView textViewQuantity;
        private LinearLayout linearLayoutPaymentStatus;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderName = itemView.findViewById(R.id.textViewOrderName);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderMode = itemView.findViewById(R.id.textViewOrderMode);
            imageViewOrder = itemView.findViewById(R.id.imageViewOrder);
            cardViewOrder = itemView.findViewById(R.id.cardViewOrder);
            textViewIsPaid = itemView.findViewById(R.id.textViewIsPaid);
            textViewAmount = itemView.findViewById(R.id.textViewTotal);
            textViewRate = itemView.findViewById(R.id.textViewRate);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            linearLayoutPaymentStatus = itemView.findViewById(R.id.linearLayoutPaymentStatus);
        }
    }
}
