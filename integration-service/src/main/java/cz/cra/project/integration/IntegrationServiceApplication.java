package cz.cra.project.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:integration-camel-config.xml")
public class IntegrationServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(IntegrationServiceApplication.class, args);
	}
}
