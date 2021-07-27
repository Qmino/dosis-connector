package be.vlaio.dosis.connector.managementapi.dto;

import be.vlaio.dosis.connector.common.Verwerkingsstatus;

import java.util.Map;

public class WipStatusTO {

    private Map<Verwerkingsstatus, Integer> items;

    public WipStatusTO(Map<Verwerkingsstatus, Integer> items) {
        this.items = items;
    }

    public Map<Verwerkingsstatus, Integer> getItems() {
        return items;
    }
}
