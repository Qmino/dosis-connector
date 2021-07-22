package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.DosisItem;
import be.vlaio.dosis.connector.common.CommonTestMother;
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
import java.io.IOException;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class DiskStoreTest {

    @TempDir
    File rootFolder;
    DiskStore store;

    @BeforeEach
    public void setup() {
        store = new DiskStore(rootFolder.getAbsolutePath(),
                new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule()));

    }

    /**
     * Test de verschillende diskstore operaties en kijkt of de juiste bestanden zijn aangemaakt.
     */
    @DisplayName("Basis functionaliteit")
    @Test
    void basicStoreTest() {
        Assertions.assertNotNull(store.fetchAll());
        Assertions.assertEquals(0, store.fetchAll().size());

        DosisItem.Builder baseDosisItem = CommonTestMother.someDosisItem();
        WorkItem.Builder base = WipTestMother.someWorkItem().but()
                .withCurrentStatus(Verwerkingsstatus.TODO)
                .withDosisItem(baseDosisItem.build());

        WorkItem toDo = base.build();
        store.upsert(toDo);
        Assertions.assertNotNull(store.fetchAll());
        Assertions.assertEquals(1, store.fetchAll().size());
        Assertions.assertEquals(toDo.getDosisItem().getId(), store.fetchAll().get(0).getDosisItem().getId());
        File[] toDoFiles = store.getStorageFolder(Verwerkingsstatus.TODO).listFiles();
        Assertions.assertEquals(1, toDoFiles.length);
        Assertions.assertEquals(toDo.getId() + ".json", toDoFiles[0].getName());
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.COMPLETED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.UNVALIDATED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.FAILED).listFiles().length);

        Assertions.assertTrue(store.fetchById(toDo.getId()).isPresent());
        Assertions.assertFalse(store.fetchById(UUID.randomUUID()).isPresent());
        WorkItem second = base.but()
                .withDosisItem(baseDosisItem.but()
                        .withId(UUID.randomUUID())
                        .build())
                .build();

        store.upsert(second);
        Assertions.assertEquals(2, store.fetchAll().size());
        Assertions.assertEquals(2, store.fetchAllUncompleted().size());
        Assertions.assertEquals(2, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.COMPLETED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.UNVALIDATED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.FAILED).listFiles().length);

        WorkItem changed = base.but()
                .withCurrentStatus(Verwerkingsstatus.UNVALIDATED)
                .build();
        store.upsert(changed);
        Assertions.assertEquals(2, store.fetchAll().size());
        Assertions.assertEquals(2, store.fetchAllUncompleted().size());
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.COMPLETED).listFiles().length);
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.UNVALIDATED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.FAILED).listFiles().length);

        WorkItem changedAgain = base.but()
                .withCurrentStatus(Verwerkingsstatus.COMPLETED)
                .build();
        store.upsert(changedAgain);
        Assertions.assertEquals(2, store.fetchAll().size());
        Assertions.assertEquals(1, store.fetchAllUncompleted().size());
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.COMPLETED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.UNVALIDATED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.FAILED).listFiles().length);

        WorkItem changedLastTime = base.but()
                .withCurrentStatus(Verwerkingsstatus.FAILED)
                .build();
        store.upsert(changedLastTime);
        Assertions.assertEquals(2, store.fetchAll().size());
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.COMPLETED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.UNVALIDATED).listFiles().length);
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.FAILED).listFiles().length);

        WorkItem toAddAndDelete = base.but()
                .withDosisItem(baseDosisItem.but()
                        .withId(UUID.randomUUID())
                        .build())
                .build();
        store.upsert(toAddAndDelete);
        Assertions.assertEquals(3, store.fetchAll().size());
        Assertions.assertEquals(2, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertTrue(store.delete(toAddAndDelete));
        Assertions.assertEquals(2, store.fetchAll().size());
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertFalse(store.delete(base.but()
                .withDosisItem(baseDosisItem.but()
                        .withId(UUID.randomUUID())
                        .build())
                .build()));
    }

    /**
     * Controleert of een nieuwe diskstore verder gaat op basis van het filesysteem.
     */
    @Test
    @DisplayName("Nieuwe store vertrekt van situatie op disk.")
    public void newDiskStoreShouldntMatter() {
        basicStoreTest();
        setup();
        Assertions.assertEquals(2, store.fetchAll().size());
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.TODO).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.COMPLETED).listFiles().length);
        Assertions.assertEquals(0, store.getStorageFolder(Verwerkingsstatus.UNVALIDATED).listFiles().length);
        Assertions.assertEquals(1, store.getStorageFolder(Verwerkingsstatus.FAILED).listFiles().length);
    }

    /**
     * Test specifiek het geval waarbij de rootfolder van de diskstore nog niet bestaat.
     */
    @Test
    @DisplayName("Rootfolder wordt aangemaakt wanneer deze nog niet bestaat.")
    void testCreationOfRootFolder() {
        store = new DiskStore(rootFolder.getAbsolutePath() + File.separator + UUID.randomUUID(),
                new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule()));
        basicStoreTest();
    }

    /**
     * Test het geval waarbij er een ongerelateerd bestand staat in een itemfolder
     */
    @Test
    @DisplayName("Ongekende bestanden hebben geen invloed op diskstore.")
    void testUnknownFileInStoreFolder() throws IOException {
        File toDoStore = store.getStorageFolder(Verwerkingsstatus.TODO);
        File newFile = new File(toDoStore.getAbsoluteFile() + File.separator + "someUnrelatedFile.json");
        Assertions.assertTrue(newFile.createNewFile());
        Assertions.assertEquals(0, store.fetchAll().size());
        store.upsert(new WorkItem.Builder()
                .withDosisItem(CommonTestMother.someDosisItem().build())
                .withCurrentStatus(Verwerkingsstatus.TODO)
                .build());
        Assertions.assertEquals(1, store.fetchAll().size());
        File[] toDoFiles = store.getStorageFolder(Verwerkingsstatus.TODO).listFiles();
        Assertions.assertEquals(2, toDoFiles.length);
    }
}
