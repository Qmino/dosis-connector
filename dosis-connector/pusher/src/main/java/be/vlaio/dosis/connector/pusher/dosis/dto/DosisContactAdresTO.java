package be.vlaio.dosis.connector.pusher.dosis.dto;

import be.vlaio.dosis.connector.common.dosisdomain.Adres;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = DosisContactAdresTO.Builder.class)
public class DosisContactAdresTO {

    @JsonProperty("Straat")
    private String straat;
    @JsonProperty("Nummer")
    private String huisnummer;
    @JsonProperty("Postcode")
    private String postcode;
    @JsonProperty("Gemeente")
    private String gemeente;

    public DosisContactAdresTO(String straat, String huisnummer, String postcode, String gemeente) {
        this.straat = straat;
        this.huisnummer = huisnummer;
        this.postcode = postcode;
        this.gemeente = gemeente;
    }

    public String getStraat() {
        return straat;
    }

    public String getHuisnummer() {
        return huisnummer;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getGemeente() {
        return gemeente;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String straat;
        private String huisnummer;
        private String postcode;
        private String gemeente;

        public Builder() {
        }

        public Builder from(Adres a) {
            this.straat = a.getStraat();
            this.huisnummer = a.getHuisNummer();
            this.gemeente = a.getGemeente();
            this.postcode = a.getPostCode();
            return this;
        }

        public Builder withStraat(String straat) {
            this.straat = straat;
            return this;
        }

        public Builder withHuisnummer(String huisnummer) {
            this.huisnummer = huisnummer;
            return this;
        }

        public Builder withPostcode(String postcode) {
            this.postcode = postcode;
            return this;
        }

        public Builder withGemeente(String gemeente) {
            this.gemeente = gemeente;
            return this;
        }

        public DosisContactAdresTO build() {
            return new DosisContactAdresTO(straat, huisnummer, postcode, gemeente);
        }
    }
}
