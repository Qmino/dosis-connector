package be.vlaio.dosis.connector.poller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DossierStatusCollectionTO {

    @JsonProperty("@type")
    private String type;
    @JsonProperty("@id")
    private String id;
    private int index;
    private int limiet;
    private int nieuweIndex;
    @JsonProperty("@volgendeVerzameling")
    private String volgendeVerzameling;
    private List<DossierStatusTO> elementen;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLimiet() {
        return limiet;
    }

    public void setLimiet(int limiet) {
        this.limiet = limiet;
    }

    public int getNieuweIndex() {
        return nieuweIndex;
    }

    public void setNieuweIndex(int nieuweIndex) {
        this.nieuweIndex = nieuweIndex;
    }

    public String getVolgendeVerzameling() {
        return volgendeVerzameling;
    }

    public void setVolgendeVerzameling(String volgendeVerzameling) {
        this.volgendeVerzameling = volgendeVerzameling;
    }

    public List<DossierStatusTO> getElementen() {
        return elementen;
    }

    public void setElementen(List<DossierStatusTO> elementen) {
        this.elementen = elementen;
    }
}
