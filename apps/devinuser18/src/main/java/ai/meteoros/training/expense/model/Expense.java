package ai.meteoros.training.expense.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(
        Long id,
        BigDecimal amount,
        String category,
        String description,
        LocalDate date
) {
}
