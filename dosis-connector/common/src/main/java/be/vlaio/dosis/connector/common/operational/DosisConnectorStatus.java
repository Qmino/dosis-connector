package be.vlaio.dosis.connector.common.operational;

import java.util.List;

public class DosisConnectorStatus {

    private List<PollerStatus> pollers;
    private WipStatus workInProgress;
    private PusherValidatorStatus pusher;
    private PusherValidatorStatus validator;
    private DosisClientStatus dosisClient;

    public List<PollerStatus> getPollers() {
        return pollers;
    }

    public WipStatus getWorkInProgress() {
        return workInProgress;
    }

    public PusherValidatorStatus getPusher() {
        return pusher;
    }

    public PusherValidatorStatus getValidator() {
        return validator;
    }

    public DosisClientStatus getDosisClient() {
        return dosisClient;
    }

    public DosisConnectorStatus(List<PollerStatus> pollers,
                                WipStatus workInProgress,
                                PusherValidatorStatus pusher,
                                PusherValidatorStatus validator,
                                DosisClientStatus dosisClient) {
        this.pollers = pollers;
        this.workInProgress = workInProgress;
        this.pusher = pusher;
        this.validator = validator;
        this.dosisClient = dosisClient;
    }


    public static final class Builder {
        private List<PollerStatus> pollers;
        private WipStatus workInProgress;
        private PusherValidatorStatus pusher;
        private PusherValidatorStatus validator;
        private DosisClientStatus dosisClient;

        public Builder() {
        }

        public Builder withPollers(List<PollerStatus> pollers) {
            this.pollers = pollers;
            return this;
        }

        public Builder withWorkInProgress(WipStatus workInProgress) {
            this.workInProgress = workInProgress;
            return this;
        }

        public Builder withPusher(PusherValidatorStatus pusher) {
            this.pusher = pusher;
            return this;
        }

        public Builder withValidator(PusherValidatorStatus validator) {
            this.validator = validator;
            return this;
        }

        public Builder withDosisClient(DosisClientStatus dosisClient) {
            this.dosisClient = dosisClient;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withPollers(pollers)
                    .withWorkInProgress(workInProgress)
                    .withPusher(pusher)
                    .withValidator(validator)
                    .withDosisClient(dosisClient);
        }

        public DosisConnectorStatus build() {
            return new DosisConnectorStatus(pollers, workInProgress, pusher, validator, dosisClient);
        }


    }
}
