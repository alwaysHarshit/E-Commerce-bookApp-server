# Service Registry Technical Report

## 1. Overview
The **Service Registry** is a central component of the BookApp microservices architecture, acting as a "phonebook" for all services. It uses **Netflix Eureka Server** to allow microservices to register themselves at runtime and discover other services without hardcoding IP addresses or port numbers. This enables dynamic scaling and high availability.

---

## 2. API & Dashboard Endpoints

### 2.1 Eureka Dashboard
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/` | `GET` | The Eureka Web UI dashboard, providing a status overview of all registered service instances. |

### 2.2 Registration Endpoints (Internal)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/eureka/apps` | `GET` | Returns the full registry of all registered applications in XML/JSON format. |
| `/eureka/apps/{appId}`| `GET` | Returns details for a specific service ID. |
| `/eureka/apps/{appId}`| `POST`| Used by client services to register themselves. |
| `/eureka/apps/{appId}/{instanceId}` | `PUT` | Used for heartbeats to indicate a service instance is still alive. |

---

## 3. Data Transfer Objects (DTOs)
The Service Registry does not define custom DTOs as it relies on the internal models provided by **Spring Cloud Netflix Eureka**.

---

## 4. Database Objects (Entities)
The Service Registry is stateless and does not use a persistent database. It maintains the registry in-memory.

---

## 5. Service Interactions

### 5.1 Service Discovery Lifecycle
1.  **Registration:** Upon startup, every microservice (e.g., `CATALOG-SERVICE`, `ORDER-SERVICE`) sends a `POST` request to the registry with its metadata (IP, port, health check URL).
2.  **Heartbeats:** Registered services send periodic heartbeats (every 30 seconds by default) to the registry.
3.  **Eviction:** If the registry does not receive a heartbeat for a specific interval (default 90 seconds), it removes the instance from the registry.
4.  **Discovery:** Client services (or the API Gateway) fetch the registry to resolve service names (e.g., `http://ORDER-SERVICE/`) to actual network locations.

---

## 6. Business Logic & Role
The Service Registry's primary logic is managed by the `@EnableEurekaServer` annotation. Its key responsibilities are:
-   **Service Self-Preservation:** A mode where Eureka stops evicting instances if it detects a network partition or a large number of heartbeats are missing, ensuring the registry remains available.
-   **Peer Replication:** (Optional) Can be configured to sync with other Eureka nodes for high availability.

---

## 7. Configurations

### 7.1 Application Configuration
-   **App Name**: `service-registry`
-   **Port**: `9000`
-   **Eureka Server Settings**:
    -   `fetch-registry`: `false` (The server doesn't need to fetch the registry from itself).
    -   `register-with-eureka`: `false` (The server doesn't need to register with itself).

---

## 8. Deployment Details

### 8.1 Dockerization
-   **Image**: Based on `eclipse-temurin:21-jre`.
-   **Resource Management**: Configured with `-Xms128m` and `-Xmx256m` to minimize footprint while ensuring stability.
-   **Network**: Runs on a shared internal network (`micro-net`) to communicate with other services.
