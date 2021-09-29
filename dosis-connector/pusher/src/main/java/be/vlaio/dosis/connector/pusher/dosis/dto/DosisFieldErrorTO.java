package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder= DosisFieldErrorTO.Builder.class)
public class DosisFieldErrorTO {

    private String code;
    private String message;
    private String description;
    private String field;

    public DosisFieldErrorTO(String code, String message, String description, String field) {
        this.code = code;
        this.message = message;
        this.description = description;
        this.field = field;
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

    public String getField() {
        return field;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String code;
        private String message;
        private String description;
        private String field;

        public Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withField(String field) {
            this.field = field;
            return this;
        }

        public DosisFieldErrorTO build() {
            return new DosisFieldErrorTO(code, message, description, field);
        }
    }
}
