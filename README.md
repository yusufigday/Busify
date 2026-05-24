# 🚌 Busify - Bus Ticketing & Route Management API

Busify is a robust backend REST API built with Java and Spring Boot for managing bus operations, ticketing, routes, and transport companies. It is designed with clean architecture, strict data validation, advanced security mechanisms, and modern Java features to ensure data integrity and high performance.

## 🚀 Features

* **Docker Containerization:** Fully dockerized environment utilizing `docker-compose` for zero-configuration deployments, seamlessly orchestrating the application and PostgreSQL database.
* **Interactive API Documentation:** Integrated Swagger (SpringDoc OpenAPI 3) for real-time, visual endpoint testing and API exploration without needing external clients.
* **Advanced Security & JWT:** Fully integrated Spring Security with stateless JSON Web Token (JWT) authentication for secure API communication.
* **Role-Based Access Control (RBAC):** Method-level security (`@PreAuthorize`) distributing precise access rights across `ADMIN`, `STAFF`, and `USER` roles.
* **IDOR Vulnerability Protection:** Custom Spring Expression Language (SpEL) and service-level guards to prevent Insecure Direct Object Reference attacks, ensuring strict data privacy for user tickets and profiles.
* **Automated Data Seeding:** Built-in `CommandLineRunner` implementation to automatically bootstrap and secure root admin accounts upon initial application startup.
* **Modern DTO Mapping:** Utilizes immutable **Java Records** and **MapStruct** for clean, fast, and compile-time safe object mapping.
* **Robust CRUD Operations:** Comprehensive endpoints for managing Users, Companies, Buses, Routes, and Tickets.
* **Relational Data Protection:** Strict database constraints and custom validation guards prevent orphaned records (e.g., preventing the deletion of a company with active buses).
* **Custom Exception Handling:** Centralized `@ControllerAdvice` for providing clean, readable, and standardized HTTP error responses.

## 🛠️ Technologies & Tools

* **Java 21 (LTS)**
* **Spring Boot 3.x**
* **Spring Security & JWT (io.jsonwebtoken)**
* **Spring Data JPA / Hibernate**
* **PostgreSQL**
* **Docker & Docker Compose**
* **Swagger (SpringDoc OpenAPI 3)**
* **MapStruct & Lombok**
* **Jakarta Bean Validation**
* **Maven**

## 🐳 Getting Started (Run with Docker)

The easiest way to run Busify is by using Docker. You do not need to install Java or PostgreSQL on your local machine.

1. Ensure **Docker Desktop** is running.
2. Open your terminal in the project root directory.
3. Run the following command to build and start the containers:
   ```bash
   docker-compose up -d --build
   ```
4. Once the containers are running, access the interactive API documentation (Swagger UI) at:
   👉 **`http://localhost:8080/swagger-ui/index.html`**

**Useful Docker Commands:**
* View application logs: `docker logs -f busify-app`
* Stop the application: `docker-compose stop`
* Shut down and remove containers: `docker-compose down`

## 🗺️ API Endpoints Overview

* **API Documentation (`/swagger-ui/index.html`):** Interactive UI for all REST endpoints.
* **Auth (`/api/auth`):** Public endpoints for user registration and JWT generation.
* **Admin (`/api/admin`):** Strictly secured endpoints for executive operations and role management (`ADMIN` only).
* **Companies (`/api/companies`):** Bus company management (`ADMIN` restricted).
* **Buses (`/api/buses`):** Fleet capacity and plate management (Read-only for public, restricted for modifications).
* **Routes (`/api/routes`):** Advanced route scheduling and searching.
* **Tickets (`/api/tickets`):** Ticketing system with seat availability, capacity checks, and strict privacy ownership controls.

---
## 👨‍💻 Developer
**Yusuf Gün** - *Java Backend Developer*
