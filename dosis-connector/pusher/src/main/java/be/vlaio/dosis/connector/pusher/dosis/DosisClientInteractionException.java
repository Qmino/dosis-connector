package be.vlaio.dosis.connector.pusher.dosis;

import be.vlaio.dosis.connector.common.ServiceError;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("rawtypes")
public class DosisClientInteractionException extends DosisClientException {

    private ResponseEntity faultyEntity;

    public DosisClientInteractionException(ResponseEntity faultyEntity) {
        this.faultyEntity = faultyEntity;
    }

    public DosisClientInteractionException(String message, Throwable cause, ResponseEntity faultyEntity) {
        super(message, cause);
        this.faultyEntity = faultyEntity;
    }

    public DosisClientInteractionException(String message, Throwable cause, ServiceError error, ResponseEntity faultyEntity) {
        super(message, cause, error);
        this.faultyEntity = faultyEntity;
    }

    public ResponseEntity getFaultyEntity() {
        return faultyEntity;
    }
}
