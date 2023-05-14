package cz.cra.project.billing.config;

import cz.cra.project.billing.domain.BillingAccount;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.apache.camel.Exchange.HTTP_RESPONSE_TEXT;
import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;

@Component
public class RouteConfig extends RouteBuilder {

	@Override
	public void configure() {

		rest("/BillingService/").bindingMode(RestBindingMode.json)
				.consumes(MediaType.APPLICATION_JSON_VALUE)
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.post("/BillingAccount").routeId("billing-account-create")
				.type(BillingAccount.class)
				.to("direct:submitAccount");

		from("direct:submitAccount")
				.log("Setting headers")
				.setHeader(HTTP_RESPONSE_CODE, constant(201))
				.setHeader(HTTP_RESPONSE_TEXT, constant("Account created"))
				.log("Payload: ${body}")
				.to("stream:out");
	}
}