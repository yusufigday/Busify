# 🚌 Busify - Bus Ticketing & Route Management API

Busify is a robust backend REST API built with Java and Spring Boot for managing bus operations, ticketing, routes, and transport companies. It is designed with clean architecture, strict data validation, advanced security mechanisms, and modern Java features to ensure data integrity and high performance.

## 🚀 Features

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
* **MapStruct & Lombok**
* **Jakarta Bean Validation**
* **Maven**

## 🗺️ API Endpoints Overview

* **Auth (`/api/auth`):** Public endpoints for user registration and JWT generation.
* **Admin (`/api/admin`):** Strictly secured endpoints for executive operations and role management (`ADMIN` only).
* **Companies (`/api/companies`):** Bus company management (`ADMIN` restricted).
* **Buses (`/api/buses`):** Fleet capacity and plate management (Read-only for public, restricted for modifications).
* **Routes (`/api/routes`):** Advanced route scheduling and searching.
* **Tickets (`/api/tickets`):** Ticketing system with seat availability, capacity checks, and strict privacy ownership controls.

## 🔒 Roadmap (Upcoming Features)

* [ ] Swagger (OpenAPI) Integration for interactive API documentation.
* [ ] Pagination and Filtering for Route and Bus listings.
* [ ] Unit and Integration Testing with JUnit 5 and Mockito.

---
## 👨‍💻 Developer
**Yusuf Gün** - *Java Backend Developer*
