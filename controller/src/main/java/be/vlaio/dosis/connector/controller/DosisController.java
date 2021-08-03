package be.vlaio.dosis.connector.controller;

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
public class DosisController implements CommandLineRunner {

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
				LOGGER.info("Starting poller from config-file: " + spec.getName());
				activePollers.put(spec.getName(), pollerFactory.apply(spec).apply(wip, dosisItemFactory));
			}
		}
	}

	public DosisConnectorStatus getStatus() {
		return new DosisConnectorStatus.Builder()
				.withPollers(activePollers.values().stream().map(Poller::getStatus).collect(Collectors.toList()))
				.withWorkInProgress(wip.getStatus())
				.build();
	}

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
	}

	@Override
	public void run(String... args) throws Exception {
//		LOGGER.info("Hello from {}", this.getClass().getSimpleName());
	}
}
