package com.example.ruralcaravan.ResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProduceResponse {

    @SerializedName("crop__name")
    @Expose
    private String cropName;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("amountsold")
    @Expose
    private Double amountsold;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("quality")
    @Expose
    private Boolean quality;
    @SerializedName("income")
    @Expose
    private Double income;

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmountsold() {
        return amountsold;
    }

    public void setAmountsold(Double amountsold) {
        this.amountsold = amountsold;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getQuality() {
        return quality;
    }

    public void setQuality(Boolean quality) {
        this.quality = quality;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

}
