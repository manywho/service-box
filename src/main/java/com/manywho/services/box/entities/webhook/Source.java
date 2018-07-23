package com.manywho.services.box.entities.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("item")
    private Item item;

    public Item getItem() {
        return item;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
