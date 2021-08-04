package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.DosisItem;
import be.vlaio.dosis.connector.common.Verwerkingsstatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Een WorkItem is een interne datastructuur die door de WIP en de Diskstore gebruikt wordt om gegevens rond de
 * verwerking van een dosisitem bij te houden en op te slaan.
 */
@JsonDeserialize(builder = WorkItem.Builder.class)
class WorkItem {

    private final DosisItem dosisItem;
    private final Verwerkingsstatus currentStatus;
    private final List<WorkItemTransition> transitions;

    public WorkItem(DosisItem dosisItem, Verwerkingsstatus currentStatus, List<WorkItemTransition> transitions) {
        this.dosisItem = dosisItem;
        this.currentStatus = currentStatus;
        this.transitions = transitions;
    }

    public DosisItem getDosisItem() {
        return dosisItem;
    }

    public Verwerkingsstatus getCurrentStatus() {
        return currentStatus;
    }

    public List<WorkItemTransition> getTransitions() {
        return transitions;
    }

    @JsonIgnore
    public UUID getId() {
        return dosisItem.getId();
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private DosisItem dosisItem;
        private Verwerkingsstatus currentStatus;
        private List<WorkItemTransition> transitions;

        public Builder withDosisItem(DosisItem dosisItem) {
            this.dosisItem = dosisItem;
            return this;
        }

        public Builder withCurrentStatus(Verwerkingsstatus currentStatus) {
            this.currentStatus = currentStatus;
            return this;
        }

        public Builder withTransitions(List<WorkItemTransition> transitions) {
            this.transitions = transitions;
            return this;
        }

        public Builder withAdditionalTransition(WorkItemTransition transition) {
            this.transitions.add(transition);
            return this;
        }

        public static Builder from(WorkItem item) {
            return new Builder()
                    .withDosisItem(item.getDosisItem())
                    .withCurrentStatus(item.getCurrentStatus())
                    .withTransitions(item.getTransitions() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(item.getTransitions()));
        }

        public Builder but() {
            return new Builder().withDosisItem(dosisItem).withCurrentStatus(currentStatus).withTransitions(transitions);
        }

        public WorkItem build() {
            return new WorkItem(dosisItem, currentStatus, transitions);
        }
    }
}
