package ai.meteoros.training.expenses.service;

import ai.meteoros.training.expenses.entity.Expense;
import ai.meteoros.training.expenses.exception.ExpenseNotFoundException;
import ai.meteoros.training.expenses.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense filterByCategory(String category) {
        return expenseRepository.findAll().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new ExpenseNotFoundException(0L));
    }

    public Expense filterByCategoryandMonth(String category, int month) {
        return expenseRepository.findAll().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .filter(e -> e.getExpenseDate().getMonthValue() == month)
                .findFirst()
                .orElseThrow(() -> new ExpenseNotFoundException(0L));
    }

    public Expense addExpense(Expense expense) {
        expense.setId(null);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense expense) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        expense.setId(id);
        return expenseRepository.save(expense);
    }

    public void delete(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        expenseRepository.deleteById(id);
    }
}
