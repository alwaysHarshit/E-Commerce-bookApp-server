# API Gateway Technical Report

## 1. Overview
The **API Gateway** serves as the central entry point for the BookApp ecosystem. Built with **Spring Cloud Gateway (WebFlux)**, it manages request routing, cross-origin resource sharing (CORS), and provides a unified interface for client applications to interact with various microservices. It integrates with **Eureka Service Registry** for dynamic service discovery and load balancing.

---

## 2. Routing Configuration

The gateway routes incoming requests to the appropriate microservices using the `Path` predicate and load balancer (`lb://`) URI.

| Service ID | Route Predicate | Target URI | Description |
| :--- | :--- | :--- | :--- |
| `auth-service` | `/auth-service/**` | `lb://AUTH-SERVICE` | Forwards authentication and authorization requests. |
| `catalog-service` | `/catalog-service/**` | `lb://CATALOG-SERVICE` | Forwards requests related to book listings and inventory. |
| `cart-service` | `/cart-service/**` | `lb://CART-SERVICE` | Forwards shopping cart management requests. |
| `order-service` | `/order-service/**` | `lb://ORDER-SERVICE` | Forwards order processing and tracking requests. |
| `payment-service` | `/payment-service/**` | `lb://PAYMENT-SERVICE` | Forwards payment transaction and status requests. |

---

## 3. Global Filters & Controllers

### 3.1 Global Filter (`TestFilter`)
A custom implementation of `GlobalFilter` is registered as a Spring `@Component`.
- **Purpose**: Logs every request URI hitting the gateway to the console for debugging and monitoring.
- **Output Example**: `🔥 GATEWAY HIT: http://localhost:8080/catalog-service/api/books`

### 3.2 Gateway Controller
A standard `@RestController` is defined to provide gateway-specific endpoints.
- **Endpoint**: `/test`
- **Method**: `GET`
- **Description**: Returns a simple "Hello World" message to verify the gateway is operational and responding.

---

## 4. Cross-Origin Resource Sharing (CORS)

The `CorsConfig` class defines a `CorsWebFilter` to handle cross-origin requests from front-end applications.

| Configuration | Setting |
| :--- | :--- |
| `AllowedOrigins` | `*` (All origins permitted) |
| `AllowedMethods` | `*` (All HTTP methods permitted) |
| `AllowedHeaders` | `*` (All headers permitted) |
| `AllowCredentials`| `false` |

---

## 5. Service Interactions

### 5.1 Service Discovery (Eureka)
- **Eureka Client**: The gateway is registered as an Eureka client (`@EnableDiscoveryClient`).
- **Dynamic Routing**: Uses `lb://` syntax to resolve service instances registered in the Service Registry.
- **Eureka URL**: Configurable via `${EUREKA_URL}` environment variable.

---

## 6. Configurations

### 6.1 Application Configuration
- **App Name**: `api-gateway`
- **Port**: `8080`
- **Framework**: Spring Boot 4.0.6 (Spring Cloud 2025.1.1)
- **Runtime**: Java 21

### 6.2 Key Dependencies
- `spring-cloud-starter-gateway-server-webflux`: Core gateway functionality.
- `spring-cloud-starter-netflix-eureka-client`: Integration with Eureka.
- `spring-boot-starter-webflux`: Reactive web support.

---

## 7. Business Logic & Transitions
The API Gateway primarily handles infrastructure concerns:
1. **Dynamic Load Balancing**: Requests are distributed across available instances of target services.
2. **Path Stripping/Preservation**: Currently, paths are forwarded as-is to the target services (e.g., `/catalog-service/api/books` remains unchanged).
3. **Request Interception**: The `TestFilter` demonstrates the ability to intercept and process every request passing through the gateway.
