# 🚌 Busify - Bus Ticketing & Route Management API

Busify is a robust backend REST API built with Java and Spring Boot for managing bus operations, ticketing, routes, and transport companies. It is designed with clean architecture, strict data validation, and modern Java features to ensure data integrity and high performance.

## 🚀 Features

* **Modern DTO Mapping:** Utilizes immutable **Java Records** and **MapStruct** for clean, fast, and compile-time safe object mapping.
* **Robust CRUD Operations:** Comprehensive endpoints for managing Users, Companies, Buses, Routes, and Tickets.
* **Relational Data Protection:** Strict database constraints and custom validation guards prevent orphaned records (e.g., preventing the deletion of a company with active buses or a user with active tickets).
* **Data Standardization:** Built-in data sanitization (trimming and formatting) to maintain database consistency and prevent duplicate entries.
* **Custom Exception Handling:** Centralized `@ControllerAdvice` for providing clean, readable, and standard HTTP error responses.

## 🛠️ Technologies & Tools

* **Java 17+**
* **Spring Boot 3.x**
* **Spring Data JPA / Hibernate**
* **MapStruct & Lombok**
* **Jakarta Bean Validation**
* **Maven**

## 🗺️ API Endpoints Overview

* **Auth & Users:** `/api/users` - Registration and profile management.
* **Companies:** `/api/companies` - Bus company management.
* **Buses:** `/api/buses` - Fleet capacity and plate management.
* **Routes:** `/api/routes` - Advanced route scheduling and searching.
* **Tickets:** `/api/tickets` - Ticketing system with seat availability and capacity checks.

## 🔒 Roadmap (Upcoming Features)

* [ ] Spring Security Integration
* [ ] JWT (JSON Web Token) Authentication & Authorization
* [ ] Role-Based Access Control (Admin vs. User endpoints)

---
## 👨‍💻 Developer
**Yusuf Gün** - *Java Backend Developer*
