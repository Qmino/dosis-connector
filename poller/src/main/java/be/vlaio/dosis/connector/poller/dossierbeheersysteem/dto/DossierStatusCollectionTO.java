package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

@JsonDeserialize(builder = DossierStatusCollectionTO.Builder.class)
public class DossierStatusCollectionTO {

    @JsonProperty("@type")
    private final String type;
    @JsonProperty("@id")
    private final String id;
    private final long index;
    private final int limiet;
    private final long nieuweIndex;
    @JsonProperty("@volgendeVerzameling")
    private final String volgendeVerzameling;
    private final List<DossierStatusTO> elementen;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
    public long getIndex() {
        return index;
    }

    public int getLimiet() {
        return limiet;
    }
    public long getNieuweIndex() {
        return nieuweIndex;
    }

    public String getVolgendeVerzameling() {
        return volgendeVerzameling;
    }

    public List<DossierStatusTO> getElementen() {
        return elementen;
    }

    public DossierStatusCollectionTO(String type, String id, long index, int limiet,
                                     long nieuweIndex, String volgendeVerzameling,
                                     List<DossierStatusTO> elementen) {
        this.type = type;
        this.id = id;
        this.index = index;
        this.limiet = limiet;
        this.nieuweIndex = nieuweIndex;
        this.volgendeVerzameling = volgendeVerzameling;
        this.elementen = elementen;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String type;
        private String id;
        private long index;
        private int limiet;
        private long nieuweIndex;
        private String volgendeVerzameling;
        private List<DossierStatusTO> elementen;

        public Builder() {
        }

        @JsonSetter("@type")
        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        @JsonSetter("@id")
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withIndex(long index) {
            this.index = index;
            return this;
        }

        public Builder withLimiet(int limiet) {
            this.limiet = limiet;
            return this;
        }

        public Builder withNieuweIndex(long nieuweIndex) {
            this.nieuweIndex = nieuweIndex;
            return this;
        }

        @JsonSetter("@volgendeVerzameling")
        public Builder withVolgendeVerzameling(String volgendeVerzameling) {
            this.volgendeVerzameling = volgendeVerzameling;
            return this;
        }

        public Builder withElementen(List<DossierStatusTO> elementen) {
            this.elementen = elementen;
            return this;
        }

        public Builder but() {
            return new DossierStatusCollectionTO.Builder()
                    .withType(type)
                    .withId(id)
                    .withIndex(index)
                    .withLimiet(limiet)
                    .withNieuweIndex(nieuweIndex)
                    .withVolgendeVerzameling(volgendeVerzameling)
                    .withElementen(elementen);
        }

        public DossierStatusCollectionTO build() {
            return new DossierStatusCollectionTO(type, id, index, limiet, nieuweIndex,
                    volgendeVerzameling, elementen);
        }
    }
}
