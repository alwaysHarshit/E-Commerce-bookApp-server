# Auth Service Technical Report

## 1. Overview
The **Auth Service** is the central identity and access management component of the BookApp ecosystem. It handles user registration (with OTP-based email verification), authentication (Local and OAuth2), and user profile management, including multi-address support. It issues JWT tokens for secure communication across the microservices architecture.

---

## 2. API Endpoints

### 2.1 Authentication Endpoints (`/auth`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/login` | `POST` | Authenticates a user and returns a JWT token. |
| `/register` | `POST` | Registers a new user and sends an OTP to their email. |
| `/admin/register` | `POST` | Registers a new admin user (Requires `ADMIN` role). |
| `/verify-otp` | `POST` | Verifies the OTP sent during registration to activate the account. |
| `/test/addAdmin` | `POST` | Setup endpoint to add the first admin user without existing permissions. |

### 2.2 User Management Endpoints (`/user`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/me` | `GET` | Returns the profile details of the currently authenticated user. |
| `/update` | `PATCH` | Updates the password for the currently authenticated user. |
| `/delete` | `DELETE` | Permanently deletes the user account. |

### 2.3 Address Management Endpoints (`/users/address`)
| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/` | `GET` | Retrieves all addresses associated with the authenticated user. |
| `/{addressId}` | `GET` | Fetches details of a specific address by its ID. |
| `/` | `POST` | Adds a new shipping address for the user. |
| `/{addressId}` | `PATCH` | Updates an existing address. |
| `/{addressId}` | `DELETE` | Removes an address from the user's profile. |

---

## 3. Data Transfer Objects (DTOs)

### 3.1 Authentication Requests
- **`LoginRequestDto`**: `{ email, password }`
- **`RegisterRequestDto`**: `{ name, email, password }`
- **`UpdatePasswordDTO`**: `{ oldPassword, newPassword }`

### 3.2 Address Requests
- **`AddressRequestDTO`**: `{ addressLine1, addressLine2, landmark, city, state, country, postalCode }`

### 3.3 Responses
- **`LoginResponse`**: Contains `token`, `email`, `role`, and `name`.
- **`ApiResponse<T>`**: Standardized response wrapper containing `success` status, `message`, and generic `data` payload.

---

## 4. Database Objects (Entities)

### 4.1 `UserEntity` (`users` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long (PK)` | Primary Key. |
| `email` | `String` | Unique identifier (Login username). |
| `password` | `String` | BCrypt encoded password. |
| `name` | `String` | Full name of the user. |
| `phoneNumber`| `String` | Contact number. |
| `provider` | `Enum` | Authentication source (`LOCAL`, `GITHUB`, `GOOGLE`, etc.). |
| `role` | `Enum` | User permissions (`USER`, `ADMIN`). |
| `is_verified`| `Boolean`| Indicates if the email is verified via OTP. |
| `otp` | `String` | Current active OTP for verification. |
| `otp_expiry` | `DateTime` | Expiration timestamp for the OTP. |

### 4.2 `AddressEntity` (`addresses` table)
| Column | Type | Description |
| :--- | :--- | :--- |
| `address_id` | `Long (PK)` | Primary Key. |
| `user_id` | `Long (FK)`| Reference to the owner in the `users` table. |
| `address_line1`| `String` | Street address. |
| `city` | `String` | City name. |
| `state` | `String` | State/Province. |
| `country` | `String` | Country. |
| `is_default` | `Boolean` | Flag for the primary shipping address. |

---

## 5. Core Business Logic

### 5.1 Registration Workflow
1.  User submits details; system checks for duplicate email.
2.  Password is encrypted using `BCrypt`.
3.  A 6-digit OTP is generated and stored with a 5-minute expiry.
4.  User record is saved with `is_verified = false`.
5.  `EmailService` sends the OTP to the user's email via Gmail SMTP.

### 5.2 Authentication & Security
- **JWT Generation**: Upon successful login, a JWT is generated containing the user's ID and Role as claims.
- **Security Filter**: `JwtFilter` intercepts requests to validate the token and populate the Security Context.
- **OAuth2**: `OAuth2SuccessHandler` handles redirection and JWT generation after successful social login (GitHub).

---

## 6. Configurations

### 6.1 Application Properties
- **App Name**: `auth-service`
- **Port**: `8081`
- **Context Path**: `/auth-service`
- **Database**: MySQL with Hibernate DDL-auto set to `update`.
- **Eureka**: Enabled (`register-with-eureka: true`).
- **Mail**: Configured for `smtp.gmail.com` on port `587`.

---

## 7. Enumerations

### 7.1 `Role`
- `USER`: Standard customer access.
- `ADMIN`: Administrative access for management APIs.

### 7.2 `Provider`
- `LOCAL`: Standard email/password registration.
- `GOOGLE`, `GITHUB`, `META`, `LINKEDIN`: Third-party social login providers.
