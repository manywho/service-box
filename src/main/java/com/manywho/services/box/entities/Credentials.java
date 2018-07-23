package com.manywho.services.box.entities;

public class Credentials {
    private String accessToken;
    private String refreshToken;
    private String boxUserId;
    private Boolean flowsListenning;

    public Credentials(){}

    public Credentials(String accessToken, String refreshToken, String boxUserId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.boxUserId = boxUserId;
        this.flowsListenning = false;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getBoxUserId() {
        return boxUserId;
    }

    public void setBoxUserId(String boxUserId) {
        this.boxUserId = boxUserId;
    }

    public Boolean getFlowsListenning() {
        return flowsListenning;
    }

    public void setFlowsListenning(Boolean flowsListenning) {
        this.flowsListenning = flowsListenning;
    }
}
