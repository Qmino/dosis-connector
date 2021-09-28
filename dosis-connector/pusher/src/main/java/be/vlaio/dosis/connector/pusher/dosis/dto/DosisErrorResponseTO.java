package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder=DosisErrorResponseTO.Builder.class)
public class DosisErrorResponseTO {

    private String code;
    private String message;
    private String description;

    public DosisErrorResponseTO(String code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String code;
        private String message;
        private String description;

        public Builder() {
        }

        @JsonProperty("Code")
        public Builder withCode(String code) {
            this.code = code;
            return this;
        }
        @JsonProperty("Message")
        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        @JsonProperty("Description")
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DosisErrorResponseTO build() {
            return new DosisErrorResponseTO(code, message, description);
        }
    }
}
