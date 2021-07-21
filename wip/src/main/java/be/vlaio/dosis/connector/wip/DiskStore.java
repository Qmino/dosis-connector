package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.DosisItem;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 *     <li>Onder pollerconfigs 1 bestand per poller met daarin de configuratie van de poller</li> [TODO]
 * </ul>
 */
@Component
public class DiskStore {

    private final Logger logger = LoggerFactory.getLogger(DiskStore.class);
    private final Map<Verwerkingsstatus, File> folders = new HashMap<>();
    private final ObjectMapper mapper;

    /**
     * Constructor.
     *
     * @param rootFolder de hoofdfolder waarin de diskstore alle bestanden plaatst en leest. Indien deze niet bestaat
     *                   tracht de diskstore deze aan te maken. De applicatie moet moet schrijfrechten hebben in de
     *                   folder (of deze kunnen aanmaken), of de initialisatie van de component zal falen met een
     *                   exception.
     * @param mapper de jackson json mapper voor serializatie en deserializatie van de objecten naar bestand.
     */
    public DiskStore(@Value("${dosisgateway.storagefolder}") String rootFolder, ObjectMapper mapper) {
        this.mapper = mapper;
        logger.debug("Initializing diskstore in " + rootFolder);
        try {
            File root = createIfNotExists(rootFolder);
            File itemFolder = createIfNotExists(root, "items");
            for (Verwerkingsstatus status : Verwerkingsstatus.values()) {
                folders.put(status, createIfNotExists(itemFolder, status.name().toLowerCase()));
            }
        } catch (SecurityException se) {
            logger.error("Could not initialize diskwriter", se);
        }
    }

    /**
     * @return Geeft alle dosisitems terug die op de harde schijf bestaan, in een enkele lijst.
     */
    public List<DosisItem> fetchAll() {
        List<DosisItem> result = new ArrayList<>();
        for (File folder : folders.values()) {
            result.addAll(readItems(folder));
        }
        return result;
    }

    /**
     * Slaat het dosisitem op schijf op. Indien het dosisitem reeds bestond, wordt het geupdate. Een update kan
     * eventueel een verplaatsing van het bestand tot gevolg hebben, als de dosisitem van plaats is veranderd.
     *
     * @param item Het item dat moet worden opgeslagen.
     */
    public void upsert(DosisItem item) {
        try {
            String json = mapper.writeValueAsString(item);
            File folder = folders.get(item.getProcessingStatus());
            if (folder == null) {
                // Zou nooit mogen gebeuren, aangezien dit betekent dat de component incorrect is geinitialiseerd.
                // We verkiezen ervoor een exception te gooien ipv data te verliezen.
                throw new RuntimeException("Critical error: unable to store dosisitem with status: " +
                        item.getProcessingStatus());
            } else {
                String fileName =  item.getId() + ".json";
                writeToFile(json, folder.getAbsolutePath() + File.separator + fileName);
                // Delete the file in all other statuses (if they exist) and collect those that can not be deleted!
                List<File> problematicFiles = folders.values().stream()
                        .filter(f -> f != folder)
                        .flatMap(f -> streamOf(f.listFiles((dir, name) -> name.equals(item.getId() + ".json"))))
                        .filter(f -> ! f.delete())
                        .collect(Collectors.toList());
                if (problematicFiles.size() > 0)
                {
                    throw new RuntimeException("Could not delete following files: " +
                            problematicFiles.stream().map(File::getName).collect(Collectors.joining(", ")));
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Unexpected JSON serizalization issue", e);
        } catch (IOException e) {
            logger.error("Unable to write item " + item.getId() + " to file.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Verwijdert het dosisitem van fysiek van de schijf, er wordt hierbij enkel gekeken naar de ID van het item, niet
     * naar de verwerkingsstatus.
     * @param item het te verwijderen item
     * @return of er effectief een bestand is verwijderd
     */
    public boolean delete(DosisItem item) {
        String relativeFileName = File.separator + item.getId() + ".json";
        // Normaal staat het item in de folder van de verwerkingsstatus van het item.
        // Voor de zekerheid kijken we in alle folders.
        boolean result = false;
        for (File folder: folders.values()) {
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
     * @param status de status waar de locatie van de items voor opgevraagd wordt.
     * @return De folder waarin items van de gegeven status (zouden) worden opgeslagen (indien van toepassing).
     */
    public File getStorageFolder(Verwerkingsstatus status) {
        return folders.get(status);
    }

    /**
     * Hulpmethode die een string naar een bestand schrijft. Indien het bestand al bestaat wordt het overschreven.
     * @param toWrite de string die in het bestand moet worden geschreven.
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
    private List<DosisItem> readItems(File folder) {
        List<DosisItem> result = new ArrayList<>();
        if ((folder != null) && folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        String s = Files.readString(Path.of(file.getAbsolutePath()));
                        result.add(mapper.readValue(s, DosisItem.class));
                    } catch (JacksonException je) {
                        logger.debug("File " + file.getName() + " does not correspond to valid DosisItem, skipped.");
                    } catch (IOException e) {
                        logger.warn("Could not read " + file.getName() + ": skipping.");
                    }
                }
            }
        }
        return result;
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
            if (! file.mkdir()) {
                throw new RuntimeException("Problem creating folder: " + absoluteFolderName);
            }
        }
        return file;
    }

    private File createIfNotExists(File parentFolder, String relativeSubfolderName) {
        File file = new File(parentFolder.getAbsolutePath() + File.separator + relativeSubfolderName);
        if (!file.exists()) {
            if (! file.mkdir()) {
                throw new RuntimeException("Problem creating folder: " + file.getAbsolutePath());
            }
        }
        return file;
    }
}
