package ai.meteoros.training.expenses.repository;

import ai.meteoros.training.expenses.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
