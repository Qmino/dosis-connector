package be.vlaio.dosis.connector.poller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class DossierStatusTO {

    @JsonProperty("@type")
    private String type;

    private String dossierNummer;
    private String dossierNaam;
    private String doorverwijzingUrl;
    private LocalDateTime wijzigingsDatum;
    private StatusTO status;
    @JsonProperty("dossier beheerder")
    private ContactTO dossierBeheerder;
    private List<AgentTO> agenten;
    private int product;
}
