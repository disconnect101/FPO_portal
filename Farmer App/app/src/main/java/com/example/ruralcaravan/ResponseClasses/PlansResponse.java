package com.example.ruralcaravan.ResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlansResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("max_cap")
    @Expose
    private Double maxCap;
    @SerializedName("current_amount")
    @Expose
    private Double currentAmount;
    @SerializedName("weigth_per_land")
    @Expose
    private Double weigthPerLand;
    @SerializedName("guidance")
    @Expose
    private String guidance;
    @SerializedName("live")
    @Expose
    private Boolean live;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("subscribers")
    @Expose
    private Integer subscribers;
    @SerializedName("facilities")
    @Expose
    private String facilities;
    @SerializedName("investment_requirements")
    @Expose
    private String investments;
    @SerializedName("estimatedProfit")
    @Expose
    private Double estimatedProfit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getMaxCap() {
        return maxCap;
    }

    public void setMaxCap(Double maxCap) {
        this.maxCap = maxCap;
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Double getWeigthPerLand() {
        return weigthPerLand;
    }

    public void setWeigthPerLand(Double weigthPerLand) {
        this.weigthPerLand = weigthPerLand;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Integer subscribers) {
        this.subscribers = subscribers;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getInvestments() {
        return investments;
    }

    public void setInvestments(String investments) {
        this.investments = investments;
    }

    public Double getEstimatedProfit() {
        return estimatedProfit;
    }

    public void setEstimatedProfit(Double estimatedProfit) {
        this.estimatedProfit = estimatedProfit;
    }

    @Override
    public String toString() {
        return "PlansResponse{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", maxCap=" + maxCap +
                ", currentAmount=" + currentAmount +
                ", weigthPerLand=" + weigthPerLand +
                ", guidance='" + guidance + '\'' +
                ", live=" + live +
                ", image='" + image + '\'' +
                ", subscribers=" + subscribers +
                ", facilities='" + facilities + '\'' +
                ", investments='" + investments + '\'' +
                ", estimatedProfit=" + estimatedProfit +
                '}';
    }
}
