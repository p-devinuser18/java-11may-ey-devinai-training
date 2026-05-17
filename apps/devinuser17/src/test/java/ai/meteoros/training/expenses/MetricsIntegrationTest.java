package ai.meteoros.training.expenses;

import ai.meteoros.training.expenses.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MetricsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MetricsService metricsService;

    @BeforeEach
    void resetMetrics() {
        metricsService.reset();
    }

    @Test
    void shouldReturnMetricsObject() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").isNumber())
                .andExpect(jsonPath("$.successCount").isNumber())
                .andExpect(jsonPath("$.failureCount").isNumber())
                .andExpect(jsonPath("$.byStatusCode").isMap());
    }

    @Test
    void shouldStartWithZeroCounts() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.successCount", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.failureCount", greaterThanOrEqualTo(0)));
    }

    @Test
    void shouldCountSuccessfulRequests() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount", greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldCountFailedRequests() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failureCount", greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldTrackBothSuccessAndFailureCounts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.failureCount", greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldNotRequireAuth() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnJsonContentType() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}
