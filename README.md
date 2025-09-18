# Tic Tac Toe 🎮

## 👤 Author

Created by **Rafael Díaz Anguita**

A **fullstack Tic Tac Toe project** built with:

- **Backend**: Spring Boot  
- **Frontend**: React  

---

## 📖 Overview

This project is a complete implementation of the classic Tic Tac Toe game.  
It is split into two main parts:

1. **Backend** – powered by Spring Boot, responsible for handling the game logic, persistence, and APIs.  
2. **Frontend** – built with React, providing a modern user interface for playing the game.  

Each part has its own `README.md` file with detailed setup and usage instructions.

---

## 🚀 Getting Started

To get started, check out the individual readme files:

- [Backend README](./web-service/README.md)  
- [Frontend README](./client/README.md)  

- Backend: http://localhost:8080
- Frontend: http://localhost:5173
- Database: PostgreSQL on port 5432

---

## 🐳 Running with Docker Compose

You can start the entire application (frontend, backend, and PostgreSQL database) with Docker Compose:

```bash
docker-compose up --build
```