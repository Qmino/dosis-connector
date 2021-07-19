package be.vlaio.dosis.connector.wip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "be.vlaio.dosis.connector")
public class DosisConnector {

	public static void main(String[] args) {
		SpringApplication.run(DosisConnector.class, args);
	}

}
