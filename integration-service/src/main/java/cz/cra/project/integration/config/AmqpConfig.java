package cz.cra.project.integration.config;

import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
	@Value("${amqp.uri}")
	private String remoteURI;

	@Value("${amqp.username}")
	private String username;

	@Value("${amqp.password}")
	private String password;

	@Bean
	public AMQPComponent amqpComponent() {
		return new AMQPComponent(amqpConnectionFactory());
	}

	@Bean
	public JmsConnectionFactory amqpConnectionFactory() {
		JmsConnectionFactory connectionFactory = new JmsConnectionFactory();

		connectionFactory.setRemoteURI(remoteURI);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}
}
