# Client Management API

A Spring Boot application that demonstrates customer management using REST APIs, PostgreSQL, Docker, Flyway, JPA, JDBC, automated testing, and CI/CD with GitHub Actions.

## Overview

This project implements a customer management API with CRUD operations, database migrations, PostgreSQL persistence, Docker-based deployment, automated unit and integration testing, and a CI/CD pipeline that builds and pushes Docker images to Docker Hub.

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

## Features

* Create customers
* Retrieve all customers
* Find customer by ID
* Update customer information
* Delete customers
* PostgreSQL database persistence
* Flyway database migrations
* JPA and JDBC data access support
* Dockerized PostgreSQL database
* Dockerized Spring Boot API
* Unit and integration testing
* Testcontainers support for integration tests
* CI pipeline for pull requests
* CD pipeline for main branch deployments
* Docker image build and push to Docker Hub
* Slack deployment notifications

## API Endpoints

| Method | Endpoint                 | Description                  |
| ------ | ------------------------ | ---------------------------- |
| GET    | `/api/v1/customers`      | Returns all customers        |
| GET    | `/api/v1/customers/{id}` | Returns a customer by ID     |
| POST   | `/api/v1/customers`      | Creates a new customer       |
| PUT    | `/api/v1/customers/{id}` | Updates an existing customer |
| DELETE | `/api/v1/customers/{id}` | Deletes a customer           |

## Database Configuration

The application uses PostgreSQL.

Default Docker Compose configuration:

```yml
services:
  customerdb:
    container_name: client-mgmt-postgres
    image: postgres:15.3
    environment:
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
      PGDATA: /data/postgres
    ports:
      - "5332:5432"

  client-mgmt-api:
    container_name: client-mgmt-api
    image: mbraga01/client-management-api
    environment:
      SPRING_DATASOURCE_URL: "jdbc:postgresql://customerdb:5432/${POSTGRES_DB}"
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
* Slack deployment notifications

Docker image:

```text
mbraga01/client-management-api
```

## Testing

The project uses Maven Surefire and Failsafe to separate unit tests and integration tests.

Unit tests are handled by Surefire.

Integration tests are handled by Failsafe and use a reserved random server port.

Testcontainers is included to support PostgreSQL-based integration testing.

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
backend/src/main/java/com/havefunwith
├── customer
├── exception
├── config
├── dto
├── repository
├── service
└── ClientManagementApiApplication.java
```

## Notes

This project was built as a personal learning and portfolio project to reinforce and expand software engineering skills gained through professional experience and independent study. The goal was to apply industry-standard practices and technologies—including Spring Boot, REST APIs, PostgreSQL, Flyway, Docker, automated testing, CI/CD, and cloud-native deployment workflows—in a self-managed environment. All code, architecture, and implementation were developed independently for educational purposes and do not contain proprietary code, confidential information, or intellectual property from any current or former employer.

## Author

Marlon Braga
