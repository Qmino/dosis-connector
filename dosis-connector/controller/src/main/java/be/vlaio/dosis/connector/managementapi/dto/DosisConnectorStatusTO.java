package be.vlaio.dosis.connector.managementapi.dto;

import be.vlaio.dosis.connector.common.operational.DosisConnectorStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DosisConnectorStatusTO {

    private List<PollerStatusTO> pollers;
    private WipStatusTO workInProgress;

    public DosisConnectorStatusTO(List<PollerStatusTO> pollers, WipStatusTO workInProgress) {
        this.pollers = pollers;
        this.workInProgress = workInProgress;
    }

    public List<PollerStatusTO> getPollers() {
        return pollers;
    }

    public WipStatusTO getWorkInProgress() {
        return workInProgress;
    }

    public static final class Builder {
        private List<PollerStatusTO> pollers;
        private WipStatusTO workInProgress;

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

        public Builder from(DosisConnectorStatus status) {
            this.pollers = status.getPollers() == null
                    ? new ArrayList<>()
                    : status.getPollers()
                    .stream()
                    .map(ps -> new PollerStatusTO.Builder().from(ps).build())
                    .collect(Collectors.toList());
            this.workInProgress =
                    new WipStatusTO.Builder().from(status.getWorkInProgress()).build();
            return this;
        }

        public DosisConnectorStatusTO build() {
            return new DosisConnectorStatusTO(pollers, workInProgress);
        }
    }
}
