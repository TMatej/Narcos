# Narcos
Medicine logistic application

## Description of the project

Our project represents a simple logistic system for distribution for medicine. In order to use our logistic system user needs to authenticate. Authenticated user is able to retrieve the list of pharmacies, check their stockroom (resupply, send package, sell, discard old), and check the description of medicine (CRUD on medicine).

Entities:
  * User
  * Medicine
  * Pharmacy
  * Orders (Resupply, Delivery)
  * Record

## Story/scenario of usage

New admin account is created. He creates new medication into database new pharmacy and record of storage in newly created pharmacy. Then he order said medication from pharmacy. Amount of medication in storage is reduced.
* User register into application using `/auth/register` endpoint
* User login into application using `/auth/login` endpoint
* User creates new medication using `/medicine` endpoint
* User creates new pharmacy using `/pharmacies` endpoint
* User creates new record using `/records` endpoint
* User creates new order using `/orders/new` endpoint


## Questions to be answered

#### **Benefits of using microservices in this project:**
 * `scalability` of the individual services instead of scaling the entire application.
 * `testability` of independent services which makes the the individual parts highly reliable.
 * `reusability` of loosely coupled services in other projects.
 * `distribution` of work among developers. Every developer can fully focus on their sub-project.  
 * `isolation` of a service failure. Even if one service fails the whole application can stay up running and faulty module may be redeployed. 
 
#### **Drawbacks of microservices in this case:**
 * `cost` of communication between services as they ususally communicate with each other using remote calls.
 * `control` of the interfaces of all services. Each service intorduce its own API which upon change effecs all the services depending on this specific service.
 * `global testing` may become a challenge, especialy in situation when multiple services depend on each other.

## Microservices
Here you can find list of available services.
  * `user-service`: http://localhost:8081/
  * `pharmacy-service`: http://localhost:8082/
  * `medicine-service`: http://localhost:8083/
  * `order-service`: http://localhost:8084/
  * `record-service` : http://localhost:8085/
  * `auth-service`: http://localhost:8888/

## Documentation
Each of the services offers dynamically generated OpenAPI specifications, which may be found at:
  * `/q/swagger-ui` - a human-readable web UI
  * `/q/openapi` - an automatically generated specification file in YAML format 

### User Service
This service manages individual users and stores them in a relational database. It also allows authentication of users (admin/user) for using secured endpoints across whole project.
User Service contains following endpoints:
  * `GET /auth/login` - Endpoint ensures authentication of individual users and allows access to all secured endpoints of all services accordingly. For doing so it uses so called token authentication (bearer authentication). As a input it consumes JSON object consisting of `email` and `password` properties. After successful authentication of received credentials it outputs a time-limited `JWT token` that holds ID of the authenticated person and the role (Admin/User), which the person represents in the system.
  * `POST /auth/register` - Endpoint for registration of users. As an input it consumes JSON object consisting of `email`, `password`, `firstName`, `lastName` and desired `role`. After successful verification that the supplied email has not been used yet, the record is inserted into db table `person` and acknowledge message is returned.
  * `GET /users/{id}` - Lists requested user by id. Can be called by any authenticated user, but only `Admin` has rights to receive information about any other user. Basic `User` can only show their personal information.
  * `GET /users?id={}` - Similarly to previous endpoint it serves to list information about user, but instead of using path parameter it utilizes query parameter `id` which  holds comma separated list of ids. This endpoit is accessible only to the `Admin`.
  * `PUT /users/{id}` - Endpoint for updating of user specified by `id`. As a input it consumes JSON object consisting of `email`, `password`, `firstName`, `lastName` and `role`. 
  * `DELETE /users/{id}` - Endpoint for deleting of user specified by `id`. 
### Pharmacy Service
Pharmacy service take care of management of individual pharmacies stored in our system. It allows listing and CRUD operations over Pharmacy table.
In order to access endpoints user must be authenticated and bearer must be part of header. Pharmacy service contains following endpoints:
  * `GET /pharmacies` - Endpoint lists every pharmacy stored in our database.
  * `POST /pharmacies` - Endpoint for creating new pharmacy. It consumes JSON object as an input consisting of `name`, `street`, `streetNumber` and `city` field. If successful then it returns newly created object.
  * `GET /pharmacies/{id}` - Endpoint retrieves pharmacy with id specified by path parameter. If pharmacy with this id does not exist then error code 404 is returned.
  * `PUT /pharmacies/{id}` - Endpoint updates pharmacy with id specified by path parameter. It consumes JSON object as an input consisting of `name`, `street`, `streetNumber` and `city` field.
  * `DELETE /pharmacies/{id}` - Endpoint for deleting pharmacy with id specified by path parameter. If pharmacy with this id does not exist then error code 404 is returned.
 
### Medicine Service
Medicine service take care of management of individual medicines stored in our system. It allows listing and CRUD operations over Medicine table.
In order to access endpoints, user must be authenticated and bearer must be part of header. Medicine service contains following endpoints:
* `GET /medicine/{id}` - Endpoint retrieves medicine with id specified by path parameter. If medicine with this id does not exist then error code 400 is returned.
* `GET /medicine/name/{name}` - Endpoint retrieves medicine with name specified by path parameter. If medicine with this name does not exist then error code 400 is returned.
* `POST /medicine` - Endpoint for creating new medicine. It consumes JSON object as an input consisting of `name`, `manufacturer`, `form`, `quantirty` and `expirationDate` field. If successful then it returns newly created object.
* `PUT /medicine/{id}` - Endpoint updates medicine with id specified by path parameter. It consumes JSON object as an input consisting of `name`, `manufacturer`, `form`, `quantirty` and `expirationDate` field.
* `DELETE /medicine/{id}` - Endpoint for deleting medicine with id specified by path parameter. If medicine with this id does not exist then error code 400 is returned.

### Order Service
Order service take care of management of individual orders placed in our system. It allows listing all orders, creating new or updating status of existing order.
In order to access endpoints, user must be authenticated and bearer must be part of header. Order service contains following endpoints:
* `GET /orders` - Endpoint list all orders stored in our database.
* `GET /orders/{id}` - Endpoint retrieves order with id specified by path parameter. If order with this id does not exist then error code 404 is returned.
* `POST /orders/new` - Endpoint for creating new order. It consumes JSON object as an input consisting of `userId` and list of items. Items have `medicationId` and `amount` fields.
* `PUT /orders/{id}` - Endpoint updates status of the order with id specified by path parameter. If order with such id does not exist then error code 404 is returned.
### Record Service
Record service take care of management of individual records placed in our system. Record is holding information about where is medicine stored and how much of it is in stock. Record service allows listing available medicine in pharmacy, listing pharmacies with requested medicine, getting current state of stock for medicine, ordering and resupplying medicine and crating and deleting new record.
In order to access endpoints, user must be authenticated and bearer must be part of header. Record service provides following endpoints:
* `GET /records/phramacy/{id}` - Endpoint lists all medicines available in pharmacy with id specified by path parameter.
* `GET /records/medicine/{id}` - Endpoint lists all pharmacies where requested medicine with id specified by path parameter is available.
* `GET /records/pharmacy/{pharmacyId}/medicine/{medicineId}` - Endpoint retrieves record of medicine with id specified by path parameter `medicineId` from pharmacy with id specified by path parameter `pharmacyId`.
* `PUT /records/order` - Endpoint updates the record in database by lowering stock amount on record. It consumes JSON object with `medicineId`, `pharmacyId` and `amount` field. If record with `medicineId` and `pharmacyId` is not available error code 404 is returned. If requested amount is negative number error 400 is returned. If current stock is not sufficient error code 404 is returned.
* `PUT /records/resupply` - Endpoint updates the record in database by increasing stock amount on record. It consumes JSON object with `medicineId`, `pharmacyId` and `amount` field. If record with `medicineId` and `pharmacyId` is not available error code 404 is returned. In case resupplied amount is negative number error 400 is returned.
* `POST /records` - Endpoint for creating new record in database. It consumes JSON object with `medicineId`, `pharmacyId` and `amount` field. If record with `medicineId` and `pharmacyId` already exists or amount in record is negative number error code 400 is returned.
* `DELETE /records/pharmacy/{pharmacyId}/medicine/{medicineId}` Endpoint deletes record of medicine with id specified by path parameter `medicineId` from pharmacy with id specified by path parameter `pharmacyId`. If record with `medicineId` and `pharmacyId` does not exist then error code 404 is returned.

## Deployment
1. run script shell script setup.sh which will run `mvn clean install` over all services
2. run command docker-compose up 

## Dependencies
 * JDK 11
 * Maven 3.8.1+
 * [Docker](https://www.docker.com/)
 * [Postman](https://www.postman.com/product/what-is-postman/), [psql](https://www.postgresql.org/docs/9.2/app-psql.html) (for testing)

## Authors
 * Miroslav Lučkai <469288@mail.muni.cz>
 * Ondřej Machala <469122@mail.muni.cz>
 * Matej Turek <469107@mail.muni.cz>
 
