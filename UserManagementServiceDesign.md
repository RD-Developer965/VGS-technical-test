# User Management Service Design

## High-Level Feature Overview

### User Concept
- Introduce a new `User` entity to represent players in the system.
- Each user will have a unique identifier, username, and password (hashed).
- Users can create and join Tic Tac Toe games.
- Track which users participated in each game.

### User Management Features
- User registration (sign up).
- User authentication (login).
- Retrieve user profile and statistics (games played, won, lost). This could be useful for a future dashboard screen.
- Associate games with users.

---

## API Changes and New Endpoints

### Existing Endpoints (to be updated)
- `/api/games/create`:  
  - Now requires authentication; associates the created game with the requesting user.
  - When creating a game, the payload will need an attribute to indicate if the player that
  - is creating the game will play as X or as O.
- `/api/games/move`:  
  - Requires authentication; associates the move with the authenticated user.

### New Endpoints

#### User Registration & Authentication
- `POST /api/users/register`  
  - Request: `{ "username": "string", "password": "string" }`  
  - Response: `201 Created` with user details or error if username exists.

- `POST /api/users/login`  
  - Request: `{ "username": "string", "password": "string" }`  
  - Response: `200 OK` with authentication token or error.

#### User Profile & Management
- `GET /api/users/me`  
  - Requires authentication.
  - Response: User profile and statistics.

- `GET /api/users/{userId}/games`  
  - List games created or played by the user.

#### Game Participation
- `POST /api/games/{gameId}/join`  
  - Allows a user to join an existing game. If the game has X player then the new user will
  - play as O and vice versa

---

## Database Structure Changes

### New Tables

#### `users`
| Column      | Type         | Constraints           |
|-------------|--------------|----------------------|
| id          | BIGSERIAL    | PRIMARY KEY          |
| username    | VARCHAR(50)  | UNIQUE, NOT NULL     |
| password    | VARCHAR(255) | NOT NULL (hashed)    |
| created_at  | TIMESTAMP    | NOT NULL             |

#### `games` (updated)
- Add `created_by` (FK to `users.id`)
- Add `x_player` (FK to `users.id`)
- Add `o_player` (FK to `users.id`)

---

## Architectural Design Changes

### Authentication
- Add authentication middleware (e.g., JWT or session-based).
- Secure endpoints that require user context.

### Service Layer
- Introduce a `UserService` for user management (registration, authentication, profile).
- Update `GameService` and related logic to handle user associations.

### Infrastructure
- Update database schema and migrations.
- Ensure password hashing and secure storage.
- Consider rate limiting and basic security best practices.

### API Documentation
- Update OpenAPI/Swagger documentation to reflect new endpoints and authentication requirements.

---

## Summary

This evolution introduces user management, authentication, and user-game associations. The API and database are extended to support user registration, login, and game participation tracking. Architectural changes focus on security and user context throughout the service.
