package com.example.ruralcaravan.ResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeaderAccessResponse {

    @SerializedName("farmer_name")
    @Expose
    private String farmerName;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("userid")
    @Expose
    private String userid;

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
