package be.vlaio.dosis.connector.pusher.dosis;

import be.vlaio.dosis.connector.common.operational.ServiceError;

public class DosisClientException extends Exception {

    private ServiceError serviceError;

    public DosisClientException() {
    }

    public DosisClientException(String message) {
        super(message);
    }

    public DosisClientException(String message, ServiceError serviceError) {
        super(message);
        this.serviceError = serviceError;
    }

    public DosisClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public DosisClientException(String message, Throwable cause, ServiceError error) {
        super(message, cause);
        this.serviceError = error;
    }

    public ServiceError getServiceError() {
        return serviceError;
    }
}
