package ai.meteoros.training.activity.tests;

import ai.meteoros.training.activity.services.ActivityLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityLogger activityLogger;

    private static final Path LOG_FILE = Paths.get("src/data/activity.log");

    @BeforeEach
    void setUp() throws Exception {
        if (Files.exists(LOG_FILE)) {
            Files.delete(LOG_FILE);
        }
        activityLogger.init();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (Files.exists(LOG_FILE)) {
            Files.delete(LOG_FILE);
        }
    }

    // --- Login endpoint null/edge cases ---

    @Test
    void loginWithMissingUserIdKeyShouldThrowNullPointerException() {
        ServletException ex = Assertions.assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
        );
        assertThat(ex.getCause()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void loginWithEmptyUserIdShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User  logged in"));
    }

    @Test
    void loginWithNullBodyShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithInvalidJsonShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithSpecialCharactersInUserIdShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user@domain.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User user@domain.com logged in"));
    }

    @Test
    void loginWithExtraFieldsShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"U100\",\"extra\":\"field\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U100 logged in"));
    }

    // --- Logout endpoint null/edge cases ---

    @Test
    void logoutWithMissingUserIdKeyShouldThrowNullPointerException() {
        ServletException ex = Assertions.assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
        );
        assertThat(ex.getCause()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void logoutWithEmptyUserIdShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User  logged out"));
    }

    @Test
    void logoutWithNullBodyShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // --- Create user endpoint null/edge cases ---

    @Test
    void createUserWithMissingUserIdKeyShouldThrowNullPointerException() {
        ServletException ex = Assertions.assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
        );
        assertThat(ex.getCause()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void createUserWithEmptyUserIdShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User  created"));
    }

    @Test
    void createUserWithNullBodyShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // --- Update user endpoint null/edge cases ---

    @Test
    void updateUserWithEmptyBodyShouldReturn200() throws Exception {
        mockMvc.perform(put("/api/users/U100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U100 updated"));
    }

    @Test
    void updateUserWithNullBodyShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/users/U100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithSpecialCharsInPathShouldReturn200() throws Exception {
        mockMvc.perform(put("/api/users/user-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User user-123 updated"));
    }

    // --- Delete user endpoint edge cases ---

    @Test
    void deleteUserShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/users/U100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U100 deleted"));
    }

    @Test
    void deleteUserWithSpecialCharsInPathShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/users/user-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User user-456 deleted"));
    }

    @Test
    void deleteUserShouldLogDeleteAction() throws Exception {
        mockMvc.perform(delete("/api/users/U700"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/activity").param("userId", "U700").param("action", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("U700"))
                .andExpect(jsonPath("$[0].action").value("DELETE"));
    }

    // --- Content type edge cases ---

    @Test
    void loginWithoutContentTypeShouldReturn415() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .content("{\"userId\":\"U100\"}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // --- Method not allowed ---

    @Test
    void getOnLoginEndpointShouldReturn405() throws Exception {
        mockMvc.perform(get("/api/users/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void getOnLogoutEndpointShouldReturn405() throws Exception {
        mockMvc.perform(get("/api/users/logout"))
                .andExpect(status().isMethodNotAllowed());
    }
}
