package be.vlaio.dosis.connector.pusher.dosis.dto;

import be.vlaio.dosis.connector.common.DossierStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = DosisDossierStatusTo.Builder.class)
public class DosisDossierStatusTo {

    @JsonProperty("VlaamsCode")
    private String vlaamseCode;
    @JsonProperty("VlaamsFase")
    private String vlaamseFase;
    @JsonProperty("Detail1")
    private String detail;
    @JsonProperty("Actie")
    private DosisDossierStatusActieTO actie;

    public DosisDossierStatusTo(String vlaamseCode, String vlaamseFase, String detail, DosisDossierStatusActieTO actie) {
        this.vlaamseCode = vlaamseCode;
        this.vlaamseFase = vlaamseFase;
        this.detail = detail;
        this.actie = actie;
    }

@JsonPOJOBuilder
    public static final class Builder {
        private String vlaamseCode;
        private String vlaamseFase;
        private String detail;
        private DosisDossierStatusActieTO actie;

        public Builder() {
        }

        public Builder withVlaamseCode(String vlaamseCode) {
            this.vlaamseCode = vlaamseCode;
            return this;
        }

        public Builder withVlaamseFase(String vlaamseFase) {
            this.vlaamseFase = vlaamseFase;
            return this;
        }

        public Builder withDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder withActie(DosisDossierStatusActieTO actie) {
            this.actie = actie;
            return this;
        }

        public Builder from(DossierStatus status) {
            this.actie = status.getActie() == null ? null : new DosisDossierStatusActieTO(true, status.getActie());
            this.detail = status.getDetail();
            this.vlaamseCode = status.getVlaamseCode();
            this.vlaamseFase = status.getVlaamseFase();
            return this;
        }

        public Builder but() {
            return new Builder().withVlaamseCode(vlaamseCode).withVlaamseFase(vlaamseFase).withDetail(detail).withActie(actie);
        }

        public DosisDossierStatusTo build() {
            return new DosisDossierStatusTo(vlaamseCode, vlaamseFase, detail, actie);
        }
    }
}
