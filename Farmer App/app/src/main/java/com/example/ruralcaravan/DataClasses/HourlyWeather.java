package com.example.ruralcaravan.DataClasses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HourlyWeather {

    String iconId;
    double temperature;
    String hour;

    public HourlyWeather(String iconId, double temperature, String hour) {
        this.iconId = iconId;
        this.temperature = temperature;
        this.hour = hour;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "HourlyWeather{" +
                "iconId='" + iconId + '\'' +
                ", temperature=" + temperature +
                ", hour=" + getHour() +
                '}';
    }
}
