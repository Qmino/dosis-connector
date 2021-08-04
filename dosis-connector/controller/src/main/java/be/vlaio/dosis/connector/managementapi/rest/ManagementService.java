package be.vlaio.dosis.connector.managementapi.rest;

import be.vlaio.dosis.connector.controller.DosisController;
import be.vlaio.dosis.connector.managementapi.dto.DosisConnectorStatusTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/")
public class ManagementService {

    @Autowired
    private DosisController controller;

    @GetMapping("heartbeat")
    public String getHeartBeat() {
        return "OK";
    }

    @GetMapping("status")
    @ResponseBody
    public DosisConnectorStatusTO getStatus() {
        return new DosisConnectorStatusTO.Builder().from(controller.getStatus()).build();
    }
}
