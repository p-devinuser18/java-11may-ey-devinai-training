package ai.meteoros.training.expenses.controller;

import ai.meteoros.training.expenses.entity.Expense;
import ai.meteoros.training.expenses.exception.InvalidMonthException;
import ai.meteoros.training.expenses.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/expenses")
    public ResponseEntity<?> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer month) {

        if (month != null && (month < 1 || month > 12)) {
            throw new InvalidMonthException(month);
        }
        if (category != null && month != null) {
            List<Expense> expenses = expenseService.filterByCategoryandMonth(category, month);
            return ResponseEntity.ok(expenses);
        }
        if (category != null) {
            List<Expense> expenses = expenseService.filterByCategory(category);
            return ResponseEntity.ok(expenses);
        }
        if (month != null) {
            List<Expense> expenses = expenseService.filterByMonth(month);
            return ResponseEntity.ok(expenses);
        }
        List<Expense> expenses = expenseService.findAll();
        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        Expense created = expenseService.addExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id,
                                                  @Valid @RequestBody Expense expense) {
        Expense updated = expenseService.updateExpense(id, expense);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
