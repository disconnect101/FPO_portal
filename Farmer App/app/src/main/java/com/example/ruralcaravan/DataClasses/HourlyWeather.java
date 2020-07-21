package com.example.ruralcaravan.DataClasses;

public class HourlyWeather {

    String iconId;
    double temperature;
    String hour;
    String description;

    public HourlyWeather(String iconId, double temperature, String hour, String description) {
        this.iconId = iconId;
        this.temperature = temperature;
        this.hour = hour;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "HourlyWeather{" +
                "iconId='" + iconId + '\'' +
                ", temperature=" + temperature +
                ", hour='" + hour + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
