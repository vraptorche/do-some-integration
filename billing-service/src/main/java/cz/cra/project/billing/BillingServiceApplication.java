package cz.cra.project.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:billing-camel-config.xml")
public class BillingServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}
}
