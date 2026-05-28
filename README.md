# 📚 BookApp — Microservices E-Commerce Platform

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025.1.1-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-Storage-FF9900?style=flat&logo=amazons3&logoColor=white)
![Eureka](https://img.shields.io/badge/Netflix_Eureka-Service_Registry-E50914?style=flat&logo=netflix&logoColor=white)
![Stripe](https://img.shields.io/badge/Stripe-Payments-635BFF?style=flat&logo=stripe&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat&logo=docker&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-GitHub-181717?style=flat&logo=github&logoColor=white)
![Feign](https://img.shields.io/badge/OpenFeign-Inter_Service-00BFFF?style=flat)
![WebFlux](https://img.shields.io/badge/Spring_WebFlux-Reactive-6DB33F?style=flat&logo=spring&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OAS_3.1-85EA2D?style=flat&logo=swagger&logoColor=black)

> A fully distributed, cloud-ready e-commerce platform for buying books — built with a **microservices architecture** using Spring Boot, Spring Cloud, and Netflix Eureka.

---

## 🗂️ Repository Structure

This monorepo contains all services as individual branches:

| Branch | Service | Port | Description |
|---|---|---|---|
| `main` | — | — | Project overview & documentation |
| `api-gateway` | API Gateway | `8080` | Central entry point, routing, CORS |
| `auth-service` | Auth Service | `8081` | Identity, JWT, OAuth2, OTP |
| `catalog-service` | Catalog Service | `8082` | Books, inventory, AWS S3 |
| `cart-service` | Cart Service | `8083` | Shopping cart management |
| `order-service` | Order Service | `8084` | Order lifecycle & tracking |
| `payment-service` | Payment Service | `8085` | Stripe payment processing |
| `service-registry` | Service Registry | `9000` | Netflix Eureka Server |

---
## 📖 API Documentation (Swagger UI)

All services expose interactive API docs via Swagger UI, accessible through the API Gateway:

| Service | Swagger UI | OpenAPI Spec |
|---|---|---|
| Auth Service | `http://98.93.144.68:8080/auth-service/swagger-ui/index.html` | `/auth-service/v3/api-docs` |
| Catalog Service | `http://98.93.144.68:8080/catalog-service/swagger-ui/index.html` | `/catalog-service/v3/api-docs` |
| Cart Service | `http://98.93.144.68:8080/cart-service/swagger-ui/index.html` | `/cart-service/v3/api-docs` |
| Order Service | `http://98.93.144.68:8080/order-service/swagger-ui/index.html` | `/order-service/v3/api-docs` |
| Payment Service | `http://98.93.144.68:8080/payment-service/swagger-ui/index.html` | `/payment-service/v3/api-docs` |

> All Swagger UIs require a valid JWT Bearer token for protected endpoints. Use `/auth-service/auth/login` to obtain one, then click **Authorize** in the Swagger UI.

## 🏗️ System Architecture

```
                        ┌──────────────────────────┐
                        │  Client (Web / Mobile)   │
                        └────────────┬─────────────┘
                                     │
                        ┌────────────▼─────────────┐
                        │      API Gateway          │
                        │   Spring Cloud WebFlux    │
                        │        :8080              │
                        └────┬──────┬──────┬────────┘
                             │      │      │
          ┌──────────────────┘      │      └──────────────────┐
          │                         │                         │
┌─────────▼────────┐   ┌────────────▼──────────┐  ┌──────────▼────────┐
│  Auth Service    │   │   Catalog Service      │  │   Cart Service    │
│     :8081        │   │       :8082            │  │      :8083        │
│  JWT • OAuth2    │   │  Books • Inventory     │  │   Cart • Items    │
│  OTP • BCrypt    │   │     AWS S3             │  │   Feign → Catalog │
└──────────────────┘   └───────────────────────┘  └───────────────────┘
                                     │
          ┌──────────────────────────┘
          │
┌─────────▼────────────┐       ┌───────────────────────┐
│   Order Service      │◄─────►│   Payment Service     │
│       :8084          │       │        :8085           │
│  Checkout • Cancel   │       │  Stripe • Webhooks    │
│  Feign → Catalog     │       │  Feign → Order        │
│  Feign → Cart        │       └───────────────────────┘
│  Feign → Payment     │
└──────────────────────┘

        ┌─────────────────────────────┐
        │     Service Registry        │
        │     Netflix Eureka          │
        │          :9000              │
        │  (All services register)    │
        └─────────────────────────────┘
```

---

## 🚀 Services Overview

### 1. 🔀 API Gateway (`api-gateway` branch)
> Central entry point for all client traffic. Built with **Spring Cloud Gateway (WebFlux)**.

**Key Responsibilities:**
- Routes requests to microservices using path-based predicates (`/auth-service/**`, `/catalog-service/**`, etc.)
- Dynamic load balancing via `lb://` URI scheme backed by Eureka
- Global request logging via custom `TestFilter` (`GlobalFilter`)
- CORS configuration allowing all origins, methods, and headers

**Tech:** Spring Boot 4.0.6 · Spring Cloud Gateway · WebFlux · Eureka Client · Java 21

---

### 2. 🔐 Auth Service (`auth-service` branch)
> Identity and access management. Handles registration, login, JWT issuance, and address management.

**Key Responsibilities:**
- User registration with **OTP-based email verification** (6-digit OTP, 5-min expiry via Gmail SMTP)
- Local login returning a signed **JWT** containing `userId` + `role`
- **OAuth2** social login support (GitHub, Google, Meta, LinkedIn)
- Full **address management** (CRUD) with default address support
- Admin user management with role-based access control (`USER`, `ADMIN`)
- Password management with BCrypt encoding

**Endpoints:** `/auth/login` · `/auth/register` · `/auth/verify-otp` · `/user/me` · `/users/address/**`

**Tech:** Spring Security · JWT · BCrypt · OAuth2 · Gmail SMTP · MySQL

---

### 3. 📖 Catalog Service (`catalog-service` branch)
> Central repository for book metadata, inventory, and cover images.

**Key Responsibilities:**
- Public search/filter of books by title, author, and genre
- Admin CRUD for books with multipart cover image upload (max 50MB)
- **AWS S3** integration (`booknest-catalog`, `us-east-1`) for cover image lifecycle
- Inventory management: stock tracking, `AVAILABLE` / `OUT_OF_STOCK` status
- Internal endpoints for stock check, reduce, and restore (used by Order Service)

**Endpoints:** `/api/books/**` · `/admin/books/**` · `/api/inventory/**`

**Tech:** AWS S3 · MySQL (`CATALOG_DB`) · Spring Boot · Eureka Client

---

### 4. 🛒 Cart Service (`cart-service` branch)
> Manages user shopping carts with real-time book detail snapshots.

**Key Responsibilities:**
- Add, remove, update quantity of items; clear cart
- Calls **Catalog Service** via Feign to fetch current book title & price on add
- Stores price snapshot at time of add (not live price)
- JWT propagation to downstream Feign requests via `RequestInterceptor`

**Entities:** `Cart` (user_id, total_price) · `CartItem` (book_id, title, price, quantity)

**Tech:** Spring Boot · Feign Client · MySQL (`ecommerce`) · JWT Auth

---

### 5. 📦 Order Service (`order-service` branch)
> Manages the complete order lifecycle from checkout to delivery.

**Key Responsibilities:**
- Cart checkout and "Buy Now" instant purchase flows
- Inventory validation and reservation via Catalog Service Feign calls
- Stripe payment intent creation via Payment Service
- Order status lifecycle: `PENDING_PAYMENT` → `CONFIRMED` → `PROCESSING` → `SHIPPED` → `DELIVERED` → `CANCELLED`
- Auto-confirms order when Payment Service sends `SUCCESS` callback
- Stock restoration on cancellation

**Endpoints:** `/orders/checkout` · `/orders/buy-now` · `/orders/my` · `/admin/orders/**` · `/api/internal/orders/**`

**Tech:** Feign (Catalog, Cart, Payment) · JWT · MySQL · Spring Boot

---

### 6. 💳 Payment Service (`payment-service` branch)
> Handles payment processing via Stripe with async webhook support.

**Key Responsibilities:**
- Creates **Stripe Payment Intents** and returns `clientSecret` to frontend
- Processes async Stripe webhooks (`payment_intent.succeeded`, `payment_intent.payment_failed`)
- Notifies Order Service of payment outcome via internal Feign callback
- Cancel payment intents and track all transactions
- Statuses: `INITIATED` → `SUCCESS` / `FAILED` / `CANCELED`

**Endpoints:** `POST /api/payments/create-intent` · `POST /api/payments/webhook` · `GET /api/payments/order/{orderId}` · `POST /api/payments/cancel/{paymentIntentId}`

**Tech:** Stripe Java SDK · Feign (Order Service) · MySQL (`ecommerce`) · Spring Boot

---

### 7. 🗂️ Service Registry (`service-registry` branch)
> Netflix Eureka Server — the "phonebook" of the microservices ecosystem.

**Key Responsibilities:**
- Accepts self-registration from all microservices on startup
- Monitors service health via heartbeats (30s interval, 90s eviction timeout)
- Provides service location to API Gateway and Feign clients for dynamic routing
- Self-preservation mode prevents mass eviction during network partitions

**Tech:** Spring Cloud Netflix Eureka Server · Java 21 · Docker (`eclipse-temurin:21-jre`)

**Docker resource config:** `-Xms128m` / `-Xmx256m`

---

## 🔗 Inter-Service Communication Map

```
API Gateway    ──routes──►  All Services (via Eureka lb://)
Cart Service   ──Feign──►   Catalog Service  (GET /api/books/{id})
Order Service  ──Feign──►   Catalog Service  (GET /api/books, inventory check/reduce/restore)
Order Service  ──Feign──►   Cart Service     (GET /cart/{cartId})
Order Service  ──Feign──►   Payment Service  (POST /api/payments/create-intent)
Payment Svc    ──Feign──►   Order Service    (PUT /api/internal/orders/{id}/payment-status)
```

---

## 🛡️ Security Model

- All public APIs protected by **JWT Bearer tokens**
- JWT carries `userId` and `role` (`USER` / `ADMIN`) as claims
- Each service runs its own `JwtFilter` to validate and populate `SecurityContext`
- Internal service-to-service paths (`/api/internal/**`) bypass JWT for Feign calls
- Feign clients propagate JWT downstream via `RequestInterceptor` on `Authorization` header
- Passwords hashed with **BCrypt**
- OAuth2 social login handled by `OAuth2SuccessHandler` (issues JWT post-authentication)

---

## 🗄️ Database Overview

| Service | Database | Key Tables |
|---|---|---|
| Auth Service | MySQL | `users`, `addresses` |
| Catalog Service | MySQL (`CATALOG_DB`) | `books`, `inventory` |
| Cart Service | MySQL (`ecommerce`) | `cart`, `cart_item` |
| Order Service | MySQL | `orders`, `order_items` |
| Payment Service | MySQL (`ecommerce`) | `payments` |
| Service Registry | In-Memory | — |

---

## ☁️ External Integrations

| Integration | Used By | Purpose |
|---|---|---|
| **AWS S3** (`booknest-catalog`, `us-east-1`) | Catalog Service | Book cover image upload, serve & delete |
| **Stripe** | Payment Service | Payment Intent creation, webhook processing, cancellation |
| **Gmail SMTP** (`smtp.gmail.com:587`) | Auth Service | OTP delivery during registration |
| **GitHub OAuth2** | Auth Service | Social login |

---

## 🐳 Running Locally with Docker

All services share the internal Docker network `micro-net`.

```bash
# 1. Start Service Registry first
docker run --network micro-net --name service-registry -p 9000:9000 bookapp/service-registry

# 2. Start remaining services (set EUREKA_URL env var)
docker run --network micro-net -e EUREKA_URL=http://service-registry:9000/eureka -p 8080:8080 bookapp/api-gateway
docker run --network micro-net -e EUREKA_URL=http://service-registry:9000/eureka -p 8081:8081 bookapp/auth-service
docker run --network micro-net -e EUREKA_URL=http://service-registry:9000/eureka -p 8082:8082 bookapp/catalog-service
docker run --network micro-net -e EUREKA_URL=http://service-registry:9000/eureka -p 8083:8083 bookapp/cart-service
docker run --network micro-net -e EUREKA_URL=http://service-registry:9000/eureka -p 8084:8084 bookapp/order-service
docker run --network micro-net -e EUREKA_URL=http://service-registry:9000/eureka -p 8085:8085 bookapp/payment-service
```

> **Note:** Each service requires its own `application.yaml` environment variables (DB credentials, Stripe keys, AWS credentials, JWT secret, Gmail credentials).

---

## 📋 Environment Variables Reference

| Variable | Used By | Description |
|---|---|---|
| `EUREKA_URL` | All services | Eureka Server URL |
| `JWT_SECRET` | Auth, Catalog, Cart, Order, Payment | Shared JWT signing secret |
| `stripe.secret-key` | Payment Service | Stripe secret API key |
| `stripe.webhook-secret` | Payment Service | Stripe webhook signing secret |
| `AWS_ACCESS_KEY` | Catalog Service | AWS S3 access key |
| `AWS_SECRET_KEY` | Catalog Service | AWS S3 secret key |
| `MAIL_USERNAME` | Auth Service | Gmail account for OTP |
| `MAIL_PASSWORD` | Auth Service | Gmail app password |
| `DB_URL` / `DB_USER` / `DB_PASS` | All DB services | MySQL connection details |

---

## 📌 Service Ports Quick Reference

| Port | Service |
|---|---|
| `8080` | API Gateway |
| `8081` | Auth Service |
| `8082` | Catalog Service |
| `8083` | Cart Service |
| `8084` | Order Service |
| `8085` | Payment Service |
| `9000` | Service Registry (Eureka Dashboard) |

---

## 📄 License

This project is licensed under the MIT License.