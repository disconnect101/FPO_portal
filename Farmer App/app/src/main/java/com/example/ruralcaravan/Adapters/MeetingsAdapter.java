package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.MeetingsResponse;

import java.util.ArrayList;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetingsViewHolder> {

    private Context context;
    private ArrayList<MeetingsResponse> meetingsResponseArrayList;

    public MeetingsAdapter(Context context, ArrayList<MeetingsResponse> meetingsResponseArrayList) {
        this.context = context;
        this.meetingsResponseArrayList = meetingsResponseArrayList;
    }

    @NonNull
    @Override
    public MeetingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row_layout, parent, false);
        MeetingsAdapter.MeetingsViewHolder meetingsViewHolder = new MeetingsViewHolder(view);
        return meetingsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingsViewHolder holder, int position) {
        MeetingsResponse meeting = meetingsResponseArrayList.get(position);
        Glide.with(context)
                .load("http://192.168.43.132:8000" + meeting.getPhoto())
                .placeholder(R.drawable.app_logo)
//                .circleCrop()
                .into(holder.organizerLogo);
        holder.textViewDateTime.setText(meeting.getDate() + ", " + meeting.getTime());
        holder.textViewVenue.setText(meeting.getVenue());
        holder.textViewOrganizer.setText(meeting.getOrganiser());
        holder.textViewMeetingAgenda.setText(Html.fromHtml(meeting.getAgenda()));
        holder.textViewDescription.setText(Html.fromHtml(meeting.getDescription()));
    }

    @Override
    public int getItemCount() {
        return meetingsResponseArrayList.size();
    }

    public class MeetingsViewHolder extends RecyclerView.ViewHolder {
        ImageView organizerLogo;
        TextView textViewDateTime;
        TextView textViewVenue;
        TextView textViewOrganizer;
        TextView textViewMeetingAgenda;
        TextView textViewDescription;
        public MeetingsViewHolder(@NonNull View itemView) {
            super(itemView);
            organizerLogo = itemView.findViewById(R.id.organizerLogo);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewVenue = itemView.findViewById(R.id.textViewVenue);
            textViewOrganizer = itemView.findViewById(R.id.textViewOrganizer);
            textViewMeetingAgenda = itemView.findViewById(R.id.textViewAgenda);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
