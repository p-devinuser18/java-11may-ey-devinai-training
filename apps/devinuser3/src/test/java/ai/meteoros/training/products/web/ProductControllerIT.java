package ai.meteoros.training.products.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // ---- GET /api/products ----

    @Test
    void returns_200_with_list_of_products() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Wireless Mouse"))
                .andExpect(jsonPath("$[1].name").value("Coffee Mug"))
                .andExpect(jsonPath("$[2].name").value("Notebook"));
    }

    // ---- GET /api/products/{id} ----

    @Test
    void returns_200_and_product_when_id_exists() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    @Test
    void returns_404_and_error_body_when_id_does_not_exist() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 9999"));
    }

    // ---- POST /api/products ----

    @Test
    void returns_201_with_created_product_when_valid() throws Exception {
        String body = "{\"name\":\"Headphones\",\"price\":89.99,\"category\":\"Audio\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Headphones"))
                .andExpect(jsonPath("$.price").value(89.99))
                .andExpect(jsonPath("$.category").value("Audio"));
    }

    @Test
    void returns_400_with_field_errors_when_name_is_blank() throws Exception {
        String body = "{\"name\":\"\",\"price\":29.99,\"category\":\"Electronics\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void returns_400_with_field_errors_when_price_is_negative() throws Exception {
        String body = "{\"name\":\"Test\",\"price\":-5.00,\"category\":\"Electronics\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists());
    }

    // ---- PUT /api/products/{id} ----

    @Test
    void returns_200_with_updated_product_when_valid() throws Exception {
        String body = "{\"name\":\"Updated Mouse\",\"price\":34.99,\"category\":\"Electronics\"}";

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Mouse"))
                .andExpect(jsonPath("$.price").value(34.99))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    @Test
    void returns_404_when_id_does_not_exist_put() throws Exception {
        String body = "{\"name\":\"Ghost\",\"price\":10.00,\"category\":\"Misc\"}";

        mockMvc.perform(put("/api/products/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 9999"));
    }

    // ---- DELETE /api/products/{id} ----

    @Test
    void returns_204_and_subsequent_get_returns_404() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 1"));
    }

    @Test
    void returns_404_when_id_does_not_exist_delete() throws Exception {
        mockMvc.perform(delete("/api/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found: 9999"));
    }
}
