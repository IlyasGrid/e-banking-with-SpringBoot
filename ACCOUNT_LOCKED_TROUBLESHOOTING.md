# Account Locked Error Troubleshooting

## Error Message
```
Authentication failed: User account is locked
```

## What This Error Means

This error occurs when Spring Security's `UserDetails` interface returns `false` for the `isAccountNonLocked()` method. Spring Security checks four account status flags:

1. **`enabled`** - Account is active
2. **`accountNonExpired`** - Account hasn't expired
3. **`accountNonLocked`** - Account is not locked
4. **`credentialsNonExpired`** - Password hasn't expired

If any of these return `false`, authentication will fail with specific error messages.

## Root Cause

The issue was likely caused by:

1. **Lombok @Builder Issue**: When using `@Builder` without `@Builder.Default`, default values might not be properly set
2. **Database NULL Values**: Existing users in the database might have `NULL` values for boolean fields
3. **JPA Mapping**: Boolean fields with `NULL` values can be interpreted as `false`

## Fixes Applied

### 1. Updated User Entity
```java
@Builder.Default
private boolean enabled = true;
@Builder.Default
private boolean accountNonExpired = true;
@Builder.Default
private boolean accountNonLocked = true;
@Builder.Default
private boolean credentialsNonExpired = true;
```

### 2. Explicit Status Setting in UserService
```java
User user = User.builder()
    .username(registrationDTO.getUsername())
    .password(passwordEncoder.encode(registrationDTO.getPassword()))
    .email(registrationDTO.getEmail())
    .fullName(registrationDTO.getFullName())
    .role(User.Role.USER)
    .enabled(true)
    .accountNonExpired(true)
    .accountNonLocked(true)
    .credentialsNonExpired(true)
    .build();
```

### 3. Database Fix Method
Added a method to fix existing users:
```java
public void fixUserAccountStatus() {
    userRepository.findAll().forEach(user -> {
        boolean needsUpdate = false;
        
        if (!user.isEnabled()) {
            user.setEnabled(true);
            needsUpdate = true;
        }
        // ... fix other flags
        
        if (needsUpdate) {
            userRepository.save(user);
        }
    });
}
```

## Manual Database Fix (Alternative)

If you prefer to fix the database directly:

```sql
-- Check current user status
SELECT username, enabled, account_non_expired, account_non_locked, credentials_non_expired 
FROM users;

-- Fix all users
UPDATE users 
SET enabled = true, 
    account_non_expired = true, 
    account_non_locked = true, 
    credentials_non_expired = true 
WHERE enabled IS NULL 
   OR account_non_expired IS NULL 
   OR account_non_locked IS NULL 
   OR credentials_non_expired IS NULL
   OR enabled = false
   OR account_non_expired = false
   OR account_non_locked = false
   OR credentials_non_expired = false;
```

## Testing the Fix

### 1. Check User Status
```bash
# Login should now work
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. Expected Success Response
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

### 3. If Still Getting Error
Check the database directly:
```sql
SELECT * FROM users WHERE username = 'admin';
```

All boolean fields should be `1` (true) or `true` depending on your database.

## Prevention

To prevent this issue in the future:

1. **Always use `@Builder.Default`** with Lombok when you have default values
2. **Set database constraints** to ensure boolean fields are NOT NULL with default values
3. **Add validation** in your service layer to ensure account status is properly set
4. **Use database migrations** to set proper defaults for existing columns

## Database Schema Update

Consider updating your database schema:

```sql
ALTER TABLE users 
MODIFY COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE,
MODIFY COLUMN account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
MODIFY COLUMN account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
MODIFY COLUMN credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE;
```

## Application Startup Fix

The application now automatically fixes any existing users with account status issues on startup, so this error should not occur again.

If you continue to see this error, it means there might be a deeper issue with the user creation or database configuration.
