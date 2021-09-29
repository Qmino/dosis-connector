package be.vlaio.dosis.connector.managementapi.rest;

import be.vlaio.dosis.connector.controller.DosisController;
import be.vlaio.dosis.connector.managementapi.dto.DosisConnectorStatusTO;
import be.vlaio.dosis.connector.managementapi.dto.PollerActivationTO;
import be.vlaio.dosis.connector.managementapi.dto.PollerSpecificationTO;
import be.vlaio.dosis.connector.managementapi.exceptions.InvalidInputException;
import be.vlaio.dosis.connector.managementapi.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Via deze webinterface biedt de DOSIS connector toegang tot runtime status informatie, en kan de werking
 * van de DOSIS connector applicatie worden beïnvloed.
 */
@RestController
@RequestMapping("/rest/")
public class ManagementService {

    @Autowired
    private DosisController controller;

    /**
     * Heartbeat. Geeft een vaste string terug om aan te geven dat de applicatie operationeel is.
     * @return De string "OK".
     */
    @GetMapping("heartbeat")
    public String getHeartBeat() {
        return "OK";
    }

    /**
     * Deze interface laat toe om de toestand van de verschillende componenten (pollers,
     * pusher, validator, dosis client, ...) te inspecteren at runtime.
     * @return De status van de dosis connector en de verschillende componenten.
     */
    @GetMapping("status")
    @ResponseBody
    public DosisConnectorStatusTO getStatus() {
        return new DosisConnectorStatusTO.Builder().from(controller.getStatus()).build();
    }

    /**
     * Wijzigt de active/inactive toestand van de poller met de gespecificeerde naam.
     * @param name de naam van de poller
     * @param activation de gewenste status.
     */
    @RequestMapping(
            value="poller/{pollerName}/active",
            consumes = "application/json",
            method = RequestMethod.PUT)
    public void setActivation(@PathVariable("pollerName") String name, @RequestBody PollerActivationTO activation) {
        if (activation.isActive() == null) {
            throw new InvalidInputException("'active' veld niet ingevuld.'");
        } else {
            controller.setActivityStatus(name, activation.isActive(), true);
        }
    }

    @RequestMapping(
            value = { "poller/{pollerName}" },
            method = RequestMethod.PUT,
            consumes = "application/json")
    @ResponseBody
    private void updateConfig(@PathVariable("pollerName") String name, @RequestBody PollerSpecificationTO pollerConfig) {
        // TODO
    }

    @RequestMapping(
            value = { "poller" },
            method = RequestMethod.POST,
            consumes = "application/json")
    private void createNewMapping(@RequestBody PollerSpecificationTO pollerConfig) {
        // TODO
    }
}
