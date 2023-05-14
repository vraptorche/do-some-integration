package cz.cra.project.integration.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cra.project.integration.util.BillingAccountConverter;
import cz.cra.project.integration.domain.Account;
import cz.cra.project.integration.domain.CommonModel;
import cz.cra.project.integration.domain.IntegrationApiModel;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import static org.apache.camel.Exchange.*;

@Component
public class RouteConfig extends RouteBuilder {

	public static final String AMQP_QUEUE = "amqp:queue:cra.integration.mediation";
	private static final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

	@Override
	public void configure() {

		restConfiguration()
				.inlineRoutes(true);

		rest("/CRMEvent/").bindingMode(RestBindingMode.json)
				.consumes(MediaType.APPLICATION_JSON_VALUE)
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.post("/Account").id("account-create")
				.type(Account.class)
				.to("direct:submitAccount");

		from("direct:submitAccount").id("submit-account")
				.wireTap("direct:processAccount").end()
				.log("Setting headers")
				.setHeader(HTTP_RESPONSE_CODE, constant(204))
				.setHeader(HTTP_RESPONSE_TEXT, constant("Account accepted for creation"));

		from("direct:processAccount").id("process-account")
				.log(">>> Received: ${body}")
				.process(exchange -> {
					final Account account = exchange.getMessage().getBody(Account.class);
					final CommonModel commonModel = new CommonModel();
					commonModel.setApiModel(account);
					commonModel.setOperation("PostAccount");
					exchange.getMessage().setBody(commonModel);
				})
				.to("stream:out")
				.marshal().json(JsonLibrary.Jackson)
				.log("Common model: ${body}")
				.setExchangePattern(ExchangePattern.InOnly)
				.multicast()
				.to(AMQP_QUEUE)
				.to("direct:consumeAccount")
				.end();

		from("direct:consumeAccount").id("consume-account")
				.delayer(1_000)
				.log("Consume Account")
				.process(exchange -> {
					try (ConsumerTemplate template = exchange.getContext().createConsumerTemplate()) {
						final Exchange received = template.receive(AMQP_QUEUE);
						final byte[] body = (byte[]) received.getMessage().getBody();
						final JsonNode jsonNode = mapper.readTree(body);
						final Account apiModel = mapper.treeToValue(jsonNode.get("apiModel"), Account.class);
						final CommonModel commonModel = new CommonModel();
						commonModel.setApiModel(apiModel);
						final String operation = jsonNode.get("operation").toString();
						commonModel.setOperation(operation);
						exchange.getMessage().setBody(commonModel);
					}
				})
				.log("Payload from queue: ${body}")
				.to("direct:billingAccount");

		from("direct:billingAccount").id("billing-account")
				.setHeader(HTTP_METHOD, constant("POST"))
				.setHeader(CONTENT_TYPE, constant("application/json"))
				.setHeader(HTTP_URI, simple("http://localhost:9090/BillingService/BillingAccount"))
				.process(exchange -> {
					final Object body = exchange.getMessage().getBody();
					if (body instanceof CommonModel commonModel) {
						final IntegrationApiModel apiModel = commonModel.getApiModel();
						if (apiModel instanceof Account account) {
							exchange.getMessage().setBody(BillingAccountConverter.toBillingAccount(account));
						}

					}
				})
				.marshal().json(JsonLibrary.Jackson)
				.to("http://localhost:9090/BillingService/BillingAccount");
	}
}
