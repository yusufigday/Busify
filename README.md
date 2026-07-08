# 🚌 Busify - Bus Ticketing & Route Management API

Busify is a robust backend REST API built with Java and Spring Boot for managing bus operations, ticketing, routes, and transport companies. It is designed with clean architecture, strict data validation, advanced security mechanisms, and modern Java features to ensure data integrity and high performance.

## 🚀 Features

* **Docker Containerization:** Fully dockerized environment utilizing `docker-compose` for zero-configuration deployments, seamlessly orchestrating the application, PostgreSQL, Redis, RabbitMQ, and Elasticsearch.
* **Interactive API Documentation:** Integrated Swagger (SpringDoc OpenAPI 3) for real-time, visual endpoint testing and API exploration without needing external clients.
* **Advanced Security & JWT:** Fully integrated Spring Security with stateless JSON Web Token (JWT) authentication for secure API communication.
* **Role-Based Access Control (RBAC):** Method-level security (`@PreAuthorize`) distributing precise access rights across `ADMIN`, `STAFF`, and `USER` roles.
* **IDOR Vulnerability Protection:** Custom Spring Expression Language (SpEL) and service-level guards to prevent Insecure Direct Object Reference attacks, ensuring strict data privacy for user tickets and profiles.
* **Redis Caching:** High-performance caching layer using Spring Cache with Redis backend. Routes, companies, and buses are cached with a configurable TTL to reduce database load.
* **JWT Token Blacklist (Redis):** Stateless logout mechanism — invalidated tokens are stored in Redis with their remaining expiration time, preventing reuse after logout.
* **Event-Driven Architecture (RabbitMQ):** Asynchronous message publishing for user registration and ticket purchase/cancellation events via RabbitMQ.
* **Centralized Logging (Elasticsearch):** Structured application event logs are pushed to Elasticsearch for real-time monitoring and traceability.
* **Automated Data Seeding:** Built-in `CommandLineRunner` implementation to automatically bootstrap and secure root admin accounts upon initial application startup.
* **Modern DTO Mapping:** Utilizes immutable **Java Records** and **MapStruct** for clean, fast, and compile-time safe object mapping.
* **Robust CRUD Operations:** Comprehensive endpoints for managing Users, Companies, Buses, Routes, and Tickets.
* **Relational Data Protection:** Strict database constraints and custom validation guards prevent orphaned records (e.g., preventing the deletion of a company with active buses).
* **Custom Exception Handling:** Centralized `@ControllerAdvice` for providing clean, readable, and standardized HTTP error responses.

## 🛠️ Technologies & Tools

* **Java 21 (LTS)**
* **Spring Boot 3.4.1**
* **Spring Security & JWT (io.jsonwebtoken)**
* **Spring Data JPA / Hibernate**
* **PostgreSQL** — Primary relational database
* **Redis 7.2** — Caching layer & JWT token blacklist
* **RabbitMQ 3.13** — Asynchronous event messaging
* **Elasticsearch 8.15** — Centralized structured logging
* **Docker & Docker Compose**
* **Swagger (SpringDoc OpenAPI 3)**
* **MapStruct & Lombok**
* **Jakarta Bean Validation**
* **Gradle**

## 🐳 Getting Started (Run with Docker)

The easiest way to run Busify is by using Docker. You do not need to install Java or PostgreSQL on your local machine.

1. Ensure **Docker Desktop** is running.
2. Open your terminal in the project root directory.
3. Run the following command to build and start all containers:
   ```bash
   docker-compose up -d --build
   ```
4. Once the containers are running, access the interactive API documentation (Swagger UI) at:
   👉 **`http://localhost:8080/swagger-ui/index.html`**

**Services started by Docker Compose:**

| Service | Port | Description |
|---|---|---|
| `busify-app` | `8080` | Spring Boot application |
| `busify-db` | `5432` | PostgreSQL database |
| `busify-redis` | `6379` | Redis cache & token blacklist |
| `busify-rabbitmq` | `5672` / `15672` | RabbitMQ (Management UI on 15672) |
| `busify-elasticsearch` | `9200` | Elasticsearch |
| `busify-kibana` | `5601` | Kibana dashboard |

**Useful Docker Commands:**
* View application logs: `docker logs -f busify-app`
* Check Redis: `docker exec busify-redis redis-cli ping`
* Stop the application: `docker-compose stop`
* Shut down and remove containers: `docker-compose down`
* Shut down and remove containers + volumes: `docker-compose down -v`

## ⚡ Redis Caching Strategy

| Cache Name | Cached Method | Evicted On |
|---|---|---|
| `routes` | `getAllRoutes()` | create / update / delete route |
| `companies` | `getAllCompanies()` | create / update / delete company |
| `buses` | `getAllBuses()`, `getBus(id)` | create / update / delete bus |
| `blacklist:*` | JWT token blacklist | Automatically expires with token TTL |

Default cache TTL: **5 minutes** (configurable via `spring.cache.redis.time-to-live` in `application.properties`)

## 🗺️ API Endpoints Overview

* **API Documentation (`/swagger-ui/index.html`):** Interactive UI for all REST endpoints.
* **Auth (`/api/auth`):** Public endpoints for user registration, JWT generation, and logout.
* **Admin (`/api/admin`):** Strictly secured endpoints for executive operations and role management (`ADMIN` only).
* **Companies (`/api/companies`):** Bus company management (`ADMIN` restricted).
* **Buses (`/api/buses`):** Fleet capacity and plate management (Read-only for public, restricted for modifications).
* **Routes (`/api/routes`):** Advanced route scheduling and searching.
* **Tickets (`/api/tickets`):** Ticketing system with seat availability, capacity checks, and strict privacy ownership controls.

---
## 👨‍💻 Developer
**Yusuf Gün** - *Java Backend Developer*
