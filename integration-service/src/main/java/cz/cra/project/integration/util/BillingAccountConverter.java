package cz.cra.project.integration.util;

import cz.cra.project.integration.domain.Account;
import cz.cra.project.integration.domain.BillingAccount;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;

@Converter
public class BillingAccountConverter implements TypeConverters {
	public static BillingAccount toBillingAccount(Account account) {
		final BillingAccount billingAccount = new BillingAccount();
		billingAccount.setId(account.getId());
		billingAccount.setLastUpdate(account.getUpdatedAt());
		billingAccount.setName(String.join(" ", account.getFirstname(), account.getLastname()));
		return billingAccount;
	}
}
