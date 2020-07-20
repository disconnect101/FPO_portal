package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.BuildConfig;
import com.example.ruralcaravan.DataClasses.CurrentWeather;
import com.example.ruralcaravan.DataClasses.DailyWeather;
import com.example.ruralcaravan.DataClasses.HourlyWeather;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.SingletonClasses.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class WeatherFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=minutely&units=metric&appid=" + BuildConfig.openWeatherMapAPIKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                extractWeatherData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        return rootView;
    }

    private void extractWeatherData(JSONObject response) {
        CurrentWeather currentWeather = getCurrentWeatherData(response);
        ArrayList<DailyWeather> dailyWeather = getDailyWeatherData(response);
        ArrayList<HourlyWeather> hourlyWeather = getHourlyWeatherData(response);
    }

    private CurrentWeather getCurrentWeatherData(JSONObject response) {
        try {
            return new CurrentWeather(response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon"),
                    response.getJSONObject("current").getDouble("feels_like"),
                    response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description"),
                    response.getJSONObject("current").getInt("humidity"),
                    response.getJSONObject("current").getInt("clouds"),
                    response.getJSONObject("current").getDouble("wind_speed")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<DailyWeather> getDailyWeatherData(JSONObject response) {
        ArrayList<DailyWeather> dailyWeatherArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        try {
            JSONArray jsonArrayOfDailyWeatherStats = response.getJSONArray("daily");
            for(int i = 0; i < 7; i++) {
                JSONObject dailyWeatherStats = jsonArrayOfDailyWeatherStats.getJSONObject(i);
                long unixTimeStamp = dailyWeatherStats.getLong("dt");
                calendar.setTimeInMillis(unixTimeStamp*1000);
                dailyWeatherArrayList.add(new DailyWeather(calendar.get(Calendar.DAY_OF_WEEK),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                dailyWeatherStats.getJSONArray("weather").getJSONObject(0).getString("icon"),
                                dailyWeatherStats.getJSONObject("temp").getDouble("max"),
                                dailyWeatherStats.getJSONObject("temp").getDouble("min"),
                                dailyWeatherStats.getJSONArray("weather").getJSONObject(0).getString("main")
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dailyWeatherArrayList;
    }

    private ArrayList<HourlyWeather> getHourlyWeatherData(JSONObject response) {
        ArrayList<HourlyWeather> hourlyWeatherArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        try {
            JSONArray jsonArrayOfHourlyWeatherStats = response.getJSONArray("hourly");
            for(int i=0;i<24;i++) {
                JSONObject hourlyWeatherStats = jsonArrayOfHourlyWeatherStats.getJSONObject(i);
                long unixTimeStamp = hourlyWeatherStats.getLong("dt");
                calendar.setTimeInMillis(unixTimeStamp*1000);
                hourlyWeatherArrayList.add(new HourlyWeather(hourlyWeatherStats.getJSONArray("weather").getJSONObject(0).getString("icon"),
                            hourlyWeatherStats.getDouble("feels_like"),
                            get12HourFormat(calendar.get(Calendar.HOUR_OF_DAY))
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hourlyWeatherArrayList;
    }

    private String get12HourFormat(int hour) {
        String _12HourFomat = null;
        if(hour == 0) {
            _12HourFomat = "12 AM";
        } else if(hour <=11) {
            _12HourFomat = hour + " AM";
        } else if (hour == 12){
            _12HourFomat = "12 PM";
        } else {
            _12HourFomat = (hour%12) + " PM";
        }
        return _12HourFomat;
    }

}
