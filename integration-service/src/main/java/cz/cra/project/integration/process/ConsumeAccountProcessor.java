package cz.cra.project.integration.process;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cra.project.integration.domain.Account;
import cz.cra.project.integration.domain.CommonModel;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class ConsumeAccountProcessor implements Processor {

	public static final String AMQP_QUEUE = "amqp:queue:cra.integration.mediation";
	private static final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
	@Override
	public void process(Exchange exchange) throws Exception {
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
	}
}
