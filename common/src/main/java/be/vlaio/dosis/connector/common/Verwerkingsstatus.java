package be.vlaio.dosis.connector.common;

/**
 * The processing status of this item in the dosis connector. This is unrelated to the actual dosis status.
 */
public enum Verwerkingsstatus {
    /**
     * The item is known by the DOSIS connector, but not yet submitted to DOSIS.
     */
    TODO,
    /**
     * The item has been submitted to DOSIS, but no response has been received by DOSIS regarding the status update.
     */
    UNVALIDATED,
    /**
     * The item has been succesfully submitted to DOSIS and verified.
     */
    COMPLETED,
    /**
     * An attemp has been made by the connector to submit this item to DOSIS, but the item has been rejected or failed
     * validation. This is a final state.
     */
    FAILED
}
