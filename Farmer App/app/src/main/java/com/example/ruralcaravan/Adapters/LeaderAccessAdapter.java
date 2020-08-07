package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.LeaderAccessResponse;

import java.util.ArrayList;

public class LeaderAccessAdapter extends RecyclerView.Adapter<LeaderAccessAdapter.LeaderAccessViewHolder> {

    private Context context;
    private ArrayList<LeaderAccessResponse> leaderAccessResponseArrayList;
    private OnFarmerSelectedListener onFarmerSelectedListener;

    public LeaderAccessAdapter(Context context, ArrayList<LeaderAccessResponse> leaderAccessResponseArrayList,
                               OnFarmerSelectedListener onFarmerSelectedListener) {
        this.context = context;
        this.leaderAccessResponseArrayList = leaderAccessResponseArrayList;
        this.onFarmerSelectedListener = onFarmerSelectedListener;
    }

    @NonNull
    @Override
    public LeaderAccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_layout, parent, false);
        LeaderAccessAdapter.LeaderAccessViewHolder leaderAccessViewHolder = new LeaderAccessViewHolder(view,
                onFarmerSelectedListener);
        return leaderAccessViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderAccessViewHolder holder, int position) {
        final LeaderAccessResponse farmer = leaderAccessResponseArrayList.get(position);
        holder.textViewName.setText(farmer.getFarmerName());
    }

    @Override
    public int getItemCount() {
        return leaderAccessResponseArrayList.size();
    }

    public class LeaderAccessViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewName;
        private CardView cardView;
        private OnFarmerSelectedListener onFarmerSelectedListener;

        public LeaderAccessViewHolder(@NonNull View itemView, OnFarmerSelectedListener onFarmerSelectedListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            cardView = itemView.findViewById(R.id.cardView);
            this.onFarmerSelectedListener = onFarmerSelectedListener;
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFarmerSelectedListener.onFarmerSelected(getAdapterPosition());
        }
    }

    public interface OnFarmerSelectedListener {
        void onFarmerSelected(int position);
    }

}
