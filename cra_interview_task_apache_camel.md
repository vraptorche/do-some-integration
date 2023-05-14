# CRA Interview Task - Do some integration

Target is create a simple Integration application.

## TODO
- Create public **GitHub** repository named: **Do Some Integration**
  - Repo should contains multi module **Maven** project
    - first module: **IntegrationService**
    - second module: **BillingService**
    <br><br>
- Create application **IntegrationService** according to sequence diagram
  - Use [Apache Camel on Spring Boot](https://camel.apache.org/camel-spring-boot/next/spring-boot.html)
    - **Please, do not use Spring Boot controllers for REST API!**
    - Camel aspects e.g. context, routes, etc. have to be implemented using [**Spring DSL**](https://camel.apache.org/manual/spring-xml-extensions.html)
    - For other functionality (mapper, utils, etc.) you can use classic Java
    <br><br>
- Create application **BillingService** according to sequence diagram
  - Use [Apache Camel on Spring Boot](https://camel.apache.org/camel-spring-boot/next/spring-boot.html)
    - **Please, do not use Spring Boot controllers for REST API!**
    - Camel aspects e.g. context, routes, etc. have to be implemented using [**Spring DSL**](https://camel.apache.org/manual/spring-xml-extensions.html)
    - For other functionality (mapper, utils, etc.) you can use classic Java
    <br><br>
- Some examples:
  - https://www.baeldung.com/spring-apache-camel-tutorial
  - https://examples.javacodegeeks.com/enterprise-java/apache-camel/apache-camel-spring-example/
  - https://github.com/apache/camel-spring-boot-examples

```mermaid
sequenceDiagram
	participant CL as Client
	participant IS as Integration Service
	participant MQ as Message Broker
	participant BS as Billing Service
	
	rect rgb(117, 179, 255)
		Note over CL,IS: Client sent a "CRM" Account data to Integration Service via REST API 
		Note over CL,IS: Account data is JSON object (see "Data model / Account" section) 
		CL->>+IS: POST http://localhost:8080/CRMEvent/Account
		IS-->>+CL: return 200 (no response body)
	end
	
	rect rgb(137, 158, 146)
		IS->>+IS: Log received http request (method, url, body)
		Note over IS: see ""Data model / Common Model" section
		IS->>+IS: Map request body to CommonModel
		Note over IS: see ""Data model / Common Model" section
		IS->>+IS: Set CommonModel.operation = PostAccount
	
		IS->>+IS: Log prepared CommonModel
	
		Note over IS,MQ: Sent prepared Common Model to mediation JMS Queue as JSON
		IS->>+IS: marshal CommonModel to JSON
		IS->>+IS: Log marshaled CommonModel
		IS->>+MQ: to JMS Queue cra.integration.mediation
	end
	
	Note over IS: Simulate some processing
	IS->>+IS: Sleep 1000
	
	rect rgb(214, 109, 86)
		Note over MQ,IS: Receive JSON message from mediation JMS Queue
		MQ->>+IS:	from JMS Queue cry.integration.mediation
		IS->>+IS: Log received message payload 
		IS->>+IS:	unmarshal received JSON message to CommonModel
		IS->>+IS: Log unmarshalled CommonModel
	end
	
	rect rgb(80, 70, 145)
		opt CommonModel.operation == PostAccount
			IS->>+IS: map CommonModel to BillingAccount
			
			Note over IS,BS: Sent BillingAccount to Billing Service via REST API
			IS->>+IS: Log prepared BillingAccount
			IS->>+BS: POST http://localhost:9090/BillingService/BillingAccount
			BS-->>+IS: return 201 (no response body)
			
			BS->>+BS: Log received http request (method, url, body)
		end
	end 
```

## Data Model
### Account
#### Class
```java
@Data
public class Account extends IntegrationApiModel {
	
	private Long id;
	
	private String firstname;
	
	private String lastname;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updatedAt;

}
```
#### JSON
```json
{
  "id": 12345,
  "firstname": "John",
  "lastname": "Doe",
  "updatedAt": "2023-02-14 10:38:00"
}
```

### CommonModel
#### Class
```java
@Data
public class CommonModel {
	
	private String operation;
	
	private IntegrationApiModel apiModel;

}
```

### BillingAccount
#### Class
```java
@Data
public class BillingAccount {
	
	private Long id;
	
	private String name; /* firstname + lastname */
		
	@JsonFormat(pattern="yyyy-MM-dd")	
	private Date lastUpdate;
		
}
```
#### JSON
```json
{
  "id": 12345,
  "name": "John Doe",
  "lastUpdate": "2023-02-14"
}
```