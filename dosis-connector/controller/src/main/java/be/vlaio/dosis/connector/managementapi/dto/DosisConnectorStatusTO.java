package be.vlaio.dosis.connector.managementapi.dto;

import be.vlaio.dosis.connector.common.operational.DosisClientStatus;
import be.vlaio.dosis.connector.common.operational.DosisConnectorStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DosisConnectorStatusTO {

    private List<PollerStatusTO> pollers;
    private WipStatusTO workInProgress;
    private PusherValidatorTO pusher;
    private PusherValidatorTO validator;
    private DosisClientStatusTO dosisClient;

    public DosisConnectorStatusTO(List<PollerStatusTO> pollers,
                                  WipStatusTO workInProgress,
                                  PusherValidatorTO pusher,
                                  PusherValidatorTO validator,
                                  DosisClientStatusTO dosisClient) {
        this.pollers = pollers;
        this.workInProgress = workInProgress;
        this.pusher = pusher;
        this.validator = validator;
        this.dosisClient = dosisClient;
    }

    public List<PollerStatusTO> getPollers() {
        return pollers;
    }

    public WipStatusTO getWorkInProgress() {
        return workInProgress;
    }

    public PusherValidatorTO getPusher() {
        return pusher;
    }

    public PusherValidatorTO getValidator() {
        return validator;
    }

    public DosisClientStatusTO getDosisClient() {
        return dosisClient;
    }

    public static final class Builder {
        private List<PollerStatusTO> pollers;
        private WipStatusTO workInProgress;
        private PusherValidatorTO pusher;
        private PusherValidatorTO validator;
        private DosisClientStatusTO dosisClient;

        public Builder() {
        }

        public Builder withPollers(List<PollerStatusTO> pollers) {
            this.pollers = pollers;
            return this;
        }

        public Builder withWorkInProgress(WipStatusTO workInProgress) {
            this.workInProgress = workInProgress;
            return this;
        }

        public Builder withPusher(PusherValidatorTO pusher) {
            this.pusher = pusher;
            return this;
        }

        public Builder withValidator(PusherValidatorTO validator) {
            this.validator = validator;
            return this;
        }

        public Builder from(DosisConnectorStatus status) {
            this.pollers = status.getPollers() == null
                    ? new ArrayList<>()
                    : status.getPollers()
                    .stream()
                    .map(ps -> new PollerStatusTO.Builder().from(ps).build())
                    .collect(Collectors.toList());
            this.workInProgress =
                    new WipStatusTO.Builder().from(status.getWorkInProgress()).build();
            this.pusher =
                    new PusherValidatorTO.Builder().from(status.getPusher()).build();
            this.validator =
                    new PusherValidatorTO.Builder().from(status.getValidator()).build();
            this.dosisClient = new DosisClientStatusTO.Builder().from(status.getDosisClient()).build();
            return this;
        }


        public DosisConnectorStatusTO build() {
            return new DosisConnectorStatusTO(pollers, workInProgress, pusher, validator, dosisClient);
        }
    }
}
