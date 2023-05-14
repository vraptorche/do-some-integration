package cz.cra.project.billing.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BillingAccount {

	private Long id;

	private String name; /* firstname + lastname */

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date lastUpdate;

}
