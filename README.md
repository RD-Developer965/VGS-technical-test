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

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/VGS-technical-test.git
   cd VGS-technical-test
   ```

2. Start the PostgreSQL database using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Build and run the application:
   ```bash
   cd web-service
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080`

## API Endpoints

### Create a New Game
```http
POST /api/games/create
```

Response:
```json
{
    "id": 1,
    "createdAt": "2025-09-17T10:00:00",
    "status": "IN_PROGRESS"
}
```

## Project Structure

The project follows Clean Architecture principles with the following layers:

- **Domain**: Contains business entities and repository interfaces
- **Application**: Contains business logic and use cases
- **Infrastructure**: Contains implementations of repositories and other external concerns
- **Presentation**: Contains REST controllers and DTOs

## Development

To run the tests:
```bash
./mvnw test
```

To build the application:
```bash
./mvnw clean package
```