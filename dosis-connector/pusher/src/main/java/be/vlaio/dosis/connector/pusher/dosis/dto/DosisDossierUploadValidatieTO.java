package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;
import java.util.List;

@JsonDeserialize(builder=DosisDossierUploadValidatieTO.Builder.class)
public class DosisDossierUploadValidatieTO {

    private DosisIdentificatieTO identificatie;
    private String uploadId;
    private String bestand;
    private LocalDateTime tijdstipVerwerking;
    private boolean success;
    private String fout;
    private List<DosisVerwerkingFoutTO> fouten;
    private Boolean verwerktVoorOpvraging;

    public DosisDossierUploadValidatieTO(DosisIdentificatieTO identificatie,
                                         String uploadId,
                                         String bestand,
                                         LocalDateTime tijdstipVerwerking,
                                         boolean success,
                                         String fout,
                                         List<DosisVerwerkingFoutTO> fouten,
                                         Boolean verwerktVoorOpvraging) {
        this.identificatie = identificatie;
        this.uploadId = uploadId;
        this.bestand = bestand;
        this.tijdstipVerwerking = tijdstipVerwerking;
        this.success = success;
        this.fout = fout;
        this.fouten = fouten;
        this.verwerktVoorOpvraging = verwerktVoorOpvraging;
    }

    public DosisIdentificatieTO getIdentificatie() {
        return identificatie;
    }

    public String getUploadId() {
        return uploadId;
    }

    public String getBestand() {
        return bestand;
    }

    public LocalDateTime getTijdstipVerwerking() {
        return tijdstipVerwerking;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFout() {
        return fout;
    }

    public List<DosisVerwerkingFoutTO> getFouten() {
        return fouten;
    }

    public Boolean getVerwerktVoorOpvraging() {
        return verwerktVoorOpvraging;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private DosisIdentificatieTO identificatie;
        private String uploadId;
        private String bestand;
        private LocalDateTime tijdstipVerwerking;
        private boolean success;
        private String fout;
        private List<DosisVerwerkingFoutTO> fouten;
        private Boolean verwerktVoorOpvraging;

        public Builder() {
        }

        @JsonSetter("Identificatie")
        public Builder withIdentificatie(DosisIdentificatieTO identificatie) {
            this.identificatie = identificatie;
            return this;
        }

        @JsonSetter("UploadId")
        public Builder withUploadId(String uploadId) {
            this.uploadId = uploadId;
            return this;
        }

        @JsonSetter("Bestand")
        public Builder withBestand(String bestand) {
            this.bestand = bestand;
            return this;
        }

        @JsonSetter("TijdstipVerwerking")
        public Builder withTijdstipVerwerking(LocalDateTime tijdstipVerwerking) {
            this.tijdstipVerwerking = tijdstipVerwerking;
            return this;
        }

        @JsonSetter("Succes")
        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        @JsonSetter("Fout")
        public Builder withFout(String fout) {
            this.fout = fout;
            return this;
        }

        @JsonSetter("Fouten")
        public Builder withFouten(List<DosisVerwerkingFoutTO> fouten) {
            this.fouten = fouten;
            return this;
        }

        @JsonSetter("VerwerktVoorOpvraging")
        public Builder withVerwerktVoorOpvraging(Boolean verwerktVoorOpvraging) {
            this.verwerktVoorOpvraging = verwerktVoorOpvraging;
            return this;
        }

        public Builder but() {
            return new DosisDossierUploadValidatieTO.Builder()
                    .withIdentificatie(identificatie)
                    .withUploadId(uploadId)
                    .withBestand(bestand)
                    .withTijdstipVerwerking(tijdstipVerwerking)
                    .withSuccess(success)
                    .withFout(fout)
                    .withFouten(fouten)
                    .withVerwerktVoorOpvraging(verwerktVoorOpvraging);
        }

        public DosisDossierUploadValidatieTO build() {
            return new DosisDossierUploadValidatieTO(identificatie, uploadId, bestand,
                    tijdstipVerwerking, success, fout, fouten, verwerktVoorOpvraging);
        }
    }
}
