package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.BalanceSheetResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        if(transaction.getAmount() >= 0) {
            holder.imageViewTransaction.setImageResource(R.drawable.ic_money_credit);
            holder.textViewAmount.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.textViewAmount.setText("+" + transaction.getAmount());
        } else {
            holder.imageViewTransaction.setImageResource(R.drawable.ic_money_debit);
            holder.textViewAmount.setText(transaction.getAmount().toString());
        }
        holder.textViewTransactionDescription.setText(transaction.getDescription());
        holder.textViewReferenceNumber.setText(transaction.getRefno());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(transaction.getDate());
            String dateYear =  new SimpleDateFormat("dd MMM yyyy").format(date);
            String time = new SimpleDateFormat("hh:mm a").format(date);
            holder.textViewTime.setText(dateYear + ", " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
