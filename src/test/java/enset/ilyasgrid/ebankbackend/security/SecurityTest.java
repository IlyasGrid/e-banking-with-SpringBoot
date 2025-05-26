package enset.ilyasgrid.ebankbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import enset.ilyasgrid.ebankbackend.dtos.UserRegistrationDTO;
import enset.ilyasgrid.ebankbackend.web.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPublicEndpointsAccessible() throws Exception {
        // Test registration endpoint is accessible
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setPassword("testpass");
        registrationDTO.setEmail("test@test.com");
        registrationDTO.setFullName("Test User");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testProtectedEndpointsRequireAuth() throws Exception {
        // Test that protected endpoints require authentication
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginWithValidCredentials() throws Exception {
        // Test login with default user
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("user123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        // Test login with invalid credentials
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername("invalid");
        loginRequest.setPassword("invalid");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
