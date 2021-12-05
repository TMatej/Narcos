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
  * `pharmacy-service`: http://localhost:8082/
  * `medicine-service`: http://localhost:8083/
  * `order-service`: http://localhost:8084/
  * `record-service` : http://localhost:8085/
  * `auth-service`: http://localhost:8888/
 
### User Service
This service manages individual users and stores them in a relational daabse. It also allows authentication of the users (admin/user) for using secured endpoints.
User Service contains following endpoints:
  * `GET /auth/login` - Endpoint ensures authentication of individual users and allowing access to all secured endpoints of all services accordingly. For doing so it uses so called token authentication (bearer authentication). As a input it consumes JSON object conzisting of `email` and `password` properties. After successful authentication of received credentials it outputs a time-limited `JWT token` that holds ID of the authenticated person and the role (Admin/User), which the person represents in the system.
  * `POST /auth/register` - Endpoint for registration of users. As a input it consumes JSON object conzisting of `email`, `password`, `firstName`, `lastName` and desired `role`. After successful verification that the supplied email has not been used yet, the record is inserted into db table `person` and acknowledge message is returned.
  * `GET /users/{id}` - Lists requested user by id. Can be called by any authenticated user, but only `Admin` has rights to receive information about any other user. Basic `User` can only show their personal information.
  * `GET /users?id={}` - Similarly to previous endpoint it serves to list information about user, but instead of using path parameter it utilizes query parameter `id` which  holds comma separated list of ids. This endpoit is accessible only to the `Admin`.
  * `PUT /users/{id}` - Endpoint for updating of user specified by `id`. As a input it consumes JSON object conzisting of `email`, `password`, `firstName`, `lastName` and `role`. 
  * `DELETE /users/{id}` - Endpoint for deleting of user specified by `id`. 
### Medicine Service doc
### Order Service doc
### Pharmacy Service doc
### Auth Service doc
Endpoints:  
  * 
  * 

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
 
