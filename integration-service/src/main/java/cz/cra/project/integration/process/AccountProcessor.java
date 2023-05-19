package cz.cra.project.integration.process;

import cz.cra.project.integration.domain.Account;
import cz.cra.project.integration.domain.CommonModel;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class AccountProcessor implements Processor {
	@Override
	public void process(Exchange exchange) {
		final Account account = exchange.getMessage().getBody(Account.class);
		final CommonModel commonModel = new CommonModel();
		commonModel.setApiModel(account);
		commonModel.setOperation("PostAccount");
		exchange.getMessage().setBody(commonModel);
	}
}
