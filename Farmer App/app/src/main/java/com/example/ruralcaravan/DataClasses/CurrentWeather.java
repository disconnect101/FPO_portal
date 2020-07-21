package com.example.ruralcaravan.DataClasses;

public class CurrentWeather {

    String iconId;
    double temperature;
    String description;
    int humidityPercentage;
    int cloudiness;
    double windSpeed;
    long unixTimeStamp;

    public CurrentWeather(String iconId, double temperature, String description, int humidityPercentage, int cloudiness, double windSpeed, long dateTime) {
        this.iconId = iconId;
        this.temperature = temperature;
        this.description = description;
        this.humidityPercentage = humidityPercentage;
        this.cloudiness = cloudiness;
        this.windSpeed = windSpeed;
        this.unixTimeStamp = dateTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHumidityPercentage() {
        return humidityPercentage;
    }

    public void setHumidityPercentage(int humidityPercentage) {
        this.humidityPercentage = humidityPercentage;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(int cloudiness) {
        this.cloudiness = cloudiness;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(long unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "iconId='" + iconId + '\'' +
                ", temperature=" + temperature +
                ", description='" + description + '\'' +
                ", humidityPercentage=" + humidityPercentage +
                ", cloudiness=" + cloudiness +
                ", windSpeed=" + windSpeed +
                ", unixTimeStamp=" + unixTimeStamp +
                '}';
    }

}
