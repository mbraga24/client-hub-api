# Client Hub API

A Spring Boot application that demonstrates customer management using REST APIs, PostgreSQL, Docker, Flyway, JPA, JDBC, automated testing, CI/CD with GitHub Actions, and SonarCloud code quality analysis.

## Overview

This project implements a customer management API with CRUD operations, database migrations, PostgreSQL persistence, Docker-based deployment, automated unit and integration testing, code coverage with JaCoCo, static analysis with SonarCloud, and a CI/CD pipeline that builds and pushes Docker images to Docker Hub.

## Tech Stack

* Java 17
* Spring Boot 3.1.2
* Spring Web
* Spring Data JPA
* Spring Data JDBC
* PostgreSQL
* Flyway
* Docker
* Docker Compose
* Jib
* GitHub Actions
* Testcontainers
* Maven
* Lombok
* JavaFaker
* JaCoCo
* SonarCloud

## Features

* Create customers with username, first name, last name, email, age, and phone number
* Retrieve all customers
* Find customer by ID
* Update customer information (with change detection)
* Delete customers
* PostgreSQL database persistence
* Flyway database migrations
* JPA and JDBC data access support (switchable via qualifier)
* In-memory repository for testing
* Dockerized PostgreSQL database
* Dockerized Spring Boot API
* Unit and integration testing
* Testcontainers support for integration tests
* CI pipeline for pull requests
* CD pipeline for main branch deployments
* Docker image build and push to Docker Hub
* Slack deployment notifications
* JaCoCo code coverage reports
* SonarCloud static code analysis

## API Endpoints

| Method | Endpoint                 | Description                  |
| ------ | ------------------------ | ---------------------------- |
| GET    | `/api/v1/customers`      | Returns all customers        |
| GET    | `/api/v1/customers/{id}` | Returns a customer by ID     |
| POST   | `/api/v1/customers`      | Creates a new customer       |
| PUT    | `/api/v1/customers/{id}` | Updates an existing customer |
| DELETE | `/api/v1/customers/{id}` | Deletes a customer           |

### Request Bodies

**POST /api/v1/customers**

```json
{
  "appUserId": 1,
  "username": "john_doe",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@email.com",
  "age": 30,
  "phoneNumber": "555-0001"
}
```

**PUT /api/v1/customers/{id}**

```json
{
  "username": "john_doe_updated",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john_new@email.com",
  "age": 31,
  "phoneNumber": "555-0002"
}
```

## Customer Model

| Field       | Type    | Constraints              |
| ----------- | ------- | ------------------------ |
| id          | Long    | Auto-generated (sequence)|
| appUserId   | Long    | Not null, unique         |
| username    | String  | Not null, unique         |
| firstName   | String  | Not null                 |
| lastName    | String  | Not null                 |
| age         | Integer | Not null                 |
| email       | String  | Not null, unique         |
| phoneNumber | String  | Not null, unique         |

## Database Configuration

The application uses PostgreSQL.

Default Docker Compose configuration:

```yml
services:
  customer-db:
    container_name: client-hub-postgres
    image: postgres:15.3
    environment:
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
      PGDATA: /data/postgres
    ports:
      - "5332:5432"

  client-hub-api:
    container_name: client-hub-api
    image: mbraga01/client-hub-api
    environment:
      SPRING_DATASOURCE_URL: "jdbc:postgresql://customer-db:5432/${POSTGRES_DB}"
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "8088:8080"
```

When running locally with Docker Compose, the API is available on:

```text
http://localhost:8088
```

PostgreSQL is exposed on:

```text
localhost:5332
```

### Database Migrations

Flyway manages schema migrations located in `backend/src/main/resources/db/migration/`:

| Version | Description                                           |
| ------- | ----------------------------------------------------- |
| V1      | Initial setup (customer table with name, email, age)  |
| V2      | Add unique constraint to customer email               |
| V3      | Split name into first/last name, add app_user_id, username, phone_number with unique constraints |

## CI/CD

This project uses GitHub Actions for continuous integration and deployment.

### CI Pipeline

The CI workflow runs on pull requests to the `main` branch.

It performs:

* Repository checkout
* Java 17 setup
* PostgreSQL service container startup
* Maven build
* Unit tests
* Integration tests
* SonarCloud static code analysis

```bash
mvn -ntp -B verify
```

### CD Pipeline

The CD workflow runs on pushes to the `main` branch or manual dispatch.

It performs:

* Repository checkout
* Java 17 setup
* PostgreSQL service container startup
* Docker Hub login
* Maven build and verification
* Docker image build using Jib
* Docker image push to Docker Hub
* SonarCloud code coverage report
* Slack deployment notifications

Docker image:

```text
mbraga01/client-hub-api
```

## Testing

The project uses Maven Surefire and Failsafe to separate unit tests and integration tests.

Unit tests are handled by Surefire.

Integration tests are handled by Failsafe and use a reserved random server port.

Testcontainers is included to support PostgreSQL-based integration testing.

JaCoCo generates code coverage reports during the verify phase.

Run all tests:

```bash
mvn verify
```

## Docker

The application can be run using Docker Compose.

Start the application and database:

```bash
docker compose up -d
```

Stop the application and database:

```bash
docker compose down
```

The Docker Compose setup includes:

* PostgreSQL 15.3 container
* Spring Boot API container
* Shared Docker bridge network
* Persistent PostgreSQL volume

## Project Structure

```text
backend/src/main/java/com/clienthub
├── customer/
│   ├── Customer.java
│   ├── CustomerController.java
│   ├── CustomerCreateRequest.java
│   ├── CustomerUpdateRequest.java
│   ├── CustomerDataAccess.java
│   ├── CustomerRepository.java
│   ├── CustomerJpaRepository.java
│   ├── CustomerJdbcRepository.java
│   ├── CustomerInMemoryRepository.java
│   ├── CustomerRowMapper.java
│   └── CustomerService.java
├── exception/
│   ├── DuplicatedResourceException.java
│   ├── ResourceNotChangedException.java
│   └── ResourceNotFoundException.java
└── Main.java
```

## Error Handling

| Exception                    | HTTP Status   |
| ---------------------------- | ------------- |
| ResourceNotFoundException    | 404 Not Found |
| DuplicatedResourceException  | 409 Conflict  |
| ResourceNotChangedException  | 304 Not Modified |

## Notes

This project was built as a personal learning and portfolio project to reinforce and expand software engineering skills gained through professional experience and independent study. The goal was to apply industry-standard practices and technologies—including Spring Boot, REST APIs, PostgreSQL, Flyway, Docker, automated testing, CI/CD, and cloud-native deployment workflows—in a self-managed environment. All code, architecture, and implementation were developed independently for educational purposes and do not contain proprietary code, confidential information, or intellectual property from any current or former employer.

## Author

Marlon Braga
