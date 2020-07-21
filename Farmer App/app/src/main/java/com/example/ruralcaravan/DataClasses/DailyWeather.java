package com.example.ruralcaravan.DataClasses;

public class DailyWeather {

    String dayOfWeek;
    int dayOfMonth;
    String iconId;
    double maxTemperature;
    double minTemperature;
    String description;

    public DailyWeather(String dayOfWeek, int dayOfMonth, String iconId, double maxTemperature, double minTemperature, String description) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.iconId = iconId;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.description = description;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DailyWeather{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", dayOfMonth=" + dayOfMonth +
                ", iconId='" + iconId + '\'' +
                ", maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", description='" + description + '\'' +
                '}';
    }
}
