package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisToegangsRechtTO {

    @JsonProperty("Code")
    private String code;

    public DosisToegangsRechtTO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
