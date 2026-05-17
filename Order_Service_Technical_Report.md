# Order Service Technical Report

## 1. Overview
The **Order Service** manages the entire lifecycle of an order within the BookApp ecosystem. It handles order creation (Cart Checkout and "Buy Now"), order tracking for users, and administrative management. It coordinates with the **Catalog Service** for inventory validation, the **Cart Service** for item retrieval, and the **Payment Service** for transaction processing.

---

## 2. API Endpoints

### 2.1 Public User Endpoints (`/orders`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/checkout/` | `POST` | Creates an order from a user's cart. |
| `/checkout/buy-now` | `POST` | Creates an order for a single book instantly. |
| `/my` | `GET` | Retrieves all orders for the currently authenticated user. |
| `/{orderId}` | `GET` | Fetches details of a specific order (Owner only). |
| `/{orderId}/cancel` | `PUT` | Cancels an order (Allowed only if not shipped/delivered). |
| `/has-purchased` | `GET` | Checks if a user has a delivered order for a specific book. |

### 2.2 Admin Endpoints (`/admin/orders`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/` | `GET` | Retrieves a paginated list of all orders in the system. |
| `/{orderId}/status` | `PATCH`| Updates the lifecycle status of an order (e.g., `SHIPPED`). |

### 2.3 Internal Endpoints (`/api/internal/orders`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/{orderId}/payment-status` | `PUT` | **Callback:** Used by Payment Service to update payment status. |

---

## 3. Data Transfer Objects (DTOs)

### 3.1 Checkout Requests
- **`CheckoutCartRequestDto`**: `{ cartId, addressId, paymentMethod }`
- **`BuyNowRequestDto`**: `{ bookId, quantity, addressId, paymentMethod }`

### 3.2 Responses
- **`CheckoutResponseDto`**: Returned after order creation. Contains `orderId`, `orderStatus`, `paymentStatus`, and `clientSecret` (for Stripe).
- **`OrderResponseDTO`**: Detailed order info including a list of `OrderItemResponseDTO`.

### 3.3 Updates
- **`PaymentStatusUpdateRequest`**: `{ status }` (Values: `INITIATED`, `SUCCESS`, `FAILED`, `CANCELED`).
- **`UpdateOrderStatusRequest`**: `{ status }` (Values: `CONFIRMED`, `SHIPPED`, etc.).

---

## 4. Database Objects (Entities)

### 4.1 `OrderEntity` (`orders` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long (PK)` | Primary Key. |
| `user_id` | `Long` | ID of the user who placed the order. |
| `total_amount` | `Double` | Grand total of the order. |
| `shipping_address_id` | `Long` | Reference to user's address. |
| `order_status` | `Enum` | `PENDING_PAYMENT`, `CONFIRMED`, `SHIPPED`, etc. |
| `payment_type` | `Enum` | `COD`, `UPI`, `DEBIT`. |
| `payment_id` | `Long` | Internal ID from Payment Service. |
| `payment_status` | `Enum` | `INITIATED`, `SUCCESS`, `FAILED`, `CANCELED`. |

### 4.2 `OrderItemEntity` (`order_items` table)
Stores a snapshot of book details at the time of purchase (Price, ISBN, Title).

---

## 5. Service Interactions (Feign Clients)

### 5.1 Catalog Service (`CATALOG-SERVICE`)
- **`GET /api/books/{id}`**: Fetch book details.
- **`GET /api/inventory/check`**: Verify stock availability.
- **`PUT /api/inventory/reduce`**: Deduct stock upon order placement.
- **`PUT /api/inventory/restore`**: Restore stock upon cancellation.

### 5.2 Payment Service (`PAYMENT-SERVICE`)
- **`POST /api/payments/create-intent`**: Initialize payment and get Stripe secret.
- **`GET /api/payments/order/{orderId}`**: Retrieve payment details for an order.

### 5.3 Cart Service (`CART-SERVICE`)
- **`GET /cart/{cartId}`**: Fetch items from the user's cart for checkout.

---

## 6. Business Logic & Transitions

### 6.1 Payment Callback Handling
When the **Payment Service** calls the internal `/payment-status` endpoint:
1.  The `paymentStatus` is updated in the database.
2.  If the status is **`SUCCESS`**, the `orderStatus` is automatically transitioned from `PENDING_PAYMENT` to **`CONFIRMED`**.

### 6.2 Order Cancellation
- Inventory is automatically restored in the **Catalog Service**.
- Payment status is set to `CANCELED`.

---

## 7. Configurations

### 7.1 Application Configuration
- **App Name**: `order-service`
- **Port**: `8083`
- **Security**: JWT-based authentication for public/admin APIs. Direct internal access permitted for `/api/internal/**`.

---

## 8. Enumerations

### 8.1 `OrderStatus`
- `PENDING_PAYMENT`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`.

### 8.2 `PaymentType`
- `COD`, `UPI`, `DEBIT`.
