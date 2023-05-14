package cz.cra.project.integration.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class Account implements IntegrationApiModel {

	private Long id;

	private String firstname;

	private String lastname;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updatedAt;

}