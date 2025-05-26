# E-Banking Security Implementation Guide

## Overview
This document describes the security implementation for the E-Banking Spring Boot application using JWT authentication and role-based authorization.

## Security Features Implemented

### 1. User Authentication
- **JWT Token-based Authentication**: Stateless authentication using JSON Web Tokens
- **Password Encryption**: BCrypt password encoding for secure password storage
- **User Registration**: Endpoint for creating new user accounts
- **User Login**: Endpoint for authenticating users and receiving JWT tokens

### 2. Authorization
- **Role-based Access Control**: Three user roles (USER, ADMIN, MANAGER)
- **Method-level Security**: Enabled with `@EnableMethodSecurity`
- **Endpoint Protection**: Different access levels for different endpoints

### 3. Security Configuration
- **CSRF Disabled**: For stateless API
- **CORS Enabled**: Cross-origin requests allowed
- **Session Management**: Stateless session creation policy

## API Endpoints

### Public Endpoints (No Authentication Required)
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `GET /swagger-ui/**` - API documentation
- `GET /v3/api-docs/**` - OpenAPI documentation

### Protected Endpoints (Authentication Required)
- `GET /customers/**` - Customer management (USER, ADMIN, MANAGER roles)
- `GET /accounts/**` - Account management (USER, ADMIN, MANAGER roles)
- `POST /customers/**` - Create customers (USER, ADMIN, MANAGER roles)
- `POST /accounts/**` - Account operations (USER, ADMIN, MANAGER roles)

## Default Users
The application creates two default users on startup:

1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Email: `admin@ebank.com`
   - Role: `USER` (can be manually updated to ADMIN in database)

2. **Test User**
   - Username: `user`
   - Password: `user123`
   - Email: `user@ebank.com`
   - Role: `USER`

## Usage Examples

### 1. User Registration
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "fullName": "New User"
  }'
```

### 2. User Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

### 3. Accessing Protected Endpoints
```bash
curl -X GET http://localhost:8080/customers \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9..."
```

## Security Components

### 1. User Entity
- Implements `UserDetails` interface
- Contains user credentials and role information
- Supports account status flags (enabled, expired, locked)

### 2. Security Configuration
- `SecurityConfig`: Main security configuration
- `JwtConfig`: JWT encoder/decoder configuration
- `UserDetailsServiceImpl`: Custom user details service

### 3. JWT Utilities
- `JwtUtils`: Token generation and validation
- RSA key pair for token signing
- 1-hour token expiration

## Testing
Run the security tests to verify the implementation:

```bash
mvn test -Dtest=SecurityTest
```

## Database Schema
The security implementation adds a `users` table:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE
);
```

## Security Best Practices Implemented
1. **Password Encryption**: All passwords are encrypted using BCrypt
2. **Stateless Authentication**: No server-side session storage
3. **Token Expiration**: JWT tokens expire after 1 hour
4. **Role-based Authorization**: Different access levels for different user types
5. **Input Validation**: Basic validation on registration endpoints
6. **Error Handling**: Proper error responses for authentication failures

## Next Steps
To further enhance security, consider implementing:
1. **Refresh Tokens**: For better user experience
2. **Rate Limiting**: To prevent brute force attacks
3. **Account Lockout**: After multiple failed login attempts
4. **Email Verification**: For user registration
5. **Password Complexity Rules**: Stronger password requirements
6. **Audit Logging**: Track security-related events
