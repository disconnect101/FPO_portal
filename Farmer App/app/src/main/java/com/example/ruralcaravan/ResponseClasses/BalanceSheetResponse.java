package com.example.ruralcaravan.ResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalanceSheetResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("refno")
    @Expose
    private String refno;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("currrent_amount")
    @Expose
    private Double currrentAmount;
    @SerializedName("description")
    @Expose
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getCurrrentAmount() {
        return currrentAmount;
    }

    public void setCurrrentAmount(Double currrentAmount) {
        this.currrentAmount = currrentAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
