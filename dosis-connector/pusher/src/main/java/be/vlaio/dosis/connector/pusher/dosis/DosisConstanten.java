package be.vlaio.dosis.connector.pusher.dosis;

/**
 * Constanten die hardcoded zijn voor de dosisservices. Ze worden daarom niet via properties configureerbaar aangeboden.
 */
public class DosisConstanten {



    public static final String DOSSIER_STATUS_UPLOAD_URL = "/api/v1/dosis/dossiers";
    public static final String DOSSIER_STATUS_RESULTATEN = "/api/v1/dosis/resultaten/perUploadId";

    public static final String GRANT_TYPE = "client_credentials";
    public static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    public static final String SCOPE = "DosisImport"; // "DOSISImport";

    // Deze parameter bepaalt hoeveel "op voorhand" de accesstoken refresht wordt.
    // 300 betekent dat de accesstoken 5 minuten voor expiration refresht wordt
    // (refreshen is hoedanook lazy - het gebeurt alleen als de dosisclient een call moet uitvoeren)
    // Als deze waarde dus kleiner is dan de levensduur van de verkregen accesstokens, wordt bij elke call een
    // refresh van de accesstoken gedaan. Dosis accesstokens zijn typisch meer dan 10u geldig, dus 3 min is een goede
    // waarde.
    public static final int REFRESH_MARGIN = 180;
    public static final String BURGERRECHT = "Raadpleger";
}
