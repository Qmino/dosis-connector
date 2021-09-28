package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.dosisdomain.DosisItem;
import be.vlaio.dosis.connector.common.operational.Verwerkingsstatus;
import be.vlaio.dosis.connector.common.operational.WipStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorkInProgress {

    private final Logger logger = LoggerFactory.getLogger(WorkInProgress.class);
    private final int lowWater;
    private final int highWater;
    private final DiskStore store;

    private final Map<Verwerkingsstatus, List<WorkItem>> items = new HashMap<>();
    private final Set<UUID> currentIds = new HashSet<>();
    private final Set<UUID> finishedIds = new HashSet<>();
    private boolean canAcceptMoreWork = true;

    /**
     * Constructor.
     *
     * @param lowWater  Zolang het aantal dosisitems in deze component lager is dan dit aantal, worden nieuwe
     *                  workitems aanvaard.
     * @param highWater Vanaf het aantal dosisitems in deze component hoger is dan dit aantal, worden nieuwe
     *                  workitems niet meer aanvaard
     * @param store     de diskstore die gebruikt wordt om dosisitems van en naar de harde schijf op te slaan.
     */
    public WorkInProgress(@Value("${dosisgateway.wip.lowwater}") int lowWater,
                          @Value("${dosisgateway.wip.highwater}") int highWater,
                          DiskStore store) {
        logger.info("Initializing WIP Component [low: " + lowWater + ", high: " + highWater + "]");
        this.store = store;
        this.lowWater = lowWater;
        this.highWater = highWater;
        initFromDisk();
    }

    /**
     * Past de verwerkingsstatus van een gekend item aan. Indien de doel-status "COMPLETED" is, wordt het item verwijderd
     * uit de WIP (het blijft echter wel bewaard op schijf).
     *
     * @param dosisItem      Het item dat moet worden aangepast. Dit item moet gekend zijn door de WIP (maw, het moet zijn
     *                       toegevoegd, en mag niet de status COMPLETED hebben).
     * @param status         de gewenste nieuwe status van het item
     * @throws IllegalArgumentException indien een transitie gevraagd wordt voor een element dat niet gekend is
     */
    public void transitionItem(DosisItem dosisItem, Verwerkingsstatus status) {
        transitionItem(dosisItem, status, new HashMap<>());
    }

    /**
     * Past de verwerkingsstatus van een gekend item aan. Indien de doel-status "COMPLETED" is, wordt het item verwijderd
     * uit de WIP (het blijft echter wel bewaard op schijf).
     *
     * @param dosisItem      Het item dat moet worden aangepast. Dit item moet gekend zijn door de WIP (maw, het moet zijn
     *                       toegevoegd, en mag niet de status COMPLETED hebben).
     * @param status         de gewenste nieuwe status van het item
     * @param additionalInfo Indien gewenst kan bijkomende informatie rond de transitie worden meegegeven als een
     *                       key-value pair. Dit wordt dan mee bewaard op schijf (dit is vooral interessant voor bv errors).
     * @throws IllegalArgumentException indien een transitie gevraagd wordt voor een element dat niet gekend is
     */
    public void transitionItem(DosisItem dosisItem, Verwerkingsstatus status, Map<String, String> additionalInfo) {
        WorkItem item = getItem(dosisItem.getId()).orElseThrow(() -> {
            throw new IllegalArgumentException("WIP can not transition an element that it does not contain. Add it first.");
        });
        WorkItem newItem = WorkItem.Builder.from(item).withCurrentStatus(status).withAdditionalTransition(
                new WorkItemTransition(item.getCurrentStatus(), status, LocalDateTime.now(), additionalInfo)
        ).build();
        items.get(item.getCurrentStatus()).remove(item);
        store.upsert(newItem);
        if (newItem.getCurrentStatus() == Verwerkingsstatus.COMPLETED) {
            registerWorkAsDone(newItem.getDosisItem().getId());
        } else {
            items.get(newItem.getCurrentStatus()).add(newItem);
        }
    }

    /**
     * Voegt een nieuw item werk toe aan de WIP component.
     * @param newItem het toe te voegen element
     * @param pollerName de naam van de poller die het element toevoegt
     * @param upstreamIndex de upstream (dossierbeheersysteem) index van het toe te voegen element
     * @throws IllegalArgumentException indien de WIP component reeds een item werk kent met dezelfde id.
     */
    public void addNewDosisItem(DosisItem newItem, String pollerName, long upstreamIndex) throws IllegalArgumentException {
        if (! canAcceptMoreWork) {
            logger.debug("Still adding work although WIP component indicates it filling up.");
        }
        if (currentIds.contains(newItem.getId())) {
            throw new IllegalArgumentException("WIP already contains element with this id.");
        } else {
            WorkItem item = new WorkItem.Builder().withDosisItem(newItem).withCurrentStatus(Verwerkingsstatus.TODO).build();
            items.get(Verwerkingsstatus.TODO).add(item);
            currentIds.add(newItem.getId());
            store.upsert(item);
            if (canAcceptMoreWork && getNumberOfItems() >= highWater) {
                canAcceptMoreWork = false;
            }
            store.saveLastProcessedIndex(pollerName, upstreamIndex, item);
        }
    }

    /**
     * Geeft de index van het laatste element dat door de poller met de gegeven naam is geregistreerd bij de WIP.
     * Indien er geen element geregisteerd is wordt -1 teruggegeven.
     * @param pollerName de naam van de poller
     * @return de index van het laatst verwerkte element afkomstig van de poller met de gegeven naam
     */
    public long getLastIndexProcessed(String pollerName) {
        return store.getLastProcessedIndex(pollerName);
    }

    /**
     * Geeft het oudste element terug in een bepaalde toestand. Elementen in de COMPLETED toestand worden
     * nooit teruggegeven.
     * @param verwerkingsstatus de toestand waarin het element gevraagd wordt
     * @return het oudste element in de gegeven toestand als het bestaat.
     */
    public Optional<DosisItem> getItemInState(Verwerkingsstatus verwerkingsstatus) {
        if (verwerkingsstatus == null || verwerkingsstatus == Verwerkingsstatus.COMPLETED) {
            return Optional.empty();
        } else {
            return items.get(verwerkingsstatus).stream().findFirst().map(WorkItem::getDosisItem);
        }
    }

    /**
     * @return ja indien er bijkomend werk aan de WIP component mag worden gegeven, false indien niet. dit moet
     * beschouwd worden als een indicatie, niet als een harde limiet. Het is mogelijk extra elementen toe te voegen aan
     * de component, ook indien deze methode false teruggeeft.
     */
    public boolean readyToAcceptNewWork() {
        return canAcceptMoreWork;
    }


    /**
     * Registreert een werkitem als afgerond. De id wordt verwijderd uit de id lijst, en de keuze om al dan niet
     * nieuw werk te accepteren wordt geherevalueerd.
     * @param id de id van het net afgewerkte workitem.
     */
    private void registerWorkAsDone(UUID id) {
        currentIds.remove(id);
        finishedIds.add(id);
        if (currentIds.size() <= lowWater && !canAcceptMoreWork) {
            canAcceptMoreWork = true;
        }
    }

    /**
     * @return aantal elementen beheerd door de wip.
     */
    private int getNumberOfItems() {
        return currentIds.size();
    }

    private Optional<WorkItem> getItem(UUID id) {
        // Find in memory
        for (List<WorkItem> itemsOfStatus : items.values()) {
            Optional<WorkItem> subResult = itemsOfStatus.stream().filter(wi -> wi.getDosisItem().getId() == id).findAny();
            if (subResult.isPresent()) {
                return subResult;
            }
        }
        // It is possible it is no longer in memory.
        return store.fetchById(id);
    }

    /**
     * Refresht de volledige in memory toestand van de WIP op basis van de diskstore.
     */
    private void initFromDisk() {
        items.clear();
        List<WorkItem> allElements = store.fetchAllUncompleted();
        items.putAll(allElements.stream().collect(Collectors.groupingBy(WorkItem::getCurrentStatus)));
        currentIds.clear();
        currentIds.addAll(allElements.stream().map(wi -> wi.getDosisItem().getId()).collect(Collectors.toList()));
        for (Verwerkingsstatus status: Verwerkingsstatus.values()) {
            items.computeIfAbsent(status, k -> new ArrayList<>());
        }
        canAcceptMoreWork = currentIds.size() < highWater;
    }

    /**
     * @return het aantal afgewerkte elementen sinds deze component is opgestart
     */
    public int getNbItemsCompleted() {
        return finishedIds.size();
    }

    /**
     * @return the status of the work in progress
     */
    public WipStatus getStatus() {
        Map<Verwerkingsstatus, Integer> work = new HashMap<>();
        items.forEach((status, items) -> work.put(status, items.size()));
        return new WipStatus.Builder()
                .withHighWaterMark(this.highWater)
                .withLowWaterMark(this.lowWater)
                .withAcceptingWork(this.canAcceptMoreWork)
                .withItems(work).build();
    }
}
