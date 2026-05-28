# Cart Service Technical Report

## 1. Overview
The **Cart Service** manages the shopping carts of users in the BookApp ecosystem. It allows users to add books to their cart, update quantities, remove items, and clear the cart. It persists cart data and coordinates with the **Catalog Service** to fetch current book details (price, title) during the addition process.

---

## 2. API Endpoints

### 2.1 User Endpoints (`/cart`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/{bookId}` | `POST` | Adds a book to the user's cart (or increases quantity if already present). |
| `/{bookId}` | `DELETE` | Removes one unit of a book from the cart (or removes it entirely if quantity is 1). |
| `/{bookId}` | `PATCH` | Updates the quantity of a specific book in the cart (using `quantity` query param). |
| `/clear` | `DELETE` | Removes all items from the user's cart. |
| `/{cartId}` | `GET` | Retrieves a specific cart by its ID. |

### 2.2 Admin/Internal Endpoints (`/cart`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/all` | `GET` | Retrieves all carts in the system (Admin only/Internal use). |

---

## 3. Data Transfer Objects (DTOs)

### 3.1 Responses
- **`ApiResponse<T>`**: Standard wrapper for all API responses.
    - `{ success, message, data, timestamp }`
- **`BookResponse`**: Data received from Catalog Service.
    - `{ id, title, price }`

---

## 4. Database Objects (Entities)

### 4.1 `Cart` (`cart` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `cart_id` | `Long (PK)` | Primary Key. |
| `user_id` | `Long` | ID of the user who owns the cart. |
| `total_price` | `Double` | Calculated total value of all items in the cart. |

### 4.2 `CartItem` (`cart_item` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `item_id` | `Integer (PK)` | Primary Key. |
| `book_id` | `String` | ID of the book. |
| `book_title` | `String` | Title of the book (Snapshot). |
| `price` | `Double` | Price of the book (Snapshot at time of adding). |
| `quantity` | `Integer` | Number of units. |
| `cart_id` | `Long (FK)` | Reference to the parent `Cart`. |

---

## 5. Service Interactions (Feign Clients)

### 5.1 Catalog Service (`CATALOG-SERVICE`)
- **`GET /catalog-service/api/books/{id}`**: Fetches book title and price to populate the cart item details.

---

## 6. Business Logic & Transitions

### 6.1 Adding Items
1.  Retrieves the user's cart (or creates a new one if it doesn't exist).
2.  Checks if the book is already in the cart.
3.  If present, increments the quantity.
4.  If not present, calls **Catalog Service** to get book details and creates a new `CartItem`.
5.  Recalculates the `totalPrice` of the cart.

### 6.2 Token Propagation
- Uses a `FeignConfig` with a `RequestInterceptor` to extract the JWT token from the `SecurityContextHolder` and inject it into the `Authorization` header for all outgoing Feign requests to downstream services.

---

## 7. Configurations

### 7.1 Application Configuration
- **App Name**: `cart-service`
- **Port**: `8083`
- **Security**: JWT-based authentication. Validates tokens and extracts `userId` and `roles`.
- **Database**: MySQL (`ecommerce` database).

---

## 8. Enumerations
*No specific enumerations are defined within the Cart Service.*
