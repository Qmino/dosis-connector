package be.vlaio.dosis.connector.common;

import java.util.HashMap;
import java.util.Map;

public class WipStatus {

    private Map<Verwerkingsstatus, Integer> items;

    public WipStatus(Map<Verwerkingsstatus, Integer> items) {
        this.items = new HashMap<>();
        this.items.putAll(items);
    }

    public Map<Verwerkingsstatus, Integer> getItems() {
        return items;
    }
}
