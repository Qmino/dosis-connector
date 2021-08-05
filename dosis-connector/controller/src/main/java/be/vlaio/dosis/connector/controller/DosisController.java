package be.vlaio.dosis.connector.controller;

import be.vlaio.dosis.connector.managementapi.exceptions.ResourceNotFoundException;
import be.vlaio.dosis.connector.poller.DosisItemFactory;
import be.vlaio.dosis.connector.poller.Poller;
import be.vlaio.dosis.connector.common.DosisConnectorStatus;
import be.vlaio.dosis.connector.common.PollerSpecification;
import be.vlaio.dosis.connector.springconf.PollersConfiguration;
import be.vlaio.dosis.connector.wip.WorkInProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DosisController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DosisController.class);

	@Autowired
	private PollersConfiguration props;
	@Autowired
	private Function<PollerSpecification, BiFunction<WorkInProgress, DosisItemFactory, Poller>> pollerFactory;
	@Autowired
	private WorkInProgress wip;
	@Autowired
	private DosisItemFactory dosisItemFactory;

	private Map<String, Poller> activePollers = new HashMap<>();

	@PostConstruct
	public void init() {
		for (PollerSpecification spec: props.getInstances()) {
			if (!activePollers.containsKey(spec.getName())) {
				LOGGER.info("Aanmaken van poller uit config-bestand: " + spec.getName());
				addPoller(spec);
			}
		}
	}

	public DosisConnectorStatus getStatus() {
		return new DosisConnectorStatus.Builder()
				.withPollers(activePollers.values().stream().map(Poller::getStatus).collect(Collectors.toList()))
				.withWorkInProgress(wip.getStatus())
				.build();
	}

	public boolean setActivityStatus(String pollerName, boolean active, boolean resetBackoff) {
		Poller poller = activePollers.get(pollerName);
		if (poller == null) {
			throw new ResourceNotFoundException("Geen poller met de naam " + pollerName + " is actief.");
		} else {
			poller.setActive(active);
			LOGGER.info("Poller " + pollerName + ": activity status aangepast naar " + active);
			if (resetBackoff) {
				poller.resetBackoff();
				LOGGER.info("Poller " + pollerName + ": exponential backoff reset");
			}
		}
		return poller.isActive();
	}

	public boolean addPoller(PollerSpecification specification) {
		if (activePollers.containsKey(specification.getName())) {
			LOGGER.warn("Poller met naam " + specification.getName() + " bestaat reeds, niet aangemaakt.");
			return false;
		} else {
			activePollers.put(specification.getName(), pollerFactory.apply(specification).apply(wip, dosisItemFactory));
			return true;
		}
	}

}
