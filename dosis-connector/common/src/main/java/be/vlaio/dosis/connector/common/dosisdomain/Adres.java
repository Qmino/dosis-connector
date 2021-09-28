package be.vlaio.dosis.connector.common.dosisdomain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Adres.Builder.class)
public class Adres {
    private final String straat;
    private final String huisNummer;
    private final String postCode;
    private final String gemeente;

    private Adres(String straat, String huisNummer, String postCode, String gemeente) {
        this.straat = straat;
        this.huisNummer = huisNummer;
        this.postCode = postCode;
        this.gemeente = gemeente;
    }

    public String getStraat() {
        return straat;
    }

    public String getHuisNummer() {
        return huisNummer;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getGemeente() {
        return gemeente;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String straat;
        private String huisNummer;
        private String postCode;
        private String gemeente;

        public Builder withStraat(String straat) {
            this.straat = straat;
            return this;
        }

        public Builder withHuisNummer(String huisNummer) {
            this.huisNummer = huisNummer;
            return this;
        }

        public Builder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Builder withGemeente(String gemeente) {
            this.gemeente = gemeente;
            return this;
        }

        public Adres build() {
            return new Adres(straat, huisNummer, postCode, gemeente);
        }
    }
}
