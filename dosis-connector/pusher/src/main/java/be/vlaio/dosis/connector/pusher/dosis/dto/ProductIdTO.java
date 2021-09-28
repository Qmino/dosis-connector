package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductIdTO {

    @JsonProperty("Id")
    private String id;

    public ProductIdTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
