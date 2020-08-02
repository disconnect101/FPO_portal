package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.BalanceSheetResponse;

import java.util.ArrayList;

public class BalanceSheetAdapter extends RecyclerView.Adapter<BalanceSheetAdapter.BalanceSheetViewHolder> {

    private Context context;
    private ArrayList<BalanceSheetResponse> balanceSheet;

    public BalanceSheetAdapter(Context context, ArrayList<BalanceSheetResponse> balanceSheet) {
        this.context = context;
        this.balanceSheet = balanceSheet;
    }

    @NonNull
    @Override
    public BalanceSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_sheet_row_layout, parent, false);
        BalanceSheetAdapter.BalanceSheetViewHolder balanceSheetViewHolder = new BalanceSheetViewHolder(view);
        return balanceSheetViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceSheetViewHolder holder, int position) {
        BalanceSheetResponse transaction = balanceSheet.get(position);
//        if(transaction.getAmount() > 0) {
//            holder.imageViewTransaction.setImageDrawable(R.drawable.);
//        } else {
//            holder.imageViewTransaction.setImageDrawable(R.drawable.);
//        }
        holder.imageViewTransaction.setImageResource(R.drawable.app_logo);
        holder.textViewTransactionDescription.setText(transaction.getDescription());
        holder.textViewReferenceNumber.setText(transaction.getRefno());
        holder.textViewTime.setText(transaction.getDate());
        holder.textViewAmount.setText(transaction.getAmount().toString());
    }

    @Override
    public int getItemCount() {
        return balanceSheet.size();
    }

    public class BalanceSheetViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewTransaction;
        private TextView textViewTransactionDescription;
        private TextView textViewReferenceNumber;
        private TextView textViewTime;
        private TextView textViewAmount;
        public BalanceSheetViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTransaction = itemView.findViewById(R.id.imageViewTransaction);
            textViewTransactionDescription = itemView.findViewById(R.id.textViewTransactionDescription);
            textViewReferenceNumber = itemView.findViewById(R.id.textViewReferenceNumber);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }
}
