package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisDossierStatusActieTO {

    @JsonProperty("ActieNodig")
    private boolean actieNodig;
    @JsonProperty("Actie")
    private String actie;

    public DosisDossierStatusActieTO(boolean actieNodig, String actie) {
        this.actieNodig = actieNodig;
        this.actie = actie;
    }

    public boolean isActieNodig() {
        return actieNodig;
    }

    public String getActie() {
        return actie;
    }
}
