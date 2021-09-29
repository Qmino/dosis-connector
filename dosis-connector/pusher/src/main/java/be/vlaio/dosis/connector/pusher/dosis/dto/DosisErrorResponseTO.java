package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

@JsonDeserialize(builder=DosisErrorResponseTO.Builder.class)
public class DosisErrorResponseTO {

    private String code;
    private String message;
    private String description;
    private List<DosisFieldErrorTO> errors;

    public DosisErrorResponseTO(String code, String message, String description, List<DosisFieldErrorTO> errors) {
        this.code = code;
        this.message = message;
        this.description = description;
        this.errors = errors;
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

    public List<DosisFieldErrorTO> getErrors() {
        return errors;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String code;
        private String message;
        private String description;
        private List<DosisFieldErrorTO> errors;

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

        public Builder withErrors(List<DosisFieldErrorTO> errors) {
            this.errors = errors;
            return this;
        }

        public DosisErrorResponseTO build() {
            return new DosisErrorResponseTO(code, message, description, errors);
        }
    }
}
