package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import org.springframework.http.ResponseEntity;

public class FetchException extends Exception {

    private ResponseEntity<DossierStatusCollectionTO> faultyEntity;

    public FetchException(ResponseEntity<DossierStatusCollectionTO> entity) {
        this.faultyEntity = entity;
    }

    public FetchException(String message, Throwable cause, ResponseEntity<DossierStatusCollectionTO> faultyEntity) {
        super(message, cause);
        this.faultyEntity = faultyEntity;
    }

    public ResponseEntity<DossierStatusCollectionTO> getFaultyEntity() {
        return faultyEntity;
    }
}
