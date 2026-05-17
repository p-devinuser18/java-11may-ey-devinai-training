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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerTest {

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
    void shouldReturnAllLogs() throws Exception {
        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void shouldFilterLogsByUserId() throws Exception {
        mockMvc.perform(get("/api/activity").param("userId", "U101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("U101"));
    }

    @Test
    void shouldFilterLogsByAction() throws Exception {
        mockMvc.perform(get("/api/activity").param("action", "LOGIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("LOGIN"));
    }

    @Test
    void shouldFilterByUserIdAndAction() throws Exception {
        mockMvc.perform(get("/api/activity")
                        .param("userId", "U101")
                        .param("action", "LOGIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("U101"))
                .andExpect(jsonPath("$[0].action").value("LOGIN"));
    }

    @Test
    void shouldReturn400WhenNoLogsFound() throws Exception {
        mockMvc.perform(get("/api/activity").param("userId", "NONEXISTENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No logs found"));
    }

    @Test
    void shouldLogLoginAction() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"U200\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U200 logged in"));

        mockMvc.perform(get("/api/activity").param("userId", "U200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("LOGIN"));
    }

    @Test
    void shouldLogLogoutAction() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"U200\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U200 logged out"));

        mockMvc.perform(get("/api/activity").param("userId", "U200").param("action", "LOGOUT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("LOGOUT"));
    }

    @Test
    void shouldLogCreateUserAction() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"U300\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U300 created"));

        mockMvc.perform(get("/api/activity").param("userId", "U300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("CREATE"));
    }

    @Test
    void shouldLogUpdateUserAction() throws Exception {
        mockMvc.perform(put("/api/users/U300")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U300 updated"));

        mockMvc.perform(get("/api/activity").param("userId", "U300").param("action", "UPDATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("UPDATE"));
    }

    @Test
    void shouldLogDeleteUserAction() throws Exception {
        mockMvc.perform(delete("/api/users/U300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User U300 deleted"));

        mockMvc.perform(get("/api/activity").param("userId", "U300").param("action", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("DELETE"));
    }

    @Test
    void shouldReturn400WhenLogFileIsEmpty() throws Exception {
        Files.delete(LOG_FILE);
        Files.createFile(LOG_FILE);

        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No logs found"));
    }
}
