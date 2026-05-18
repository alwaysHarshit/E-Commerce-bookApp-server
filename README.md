# Catalog Service Technical Report

## 1. Overview
The **Catalog Service** is the central repository for the BookApp ecosystem's book collection. It manages book metadata, maintains inventory levels, and handles cover image storage via AWS S3. It provides public search and filtering capabilities for users and administrative tools for catalog management. It acts as a critical data source for the **Order Service** and **Cart Service**.

---

## 2. API Endpoints

### 2.1 Public User Endpoints (`/api/books`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/` | `GET` | Retrieves all available books in the catalog. |
| `/{id}` | `GET` | Fetches detailed information for a specific book. |
| `/search/title` | `GET` | Searches for books by title (Query param: `title`). |
| `/search/author` | `GET` | Searches for books by author (Query param: `author`). |
| `/filter/genre` | `GET` | Filters books by genre (Query param: `genre`). |

### 2.2 Admin Endpoints (`/admin/books`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/` | `POST` | Adds a new book with a cover image (Multipart). |
| `/` | `GET` | Retrieves all books with administrative details (e.g., S3 keys). |
| `/{id}` | `PATCH`| Updates an existing book's details or cover image. |
| `/{id}` | `DELETE`| Removes a book and its associated cover image from S3. |

### 2.3 Inventory Endpoints (`/api/inventory`)
These endpoints are primarily used internally by other services (e.g., Order Service).
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/check` | `GET` | Checks if a specific quantity of a book is in stock. |
| `/reduce` | `PUT` | Deducts stock upon successful order placement. |
| `/restore` | `PUT` | Adds stock back if an order is canceled. |
| `/update` | `PUT` | Manually updates the stock level for a book. |

---

## 3. Data Transfer Objects (DTOs)

### 3.1 Requests
- **`BookRequestDTO`**: Used for creating/updating books. Includes fields like `title`, `author`, `isbn`, `price`, `stocks`, and a `MultipartFile` for the cover image.

### 3.2 Responses
- **`UserBookResponse`**: Standard book details for public consumption (includes `coverImageUrl` and `stock`).
- **`AdminBookResponse`**: Extends user details with administrative fields like `coverImageKey`.
- **`ApiResponse<T>`**: Generic wrapper for all API responses providing status messages and data.

---

## 4. Database Objects (Entities)

### 4.1 `Book` (`books` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long (PK)` | Primary Key. |
| `title` | `String` | Book Title. |
| `author` | `String` | Author Name. |
| `isbn` | `String (Unique)`| International Standard Book Number. |
| `genre` | `String` | Book Genre. |
| `price` | `Double` | Listing Price. |
| `cover_image_url` | `String` | Public S3 URL for the cover image. |
| `cover_image_key` | `String` | S3 Key for image management/deletion. |

### 4.2 `Inventory` (`inventory` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long (PK)` | Primary Key. |
| `book_id` | `Long (FK)` | One-to-One relationship with `Book`. |
| `stock` | `Integer` | Current quantity available. |
| `status` | `String` | `AVAILABLE`, `OUT_OF_STOCK`, etc. |
| `last_updated` | `Timestamp` | Automatic timestamp of the last stock change. |

---

## 5. Storage & External Integrations

### 5.1 AWS S3 Integration
- **Bucket**: `booknest-catalog`
- **Region**: `us-east-1`
- **Usage**: Used to store and serve book cover images. The service handles both upload (via `AmazonS3` client) and cleanup upon book deletion.

---

## 6. Business Logic & Transitions

### 6.1 Stock Management
- When stock reaches **0**, the status is automatically set to `OUT_OF_STOCK`.
- Restoring stock to a value **> 0** reverts the status to `AVAILABLE`.
- Stock reduction triggers a check for "Insufficient stock" exceptions.

### 6.2 Image Lifecycle
- Adding a book uploads the image to S3 and stores the URL and Key.
- Updating a book can replace the existing image in S3.
- Deleting a book triggers a deletion request to S3 using the stored `coverImageKey`.

---

## 7. Configurations

### 7.1 Application Configuration
- **App Name**: `catalog-Service`
- **Port**: `8082`
- **Database**: MySQL (`CATALOG_DB`)
- **context-path**: `/catalog-service`
- **Service Discovery**: Eureka Client enabled (Registers with `service-registry:8085`).
- **Multipart Limits**: 50MB for cover image uploads.
