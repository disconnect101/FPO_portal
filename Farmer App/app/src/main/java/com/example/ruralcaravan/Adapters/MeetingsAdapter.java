package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;

import info.hoang8f.widget.FButton;

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
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date, time;
        SimpleDateFormat sdfTime = new SimpleDateFormat("H:mm:ss");
        try {
            date = sdfDate.parse(meeting.getDate());
            String dayOfMonth =  new SimpleDateFormat("dd").format(date);
            String monthAndYear = new SimpleDateFormat("MMM yyyy").format(date);
            String dayOfWeek = new SimpleDateFormat("E").format(date);
            time = sdfTime.parse(meeting.getTime());
            String formattedTime = new SimpleDateFormat("hh:mm a").format(time);
            holder.textViewDayOfWeek.setText(dayOfWeek);
            holder.textViewDayOfMonth.setText(dayOfMonth);
            holder.textViewMonthAndYear.setText(monthAndYear);
            holder.textViewTime.setText(formattedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.textViewVenue.setText(meeting.getVenue());
        holder.textViewOrganizer.setText(meeting.getOrganiser());
        holder.textViewMeetingAgenda.setText(Html.fromHtml(meeting.getAgenda()));
        holder.textViewDescription.setText(Html.fromHtml(meeting.getDescription()));
        if(meeting.getMeetingtoken().equals(Constants.MEETING_ACCEPTED_CODE)) {
            holder.buttonAcceptMeeting.setText(context.getString(R.string.accepted));
            holder.buttonAcceptMeeting.setButtonColor(context.getResources().getColor(R.color.orange_buy_now));
        } else {
            holder.buttonAcceptMeeting.setText(context.getString(R.string.accept));
            holder.buttonAcceptMeeting.setButtonColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return meetingsResponseArrayList.size();
    }

    public class MeetingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textViewVenue;
        private TextView textViewOrganizer;
        private TextView textViewMeetingAgenda;
        private TextView textViewDescription;
        private TextView textViewDayOfWeek;
        private TextView textViewDayOfMonth;
        private TextView textViewMonthAndYear;
        private TextView textViewTime;
        private FButton buttonAcceptMeeting;
        private OnMeetingAcceptedListener onMeetingAcceptedListener;

        public MeetingsViewHolder(@NonNull View itemView, OnMeetingAcceptedListener onMeetingAcceptedListener) {
            super(itemView);
            textViewVenue = itemView.findViewById(R.id.textViewVenue);
            textViewOrganizer = itemView.findViewById(R.id.textViewOrganizer);
            textViewMeetingAgenda = itemView.findViewById(R.id.textViewAgenda);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDayOfWeek = itemView.findViewById(R.id.textViewDayOfWeek);
            textViewDayOfMonth = itemView.findViewById(R.id.textViewDayOfMonth);
            textViewMonthAndYear = itemView.findViewById(R.id.textViewMonthAndYear);
            textViewTime = itemView.findViewById(R.id.textViewTime);
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
        void onMeetingAccepted(int position, FButton button);
    }
}
