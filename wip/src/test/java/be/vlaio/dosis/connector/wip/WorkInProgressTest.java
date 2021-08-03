package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.CommonTestMother;
import be.vlaio.dosis.connector.common.DosisItem;
import be.vlaio.dosis.connector.common.Verwerkingsstatus;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WorkInProgressTest {

    @TempDir
    File rootFolder;
    DiskStore store;
    WorkInProgress wip;
    String pollerNaam;

    @BeforeEach
    public void setup() {
        pollerNaam = "pn";
        store = new DiskStore(rootFolder.getAbsolutePath(),
                new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule()));
        wip = new WorkInProgress(2, 5, store);
    }

    @Test
    @DisplayName("Low water & High Water")
    public void testLowAndHighWater() {
        DosisItem first = CommonTestMother.someDosisItem().build();
        DosisItem second = CommonTestMother.someDosisItem().build();
        DosisItem third = CommonTestMother.someDosisItem().build();
        DosisItem fourth = CommonTestMother.someDosisItem().build();
        DosisItem fifth = CommonTestMother.someDosisItem().build();
        DosisItem sixth = CommonTestMother.someDosisItem().build();

        Assertions.assertTrue(wip.readyToAcceptNewWork());
        Assertions.assertEquals(-1L, wip.getLastIndexProcessed(pollerNaam));
        wip.addNewDosisItem(first, pollerNaam, 1L);  //1
        Assertions.assertEquals(1L, wip.getLastIndexProcessed(pollerNaam));
        Assertions.assertEquals(-1L, wip.getLastIndexProcessed("andere"));
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(second, pollerNaam, 2L); //2
        Assertions.assertEquals(2L, wip.getLastIndexProcessed(pollerNaam));
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(third, pollerNaam, 3L);  //3
        Assertions.assertEquals(3L, wip.getLastIndexProcessed(pollerNaam));
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(fourth, pollerNaam, 4L); //4
        Assertions.assertEquals(4L, wip.getLastIndexProcessed(pollerNaam));
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(fifth, pollerNaam, 5L);  //5
        Assertions.assertEquals(5L, wip.getLastIndexProcessed(pollerNaam));
        Assertions.assertFalse(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(sixth, pollerNaam, 6L);  //6  We kunnen nog steeds toevoegen.
        Assertions.assertEquals(6L, wip.getLastIndexProcessed(pollerNaam));
        Assertions.assertFalse(wip.readyToAcceptNewWork());
        wip.transitionItem(first, Verwerkingsstatus.COMPLETED);
        Assertions.assertFalse(wip.readyToAcceptNewWork()); //5
        wip.transitionItem(second, Verwerkingsstatus.COMPLETED);
        Assertions.assertFalse(wip.readyToAcceptNewWork()); //4
        wip.transitionItem(third, Verwerkingsstatus.COMPLETED);
        Assertions.assertFalse(wip.readyToAcceptNewWork()); //3
        wip.transitionItem(fourth, Verwerkingsstatus.COMPLETED);
        Assertions.assertTrue(wip.readyToAcceptNewWork()); //2
    }

    @Test
    @DisplayName("Addition, transition and retrieval")
    public void testAdditionTransitionRetrieval() {
        DosisItem first = CommonTestMother.someDosisItem().build();
        DosisItem second = CommonTestMother.someDosisItem().build();
        DosisItem third = CommonTestMother.someDosisItem().build();
        DosisItem sameAsFirst = CommonTestMother.someDosisItem().but().withId(first.getId()).build();

        wip.addNewDosisItem(first, pollerNaam, 0L);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wip.addNewDosisItem(sameAsFirst, pollerNaam, 0L);
        });
        Optional<DosisItem> retrieved = wip.getItemInState(Verwerkingsstatus.TODO);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(first, retrieved.get());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.FAILED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.UNVALIDATED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.COMPLETED).isPresent());
        Assertions.assertEquals(0, wip.getNbItemsCompleted());
        Assertions.assertTrue(store.fetchById(first.getId()).isPresent());

        wip.addNewDosisItem(second, pollerNaam, 1L);
        retrieved = wip.getItemInState(Verwerkingsstatus.TODO);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(first, retrieved.get());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.FAILED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.UNVALIDATED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.COMPLETED).isPresent());
        Assertions.assertEquals(0, wip.getNbItemsCompleted());

        wip.transitionItem(first, Verwerkingsstatus.FAILED);
        retrieved = wip.getItemInState(Verwerkingsstatus.TODO);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(second, retrieved.get());
        Assertions.assertTrue(wip.getItemInState(Verwerkingsstatus.FAILED).isPresent());
        Assertions.assertEquals(first, wip.getItemInState(Verwerkingsstatus.FAILED).get());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.UNVALIDATED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.COMPLETED).isPresent());
        Assertions.assertEquals(0, wip.getNbItemsCompleted());

        wip.transitionItem(second, Verwerkingsstatus.COMPLETED);
        retrieved = wip.getItemInState(Verwerkingsstatus.TODO);
        Assertions.assertFalse(retrieved.isPresent());
        Assertions.assertTrue(wip.getItemInState(Verwerkingsstatus.FAILED).isPresent());
        Assertions.assertEquals(first, wip.getItemInState(Verwerkingsstatus.FAILED).get());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.UNVALIDATED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.COMPLETED).isPresent());

        Assertions.assertEquals(1, wip.getNbItemsCompleted());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wip.transitionItem(third, Verwerkingsstatus.COMPLETED);
        });
    }
}
