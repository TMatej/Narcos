# Narcos
Medicine logistic application

## Description of the project

In order to use our logistic system user needs to authenticate. Authenticated user is able to retrieve the list of pharmacies, check their stockroom (resupply, send packge, sell, discard old), and check the description of medicine (CRUD on medicine).

Entities:
  * User
  * Medicine
  * Stockroom (Pharmacy)
  * Orders (Resupply, Delivery)

## Story/scenario of usage

_*Have a scenario that you can run that showcase some reactive properties of the system (those that you implemented): Responsiveness (quick response / always on), resiliency (self-healing service), elasticity (how the system can scale up/down when needed), message-driveness (asynchronous messages with loosely coupled / location-transparent services)_*

## Questions to be answered

1. **Why you think a microservice architecture can be appropriate:** ...
1. **Benefits of the using microservices in this project:** ...
1. **Drawbacks of microservices in this case:** ...

## Microservices
Here you can find list of available services.
  * `user-service`: http://localhost:8081/
  * `medicine-service`: http://localhost:8082/
  * `order-service`: http://localhost:8080/
  * `pharmacy-service`: http://localhost:8080/
  * `auth-service`: http://localhost:8888/
 
### User Service doc
### Medicine Service doc
### Order Service doc
### Pharmacy Service doc
### Auth Service doc
Endpoints:  
  * `GET /auth/login`
  * `POST /auth/register `

### Swagger/OpenAPI

### Health Check

## Deployment

## Dependencies
 * JDK 11
 * Maven 3.8.1+
 * [Docker](https://www.docker.com/)
 * [Postman](https://www.postman.com/product/what-is-postman/), [psql](https://www.postgresql.org/docs/9.2/app-psql.html) (for testing)

## Authors
 * Miroslav Lučkai <469288@mail.muni.cz>
 * Ondřej Machala <469122@mail.muni.cz>
 * Matej Turek <469107@mail.muni.cz>
 
