## Build
Project uses maven and can be build like so:
```
maven clean install
```
## Message Broker
Pre-requisites:
docker must be installed (with compose plugin or separate docker-compose)
RabbitMQ is used as message broker.
To run locally docker compose file is prepared in deploy directory
To run message broker:
```
cd deploy
docker compose up -d
```
## Services
Two services are available:

 - Integration Service
 - Billing Service
 -
Run services:
```
java -jar integration-service/target/integration-service-1.0-SNAPSHOT.jar
java -jar billing-service/target/billing-service-1.0-SNAPSHOT.jar
```
Test Services:

**Integration Service**
```
curl --location 'http://localhost:8080/CRMEvent/Account' \
--header 'Content-Type: application/json' \
--data '{
    "id": 12345,
    "firstname": "John",
    "lastname": "Doe",
    "updatedAt": "2023-02-14 10:38:00"
}'
```
 **Billing Service**
```
curl --location 'http://localhost:9090/BillingService/BillingAccount' \
--header 'Content-Type: application/json' \
--data '{
    "id": 12345,
    "name": "John Doe",
    "lastUpdate": "2023-02-14"
}'
```


