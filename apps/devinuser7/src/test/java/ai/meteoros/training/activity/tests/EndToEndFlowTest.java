package ai.meteoros.training.activity.tests;

import ai.meteoros.training.activity.services.ActivityLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EndToEndFlowTest {

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

    @Test
    void fullUserLifecycle_CreateLoginUpdateDeleteAndVerifyAllActivities() throws Exception {
        String userId = "E2E_USER";

        // Step 1: Create user
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + userId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userId + " created"));

        // Step 2: Login
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + userId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userId + " logged in"));

        // Step 3: Update user
        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated E2E User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userId + " updated"));

        // Step 4: Logout
        mockMvc.perform(post("/api/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + userId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userId + " logged out"));

        // Step 5: Login again
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + userId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userId + " logged in"));

        // Step 6: Delete user
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User " + userId + " deleted"));

        // Step 7: Verify all activities for this user are logged
        mockMvc.perform(get("/api/activity").param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].action").value("CREATE"))
                .andExpect(jsonPath("$[1].action").value("LOGIN"))
                .andExpect(jsonPath("$[2].action").value("UPDATE"))
                .andExpect(jsonPath("$[3].action").value("LOGOUT"))
                .andExpect(jsonPath("$[4].action").value("LOGIN"))
                .andExpect(jsonPath("$[5].action").value("DELETE"));

        // Step 8: Verify filtering by specific actions
        mockMvc.perform(get("/api/activity").param("userId", userId).param("action", "LOGIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/activity").param("userId", userId).param("action", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].action").value("DELETE"));

        // Step 9: Verify that sample data is still intact
        mockMvc.perform(get("/api/activity").param("userId", "U101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        // Step 10: Verify total log count (10 sample + 6 e2e)
        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(16));
    }
}
