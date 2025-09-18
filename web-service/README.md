# Tic Tac Toe Game Service

This is a RESTful web service for managing Tic Tac Toe games using Spring Boot and PostgreSQL.

## Technologies Used

- Java 21
- Spring Boot 3.5.5
- PostgreSQL 15
- Docker & Docker Compose
- Maven

## Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Maven

## Getting Started

- Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080`

## Project Structure

The project follows Clean Architecture principles with the following layers:

- **Domain**: Contains business entities and repository interfaces
- **Application**: Contains business logic and use cases
- **Infrastructure**: Contains implementations of repositories and other external concerns
- **Presentation**: Contains REST controllers, DTOs, Global Exceptions

## Development

To run the tests:
```bash
./mvnw test
```

To build the application:
```bash
./mvnw clean package
```