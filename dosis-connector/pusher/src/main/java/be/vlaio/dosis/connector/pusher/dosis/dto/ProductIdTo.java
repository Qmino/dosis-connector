package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductIdTo {

    @JsonProperty("Id")
    private String id;

    public ProductIdTo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
