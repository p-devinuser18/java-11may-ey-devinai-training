package ai.meteoros.training.expenses.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // ───── GET /api/expenses ─────

    @Test
    void returns_200_with_list_of_expenses() throws Exception {
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    // ───── GET /api/expenses?month=? ─────

    @Test
    void getByMonth_returns_200_and_expense_when_expense_exists() throws Exception {
        // January has 3 expenses: Food(groceries), Food(dinner), Transport(gas)
        mockMvc.perform(get("/api/expenses").param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].category").exists());
    }

    @Test
    void getByMonth_returns_404_and_error_body_when_expense_does_not_exist() throws Exception {
        // December has no expenses
        mockMvc.perform(get("/api/expenses").param("month", "12"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ───── GET /api/expenses?category=? ─────

    @Test
    void getByCategory_returns_200_and_expense_when_expense_exists() throws Exception {
        mockMvc.perform(get("/api/expenses").param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].category").value("Food"));
    }

    @Test
    void getByCategory_returns_404_and_error_body_when_expense_does_not_exist() throws Exception {
        mockMvc.perform(get("/api/expenses").param("category", "Travel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ───── GET /api/expenses?category=?&month=? ─────

    @Test
    void getByCategoryAndMonth_returns_200_and_expense_when_expense_exists() throws Exception {
        // Food in January: groceries + dinner = 2
        mockMvc.perform(get("/api/expenses")
                        .param("category", "Food")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].category").value("Food"));
    }

    @Test
    void getByCategoryAndMonth_returns_404_and_error_body_when_expense_does_not_exist() throws Exception {
        // Transport in December: none
        mockMvc.perform(get("/api/expenses")
                        .param("category", "Transport")
                        .param("month", "12"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ───── POST /api/expenses ─────

    @Test
    void post_returns_201_with_created_expense_when_valid() throws Exception {
        String json = """
                {
                    "amount": 25.50,
                    "category": "Food",
                    "description": "Lunch at cafe",
                    "expenseDate": "2025-06-10"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.description").value("Lunch at cafe"))
                .andExpect(jsonPath("$.expenseDate").value("2025-06-10"));
    }

    @Test
    void post_returns_400_with_field_errors_when_description_is_blank() throws Exception {
        String json = """
                {
                    "amount": 10.00,
                    "category": "Food",
                    "description": "",
                    "expenseDate": "2025-06-10"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void post_returns_400_with_field_errors_when_category_is_blank() throws Exception {
        String json = """
                {
                    "amount": 10.00,
                    "category": "",
                    "description": "Some expense",
                    "expenseDate": "2025-06-10"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.category").exists());
    }

    @Test
    void post_returns_400_with_field_errors_when_date_is_blank() throws Exception {
        String json = """
                {
                    "amount": 10.00,
                    "category": "Food",
                    "description": "Some expense"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.expenseDate").exists());
    }

    @Test
    void post_returns_400_with_field_errors_when_price_is_negative() throws Exception {
        String json = """
                {
                    "amount": -5.00,
                    "category": "Food",
                    "description": "Invalid expense",
                    "expenseDate": "2025-06-10"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists());
    }

    // ───── PUT /api/expenses/{id} ─────

    @Test
    void put_returns_200_with_updated_expense_when_valid() throws Exception {
        String json = """
                {
                    "amount": 99.99,
                    "category": "Food",
                    "description": "Updated groceries",
                    "expenseDate": "2025-01-11"
                }
                """;

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(99.99))
                .andExpect(jsonPath("$.description").value("Updated groceries"));
    }

    @Test
    void put_returns_404_when_id_does_not_exist() throws Exception {
        String json = """
                {
                    "amount": 10.00,
                    "category": "Food",
                    "description": "Does not matter",
                    "expenseDate": "2025-06-10"
                }
                """;

        mockMvc.perform(put("/api/expenses/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // ───── DELETE /api/expenses/{id} ─────

    @Test
    void delete_returns_204_and_subsequent_get_returns_404() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());

        // Verify the expense is gone — filtering by the deleted item's attributes yields 404
        // or a GET all returns 9 instead of 10
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9));
    }

    @Test
    void delete_returns_404_when_id_does_not_exist() throws Exception {
        mockMvc.perform(delete("/api/expenses/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
