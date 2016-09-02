package com.manywho.services.box.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manywho.services.box.entities.webhook.Source;

import java.util.HashMap;

public class WebhookReturn {


    @JsonProperty("webhook")
    private HashMap<String, Object> webhook;

    @JsonProperty("created_by")
    private HashMap<String, Object> createdBy;

    @JsonProperty("trigger")
    private String trigger;

    @JsonProperty("source")
    private Source source;

    public Source getSource() {return source;}

    public HashMap<String, Object> getWebhook() {
        return webhook;
    }

    public HashMap<String, Object> getCreatedBy() {
        return createdBy;
    }

    public String getTrigger() {
        return trigger;
    }
}
