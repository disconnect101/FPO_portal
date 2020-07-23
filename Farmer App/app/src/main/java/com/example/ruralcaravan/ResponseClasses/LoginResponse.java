package com.example.ruralcaravan.ResponseClasses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "statuscode",
        "token",
        "category"
})
public class LoginResponse {

    @JsonProperty("statuscode")
    private String statuscode;
    @JsonProperty("token")
    private String token;
    @JsonProperty("category")
    private String category;

    @JsonProperty("statuscode")
    public String getStatuscode() {
        return statuscode;
    }

    @JsonProperty("statuscode")
    public void setStatuscode(String statuscode) {
        this.statuscode = statuscode;
    }

    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    @JsonProperty("token")
    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

}
