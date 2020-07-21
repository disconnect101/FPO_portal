package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.DataClasses.DailyWeather;
import com.example.ruralcaravan.R;

import java.util.ArrayList;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder> {

    Context context;
    ArrayList<DailyWeather> dailyWeatherList;

    public DailyWeatherAdapter(Context context, ArrayList<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_weather_column_layout, parent, false);
        view.getLayoutParams().width = parent.getWidth() / 7;
        DailyWeatherViewHolder dailyWeatherViewHolder = new DailyWeatherViewHolder(view);
        return dailyWeatherViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {
        DailyWeather dailyWeather = dailyWeatherList.get(position);
        holder.textViewDay.setText(String.valueOf(dailyWeather.getDayOfWeek()));
        holder.textViewDate.setText(String.valueOf(dailyWeather.getDayOfMonth()));
        holder.imageViewIcon.setImageResource(holder.imageViewIcon.getContext().getResources().getIdentifier("ic_" + dailyWeather.getIconId(), "drawable", holder.imageViewIcon.getContext().getPackageName()));
        holder.textViewMaxTemperature.setText(dailyWeather.getMaxTemperature() + " \u2103");
        holder.textViewMinTemperature.setText(dailyWeather.getMinTemperature() + " \u2103");
        holder.textViewDescription.setText(dailyWeather.getDescription());

    }

    @Override
    public int getItemCount() {
        return dailyWeatherList.size();
    }

    public class DailyWeatherViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDay;
        TextView textViewDate;
        ImageView imageViewIcon;
        TextView textViewMaxTemperature;
        TextView textViewMinTemperature;
        TextView textViewDescription;

        public DailyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewMaxTemperature = itemView.findViewById(R.id.textViewMaxTemperature);
            textViewMinTemperature = itemView.findViewById(R.id.textViewMinTemperature);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
