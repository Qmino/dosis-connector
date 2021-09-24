package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.common.ServiceError;
import org.springframework.http.ResponseEntity;

public class FetchException extends Exception {

    private ResponseEntity<DossierStatusCollectionTO> faultyEntity;
    private ServiceError errorType;

    public FetchException(ResponseEntity<DossierStatusCollectionTO> entity) {
        this.faultyEntity = entity;
    }

    public FetchException(String message,
                          Throwable cause,
                          ServiceError errorType,
                          ResponseEntity<DossierStatusCollectionTO> faultyEntity) {
        super(message, cause);
        this.errorType = errorType;
        this.faultyEntity = faultyEntity;
    }

    public ResponseEntity<DossierStatusCollectionTO> getFaultyEntity() {
        return faultyEntity;
    }

    public ServiceError getErrorType() {
        return errorType;
    }
}
