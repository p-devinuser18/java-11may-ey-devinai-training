package ai.meteoros.training.expenses.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense not found: " + id);
    }

    public ExpenseNotFoundException(String category) {
        super("Expense not found for category: " + category);
    }

    public ExpenseNotFoundException(String category, int month) {
        super("Expense not found for category: " + category + " and month: " + month);
    }
}
