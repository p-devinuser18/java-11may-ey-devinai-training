package ai.meteoros.training.expense.controller;

import ai.meteoros.training.expense.model.Expense;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final List<Expense> expenses;

    public ExpenseController() {
        this.expenses = List.of(
                new Expense(1L, new BigDecimal("49.99"), "Food", "Weekly groceries", LocalDate.of(2025, 5, 10)),
                new Expense(2L, new BigDecimal("120.00"), "Transport", "Monthly metro pass", LocalDate.of(2025, 5, 1)),
                new Expense(3L, new BigDecimal("15.50"), "Entertainment", "Movie ticket", LocalDate.of(2025, 5, 15))
        );
    }

    @GetMapping("/expenses")
    public List<Expense> getExpenses() {
        return expenses;
    }
}
