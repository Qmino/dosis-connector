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
import java.util.Optional;

public class WorkInProgressTest {

    @TempDir
    File rootFolder;
    DiskStore store;
    WorkInProgress wip;

    @BeforeEach
    public void setup() {
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
        wip.addNewDosisItem(first);  //1
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(second); //2
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(third);  //3
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(fourth); //4
        Assertions.assertTrue(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(fifth);  //5
        Assertions.assertFalse(wip.readyToAcceptNewWork());
        wip.addNewDosisItem(sixth);  //6  We kunnen nog steeds toevoegen.
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

        wip.addNewDosisItem(first);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wip.addNewDosisItem(sameAsFirst);
        });
        Optional<DosisItem> retrieved = wip.getItemInState(Verwerkingsstatus.TODO);
        Assertions.assertTrue(retrieved.isPresent());
        Assertions.assertEquals(first, retrieved.get());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.FAILED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.UNVALIDATED).isPresent());
        Assertions.assertFalse(wip.getItemInState(Verwerkingsstatus.COMPLETED).isPresent());
        Assertions.assertEquals(0, wip.getNbItemsCompleted());

        wip.addNewDosisItem(second);
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
