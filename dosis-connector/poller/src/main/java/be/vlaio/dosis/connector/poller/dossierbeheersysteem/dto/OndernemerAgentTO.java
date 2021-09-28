package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

@JsonDeserialize(builder = OndernemerAgentTO.Builder.class)
public class OndernemerAgentTO extends AgentTO {
    private final String kboNummer;
    private final List<ToegangsRechtTO> toegangsrechten;

    public OndernemerAgentTO(String kboNummer, List<ToegangsRechtTO> toegangsrechten) {
        this.kboNummer = kboNummer;
        this.toegangsrechten = toegangsrechten;
    }

    public String getKboNummer() {
        return kboNummer;
    }

    public List<ToegangsRechtTO> getToegangsrechten() {
        return toegangsrechten;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String kboNummer;
        private List<ToegangsRechtTO> toegangsrechten;

        public Builder withKboNummer(String kboNummer) {
            this.kboNummer = kboNummer;
            return this;
        }

        public Builder withToegangsrechten(List<ToegangsRechtTO> toegangsrechten) {
            this.toegangsrechten = toegangsrechten;
            return this;
        }

        public OndernemerAgentTO build() {
            return new OndernemerAgentTO(kboNummer, toegangsrechten);
        }
    }
}
