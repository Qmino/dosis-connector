package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.Verwerkingsstatus;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Standaard disk opslagklasse. Deze klasse schrijft en DosisItems en de huidige toestand van de pollers naar de harde
 * schijf. Intern wordt een eenvoudige mapstructuur aangehouden:
 * <ul>
 *     <li>Op topniveau twee folders: items en pollerconfigs</li>
 *     <li>Onder items een subfolder per verwerkingsstatus.</li>
 *     <li>In deze folder, 1 bestand per DosisItem in deze status. Elk item heeft als bestandsnaam de UUID van het item,
 *     met bestandsextensie .json</li>
 * </ul>
 * TODO: Onder pollerconfigs 1 bestand per poller met daarin de configuratie van de poller
 */
@Component
public class DiskStore {

    private final Logger logger = LoggerFactory.getLogger(DiskStore.class);
    private final Map<Verwerkingsstatus, File> statusFolders = new HashMap<>();
    private File pollerConfigFolder;
    private final ObjectMapper mapper;

    /**
     * Constructor.
     *
     * @param rootFolder de hoofdfolder waarin de diskstore alle bestanden plaatst en leest. Indien deze niet bestaat
     *                   tracht de diskstore deze aan te maken. De applicatie moet moet schrijfrechten hebben in de
     *                   folder (of deze kunnen aanmaken), of de initialisatie van de component zal falen met een
     *                   exception.
     * @param mapper     de jackson json mapper voor serializatie en deserializatie van de objecten naar bestand.
     */
    public DiskStore(@Value("${dosisgateway.storagefolder}") String rootFolder, ObjectMapper mapper) {
        this.mapper = mapper;
        if (rootFolder == null || rootFolder.trim().isEmpty()) {
            rootFolder = System.getProperty("user.dir");
        }
        try {
            File root = createIfNotExists(rootFolder);
            File itemFolder = createIfNotExists(root, "items");
            pollerConfigFolder = createIfNotExists(root, "pollerconfigs");
            for (Verwerkingsstatus status : Verwerkingsstatus.values()) {
                statusFolders.put(status, createIfNotExists(itemFolder, status.name().toLowerCase()));
            }
            logger.info("Diskstore geïnitialiseerd in " + root.getAbsolutePath());
        } catch (SecurityException se) {
            logger.error("Probleem bij initializatie van de diskwriter", se);
        }
    }

    /**
     * @return Geeft alle workitems terug die op de harde schijf bestaan, in een enkele lijst.
     */
    public List<WorkItem> fetchAll() {
        List<WorkItem> result = new ArrayList<>();
        for (File folder : statusFolders.values()) {
            result.addAll(readItems(folder));
        }
        return result;
    }

    /**
     * @return Geeft alle dosisitems terug die op de harde schijf bestaan en die niet de verwerkingsstatus
     * COMPLETED hebben, in een enkele lijst.
     */
    public List<WorkItem> fetchAllUncompleted() {
        List<WorkItem> result = new ArrayList<>();
        for (Map.Entry<Verwerkingsstatus, File> entry : statusFolders.entrySet()) {
            if (entry.getKey() != Verwerkingsstatus.COMPLETED) {
                result.addAll(readItems(entry.getValue()));
            }
        }
        return result;
    }

    /**
     * Haalt een workitem op met een gegeven id (indien dit bestaat).
     *
     * @param id de gewenste id
     * @return Het gevraagde item indien het bestaat.
     */
    public Optional<WorkItem> fetchById(UUID id) {
        for (File folder : statusFolders.values()) {
            File file = new File(folder.getAbsolutePath() + File.separator + id + ".json");
            if (file.exists()) {
                return readWorkItemFromFile(file);
            }
        }
        return Optional.empty();
    }

    /**
     * Slaat het dosisitem op schijf op. Indien het dosisitem reeds bestond, wordt het geüpdatet. Een update kan
     * eventueel een verplaatsing van het bestand tot gevolg hebben, als de dosisitem van plaats is veranderd.
     *
     * @param item Het item dat moet worden opgeslagen.
     */
    public void upsert(WorkItem item) {
        try {
            String json = mapper.writeValueAsString(item);
            File folder = statusFolders.get(item.getCurrentStatus());
            if (folder == null) {
                // Zou nooit mogen gebeuren, aangezien dit betekent dat de component incorrect is geïnitialiseerd.
                // We verkiezen ervoor een exception te gooien ipv data te verliezen.
                throw new RuntimeException("Kritische fout: kan dosisitem met status: " +
                        item.getCurrentStatus() + " niet opslaan.");
            } else {
                String fileName = item.getDosisItem().getId() + ".json";
                writeToFile(json, folder.getAbsolutePath() + File.separator + fileName);
                // Delete the file in all other statuses (if they exist) and collect those that can not be deleted!
                List<File> problematicFiles = statusFolders.values().stream()
                        .filter(f -> f != folder)
                        .flatMap(f -> streamOf(f.listFiles((dir, name) -> name.equals(item.getDosisItem().getId() + ".json"))))
                        .filter(f -> !f.delete())
                        .collect(Collectors.toList());
                if (problematicFiles.size() > 0) {
                    throw new RuntimeException("Volgende bestanden konden niet verwijderd worden: " +
                            problematicFiles.stream().map(File::getName).collect(Collectors.joining(", ")));
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Onverwachtte JSON serizalizatie fout", e);
        } catch (IOException e) {
            logger.error("Kan dosisitem " + item.getDosisItem().getId() + " niet wegschrijven naar bestand.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Slaat voor een gegegeven poller, het laatst afgehaalde en verwerkte element op.
     *
     * @param pollerName    de naam van de poller
     * @param upstreamIndex de index die moet gesaved worden
     * @param item het laatst verwerkte workitem
     */
    public void saveLastProcessedIndex(String pollerName, long upstreamIndex, WorkItem item) {
        File pollerFile = new File(pollerConfigFolder.getAbsolutePath() + File.separator + pollerName + ".json");
        try {
            String json = mapper.writeValueAsString(
                    new PollerConfig.Builder()
                            .withLastId(item.getId())
                            .withLastUpdate(LocalDateTime.now())
                            .withPollerName(pollerName)
                            .withLastIndex(upstreamIndex)
                            .build()
            );
            writeToFile(json, pollerFile.getAbsolutePath());
        } catch (JsonProcessingException e) {
            logger.error("Onverwachtte JSON serializatie fout", e);
        } catch (IOException e) {
            logger.error("Kan pollerconfiguratie niet wegschrijven naar bestand: " + pollerFile.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Vraagt voor een gegeven poller de laatst gekende index op. Indien voor de poller geen index
     * gekend is, wordt 0 teruggegeven.
     *
     * @param pollerName de naam van de poller
     * @return de index van het laatste element dat door de poller is geregistreerd.
     */
    public long getLastProcessedIndex(String pollerName) {
        File pollerFile = new File(pollerConfigFolder.getAbsolutePath() + File.separator + pollerName + ".json");
        if (! pollerFile.exists()) {
            return -1L;
        } else {
            try {
                String s = Files.readString(Path.of(pollerFile.getAbsolutePath()));
                if (s == null || s.isBlank()) {
                    return -1L;
                }
                PollerConfig conf = mapper.readValue(s, PollerConfig.class);
                return conf == null ? 0 : conf.getLastIndex();
            } catch (JacksonException je) {
                logger.debug("Bestand " + pollerFile.getName() + " komt niet overeen met een geldige poller configuratie.");
                return -1L;
            } catch (IOException e) {
                logger.warn("Kan bestand " + pollerFile.getName() + " niet lezen. Pollerconfiguratie overgeslagen.");
                return -1L;
            }
        }
    }


    /**
     * Verwijdert het dosisitem van fysiek van de schijf, er wordt hierbij enkel gekeken naar de ID van het item, niet
     * naar de verwerkingsstatus.
     *
     * @param item het te verwijderen item
     * @return of er effectief een bestand is verwijderd
     */
    public boolean delete(WorkItem item) {
        String relativeFileName = File.separator + item.getDosisItem().getId() + ".json";
        // Normaal staat het item in de folder van de verwerkingsstatus van het item.
        // Voor de zekerheid kijken we in alle folders.
        boolean result = false;
        for (File folder : statusFolders.values()) {
            File bestand = new File(folder.getAbsolutePath() + relativeFileName);
            if (bestand.exists()) {
                boolean deleted = bestand.delete();
                result = result || deleted; // do not inline due to shortcut of boolean logic!
            }
        }
        return result;
    }

    /**
     * Geeft de folder waar items van de gegeven status worden opgeslagen.
     *
     * @param status de status waar de locatie van de items voor opgevraagd wordt.
     * @return De folder waarin items van de gegeven status (zouden) worden opgeslagen (indien van toepassing).
     */
    public File getStorageFolder(Verwerkingsstatus status) {
        return statusFolders.get(status);
    }

    /**
     * Hulpmethode die een string naar een bestand schrijft. Indien het bestand al bestaat wordt het overschreven.
     *
     * @param toWrite  de string die in het bestand moet worden geschreven.
     * @param fileName de volledige (absolute) naam van het bestand
     * @throws IOException indien er niet naar het bestand kan worden geschreven
     */
    private void writeToFile(String toWrite, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(toWrite);
        writer.close();
    }

    /**
     * Hulpmethode die alle DosisItems uit een gegeven, bestaande, folder leest. Er wordt niet recursief gezocht.
     * Eventuele bestanden in de folder die niet overeenkomen met een dosisitem worden overgeslagen.
     *
     * @param folder de folder waarin de dosisitems gelezen moeten worden. Indien null of onbestaande, wordt een lege
     *               lijst van DosisItems teruggegeven.
     * @return Alle dosisitems in de folder.
     */
    private List<WorkItem> readItems(File folder) {
        List<WorkItem> result = new ArrayList<>();
        if ((folder != null) && folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    readWorkItemFromFile(file).ifPresent(result::add);
                }
            }
        }
        return result;
    }

    private Optional<WorkItem> readWorkItemFromFile(File file) {
        try {
            String s = Files.readString(Path.of(file.getAbsolutePath()));
            return Optional.of(mapper.readValue(s, WorkItem.class));
        } catch (JacksonException je) {
            logger.debug("Bestand " + file.getName() + " komt niet overeen met een geldig DosisItem, overgeslagen.");
            return Optional.empty();
        } catch (IOException e) {
            logger.warn("Kan bestand " + file.getName() + " niet lezen: overgeslagen.");
            return Optional.empty();
        }
    }

    private Stream<File> streamOf(File[] files) {
        if (files == null || files.length == 0) {
            return Stream.empty();
        } else {
            return Stream.of(files);
        }
    }

    private File createIfNotExists(String absoluteFolderName) {
        File file = new File(absoluteFolderName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new RuntimeException("Probleem bij aanmaak folder: " + absoluteFolderName);
            }
        }
        return file;
    }

    private File createIfNotExists(File parentFolder, String relativeSubfolderName) {
        File file = new File(parentFolder.getAbsolutePath() + File.separator + relativeSubfolderName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new RuntimeException("Probleem bij aanmaak folder: " + file.getAbsolutePath());
            }
        }
        return file;
    }

}
