package com.example.ruralcaravan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.DataClasses.HourlyWeather;
import com.example.ruralcaravan.R;

import java.util.ArrayList;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {

    private Context context;
    private ArrayList<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(Context context, ArrayList<HourlyWeather> hourlyWeatherArrayList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherArrayList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_weather_column_layout, parent, false);
        HourlyWeatherViewHolder hourlyWeatherViewHolder = new HourlyWeatherViewHolder(view);
        return hourlyWeatherViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.imageViewIcon.setImageResource(holder.imageViewIcon.getContext().getResources().getIdentifier("ic_" + hourlyWeather.getIconId(), "drawable", holder.imageViewIcon.getContext().getPackageName()));
        holder.textViewDescription.setText(hourlyWeather.getDescription());
        holder.textViewTemperature.setText(hourlyWeather.getTemperature() + " \u2103");
        holder.textViewHour.setText(hourlyWeather.getHour());
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewDescription;
        TextView textViewTemperature;
        TextView textViewHour;
        public HourlyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTemperature = itemView.findViewById(R.id.textViewTemperature);
            textViewHour = itemView.findViewById(R.id.textViewHour);
        }
    }
}
