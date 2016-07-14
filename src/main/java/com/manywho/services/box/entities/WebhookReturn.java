package com.manywho.services.box.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class WebhookReturn {

    @JsonProperty("source")
    private HashMap<String, Object> source;

    @JsonProperty("webhook")
    private HashMap<String, Object> webhook;

    @JsonProperty("trigger")
    private String trigger;

    public HashMap<String, Object> getSource() {
        return source;
    }

    public HashMap<String, Object> getWebhook() {
        return webhook;
    }

    public String getTrigger() {
        return trigger;
    }
}
