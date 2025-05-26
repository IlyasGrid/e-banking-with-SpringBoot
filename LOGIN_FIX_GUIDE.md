# Login Fix Guide

## Issues Fixed

The login was returning 401 errors due to several configuration issues:

### 1. **Authentication Provider Configuration**
- **Problem**: The `DaoAuthenticationProvider` was defined as a bean but not properly integrated with the authentication manager
- **Fix**: Created a custom `AuthenticationManager` using `ProviderManager` that explicitly uses our `DaoAuthenticationProvider`

### 2. **JWT Configuration**
- **Problem**: Deprecated JWT configuration methods and missing JWT decoder injection
- **Fix**: Updated to use the new JWT configuration syntax and properly injected `JwtDecoder`

### 3. **Error Handling**
- **Problem**: No proper error handling for authentication failures
- **Fix**: Added try-catch blocks with specific error messages for different authentication exceptions

## Key Changes Made

### SecurityConfig.java
```java
@Bean
public AuthenticationManager authenticationManager() throws Exception {
    return new ProviderManager(authenticationProvider());
}
```

### AuthController.java
```java
@PostMapping("/login")
public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
    try {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        // ... JWT token generation
    } catch (BadCredentialsException e) {
        // Return proper error response
    }
}
```

## Testing the Login

### 1. **Register a New User** (Optional)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "email": "test@example.com",
    "fullName": "Test User"
  }'
```

### 2. **Login with Default Users**
```bash
# Login with admin user
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Login with regular user
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE3MDk..."
}
```

### 3. **Test Authentication with Token**
```bash
# Use the token from login response
curl -X GET http://localhost:8080/auth/test \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "message": "Authentication test successful",
  "user": "admin",
  "authorities": "[ROLE_USER]"
}
```

### 4. **Test Protected Endpoints**
```bash
# Access customers endpoint
curl -X GET http://localhost:8080/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# Access accounts endpoint
curl -X GET http://localhost:8080/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Error Scenarios

### Invalid Credentials
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "invalid",
    "password": "invalid"
  }'
```

**Expected Response:**
```json
{
  "error": "Invalid username or password"
}
```

### Missing Token
```bash
curl -X GET http://localhost:8080/customers
```

**Expected Response:** `401 Unauthorized`

### Invalid Token
```bash
curl -X GET http://localhost:8080/customers \
  -H "Authorization: Bearer invalid_token"
```

**Expected Response:** `401 Unauthorized`

## Default Users Available

1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Role: `USER`

2. **Test User**
   - Username: `user`
   - Password: `user123`
   - Role: `USER`

## Architecture Overview

1. **Login Flow:**
   - User sends credentials to `/auth/login`
   - `AuthenticationManager` uses `DaoAuthenticationProvider`
   - `DaoAuthenticationProvider` uses `UserDetailsServiceImpl`
   - `UserDetailsServiceImpl` loads user from database
   - Password is verified using `BCryptPasswordEncoder`
   - JWT token is generated and returned

2. **Protected Endpoint Access:**
   - Client sends request with `Authorization: Bearer <token>` header
   - Spring Security validates JWT token using `JwtDecoder`
   - If valid, request proceeds to controller
   - If invalid, returns 401 Unauthorized

The login should now work correctly without 401 errors for valid credentials!
