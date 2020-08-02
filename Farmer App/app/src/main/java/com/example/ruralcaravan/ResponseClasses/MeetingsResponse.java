package com.example.ruralcaravan.ResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeetingsResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("organiser")
    @Expose
    private String organiser;
    @SerializedName("agenda")
    @Expose
    private String agenda;
    @SerializedName("venue")
    @Expose
    private String venue;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("meetingtoken")
    @Expose
    private String meetingtoken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMeetingtoken() {
        return meetingtoken;
    }

    public void setMeetingtoken(String meetingtoken) {
        this.meetingtoken = meetingtoken;
    }

}
