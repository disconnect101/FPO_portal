package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ProduceResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProduceAdapter extends RecyclerView.Adapter <ProduceAdapter.ProduceViewHolder> {

    private Context context;
    private ArrayList<ProduceResponse> produceResponseArrayList;

    public ProduceAdapter(Context context, ArrayList<ProduceResponse> produceResponseArrayList) {
        this.context = context;
        this.produceResponseArrayList = produceResponseArrayList;
    }

    @NonNull
    @Override
    public ProduceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produce_row_layout, parent, false);
        ProduceAdapter.ProduceViewHolder produceViewHolder = new ProduceViewHolder(view);
        return produceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProduceViewHolder holder, int position) {
        ProduceResponse produce = produceResponseArrayList.get(position);
        holder.textViewName.setText(produce.getCropName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(produce.getDate());
            String dateYear =  new SimpleDateFormat("dd MMM yyyy").format(date);
            String time = new SimpleDateFormat("hh:mm a").format(date);
            holder.textViewDate.setText(dateYear + ", " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.textViewAmount.setText(produce.getAmount().toString());
        holder.textViewAmountSold.setText(produce.getAmountsold().toString());
        holder.textViewIncome.setText(produce.getIncome().toString());
        holder.textViewQuality.setText(produce.getQuality() ? context.getString(R.string.accepted) : context.getString(R.string.rejected));
    }

    @Override
    public int getItemCount() {
        return produceResponseArrayList.size();
    }

    public class ProduceViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewDate;
        private TextView textViewAmount;
        private TextView textViewAmountSold;
        private TextView textViewIncome;
        private TextView textViewQuality;

        public ProduceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewAmountSold = itemView.findViewById(R.id.textViewAmountSold);
            textViewIncome = itemView.findViewById(R.id.textViewIncome);
            textViewQuality = itemView.findViewById(R.id.textViewQuality);
        }
    }
}
