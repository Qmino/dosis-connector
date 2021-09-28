package be.vlaio.dosis.connector.common.dosisdomain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

@JsonDeserialize(builder = OndernemerAgent.Builder.class)
public class OndernemerAgent extends Agent {
    private final String kboNummer;
    private final List<ToegangsRecht> toegangsRechten;

    public OndernemerAgent(String kboNummer, List<ToegangsRecht> toegangsRechten) {
        this.kboNummer = kboNummer;
        this.toegangsRechten = toegangsRechten;
    }

    public String getKboNummer() {
        return kboNummer;
    }

    public List<ToegangsRecht> getToegangsRechten() {
        return toegangsRechten;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String kboNummer;
        private List<ToegangsRecht> toegangsRechten;

        public Builder withKboNummer(String kboNummer) {
            this.kboNummer = kboNummer;
            return this;
        }

        public Builder withToegangsRechten(List<ToegangsRecht> toegangsRechten) {
            this.toegangsRechten = toegangsRechten;
            return this;
        }

        public OndernemerAgent build() {
            return new OndernemerAgent(kboNummer, toegangsRechten);
        }
    }
}
