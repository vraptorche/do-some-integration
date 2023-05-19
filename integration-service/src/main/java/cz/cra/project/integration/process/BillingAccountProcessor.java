package cz.cra.project.integration.process;

import cz.cra.project.integration.domain.Account;
import cz.cra.project.integration.domain.CommonModel;
import cz.cra.project.integration.domain.IntegrationApiModel;
import cz.cra.project.integration.util.BillingAccountConverter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BillingAccountProcessor implements Processor {
	@Override
	public void process(Exchange exchange) {
		final Object body = exchange.getMessage().getBody();
		if (body instanceof CommonModel commonModel) {
			final IntegrationApiModel apiModel = commonModel.getApiModel();
			if (apiModel instanceof Account account) {
				exchange.getMessage().setBody(BillingAccountConverter.toBillingAccount(account));
			}

		}
	}
}
