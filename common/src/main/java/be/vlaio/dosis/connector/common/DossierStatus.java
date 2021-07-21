package be.vlaio.dosis.connector.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Een dossierstatus zoals gedefinieerd door Dosis.
 */
@JsonDeserialize(builder = DossierStatus.Builder.class)
public class DossierStatus {

    private final String vlaamseCode;
    private final String vlaamseFase;
    private final String detail;
    private final String actie;

    private DossierStatus(String vlaamseCode, String vlaamseFase, String detail, String actie) {
        this.vlaamseCode = vlaamseCode;
        this.vlaamseFase = vlaamseFase;
        this.detail = detail;
        this.actie = actie;
    }

    public String getVlaamseCode() {
        return vlaamseCode;
    }

    public String getVlaamseFase() {
        return vlaamseFase;
    }

    public String getDetail() {
        return detail;
    }

    public String getActie() {
        return actie;
    }

    public static final class Builder {
        private String vlaamseCode;
        private String vlaamseFase;
        private String detail;
        private String actie;

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

        public Builder withActie(String actie) {
            this.actie = actie;
            return this;
        }

        public DossierStatus build() {
            return new DossierStatus(vlaamseCode, vlaamseFase, detail, actie);
        }
    }
}
