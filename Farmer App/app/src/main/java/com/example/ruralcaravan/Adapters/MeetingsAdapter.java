package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.MeetingsResponse;
import com.example.ruralcaravan.Utilities.Constants;

import java.util.ArrayList;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetingsViewHolder> {

    private Context context;
    private ArrayList<MeetingsResponse> meetingsResponseArrayList;
    private OnMeetingAcceptedListener onMeetingAcceptedListener;

    public MeetingsAdapter(Context context, ArrayList<MeetingsResponse> meetingsResponseArrayList,
                           OnMeetingAcceptedListener onMeetingAcceptedListener) {
        this.context = context;
        this.meetingsResponseArrayList = meetingsResponseArrayList;
        this.onMeetingAcceptedListener = onMeetingAcceptedListener;
    }

    @NonNull
    @Override
    public MeetingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row_layout, parent, false);
        MeetingsAdapter.MeetingsViewHolder meetingsViewHolder = new MeetingsViewHolder(view, onMeetingAcceptedListener);
        return meetingsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingsViewHolder holder, int position) {
        MeetingsResponse meeting = meetingsResponseArrayList.get(position);
        Glide.with(context)
                .load(context.getString(R.string.socket_address) + meeting.getPhoto())
                .placeholder(R.drawable.app_logo)
//                .circleCrop()
                .into(holder.organizerLogo);
        holder.textViewDateTime.setText(meeting.getDate() + ", " + meeting.getTime());
        holder.textViewVenue.setText(meeting.getVenue());
        holder.textViewOrganizer.setText(meeting.getOrganiser());
        holder.textViewMeetingAgenda.setText(Html.fromHtml(meeting.getAgenda()));
        holder.textViewDescription.setText(Html.fromHtml(meeting.getDescription()));
        if(meeting.getMeetingtoken().equals(Constants.MEETING_ACCEPTED_CODE)) {
            holder.buttonAcceptMeeting.setText(context.getString(R.string.accepted));
            holder.buttonAcceptMeeting.setBackgroundColor(context.getResources().getColor(R.color.orange_buy_now));
        } else {
            holder.buttonAcceptMeeting.setText(context.getString(R.string.accept));
            holder.buttonAcceptMeeting.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return meetingsResponseArrayList.size();
    }

    public class MeetingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView organizerLogo;
        private TextView textViewDateTime;
        private TextView textViewVenue;
        private TextView textViewOrganizer;
        private TextView textViewMeetingAgenda;
        private TextView textViewDescription;
        private Button buttonAcceptMeeting;
        private OnMeetingAcceptedListener onMeetingAcceptedListener;

        public MeetingsViewHolder(@NonNull View itemView, OnMeetingAcceptedListener onMeetingAcceptedListener) {
            super(itemView);
            organizerLogo = itemView.findViewById(R.id.organizerLogo);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewVenue = itemView.findViewById(R.id.textViewVenue);
            textViewOrganizer = itemView.findViewById(R.id.textViewOrganizer);
            textViewMeetingAgenda = itemView.findViewById(R.id.textViewAgenda);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            buttonAcceptMeeting = itemView.findViewById(R.id.buttonAcceptMeeting);
            this.onMeetingAcceptedListener = onMeetingAcceptedListener;
            buttonAcceptMeeting.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMeetingAcceptedListener.onMeetingAccepted(getAdapterPosition(), buttonAcceptMeeting);
        }
    }

    public interface OnMeetingAcceptedListener {
        void onMeetingAccepted(int position, Button button);
    }
}
