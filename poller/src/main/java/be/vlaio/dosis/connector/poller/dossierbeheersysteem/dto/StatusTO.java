package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = StatusTO.Builder.class)
public class StatusTO {

    @JsonProperty("@type")
    private final String type;
    private final String vlaamseFase;
    private final String vlaamseCode;
    private final String detail;
    private final String actie;

    public StatusTO(String type, String vlaamseFase, String vlaamseCode, String detail, String actie) {
        this.type = type;
        this.vlaamseFase = vlaamseFase;
        this.vlaamseCode = vlaamseCode;
        this.detail = detail;
        this.actie = actie;
    }

    public String getType() {
        return type;
    }

    public String getVlaamseFase() {
        return vlaamseFase;
    }

    public String getVlaamseCode() {
        return vlaamseCode;
    }

    public String getDetail() {
        return detail;
    }

    public String getActie() {
        return actie;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String type;
        private String vlaamseFase;
        private String vlaamseCode;
        private String detail;
        private String actie;

        public Builder() {
        }

        @JsonSetter("@type")
        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withVlaamseFase(String vlaamseFase) {
            this.vlaamseFase = vlaamseFase;
            return this;
        }

        public Builder withVlaamseCode(String vlaamseCode) {
            this.vlaamseCode = vlaamseCode;
            return this;
        }

        public Builder withDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder withActie(String actie) {
            this.actie = actie;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withType(type)
                    .withVlaamseFase(vlaamseFase)
                    .withVlaamseCode(vlaamseCode)
                    .withDetail(detail)
                    .withActie(actie);
        }

        public StatusTO build() {
            return new StatusTO(type, vlaamseFase, vlaamseCode, detail, actie);
        }
    }
}
