package ai.meteoros.training.expenses.service;

import ai.meteoros.training.expenses.entity.Expense;
import ai.meteoros.training.expenses.exception.ExpenseNotFoundException;
import ai.meteoros.training.expenses.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense foodExpenseMay;
    private Expense travelExpenseJune;

    @BeforeEach
    void setUp() {
        foodExpenseMay = new Expense(1L, new BigDecimal("25.50"), "Food",
                "Lunch", LocalDate.of(2025, 5, 15));
        travelExpenseJune = new Expense(2L, new BigDecimal("120.00"), "Travel",
                "Taxi", LocalDate.of(2025, 6, 10));
    }

    @Nested
    class GetExpensesWithoutCategoryOrMonth {

        @Test
        void returns_all_products_when_repository_has_data() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            List<Expense> result = expenseService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(foodExpenseMay, travelExpenseJune);
            verify(expenseRepository).findAll();
        }

        @Test
        void returns_empty_list_when_repository_is_empty() {
            when(expenseRepository.findAll()).thenReturn(Collections.emptyList());

            List<Expense> result = expenseService.findAll();

            assertThat(result).isEmpty();
            verify(expenseRepository).findAll();
        }
    }

    @Nested
    class GetExpensesWithCategory {

        @Test
        void returns_expense_when_expense_exists() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            List<Expense> result = expenseService.filterByCategory("Food");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategory()).isEqualTo("Food");
        }

        @Test
        void throws_ExpenseNotFoundException_when_expense_does_not_exist() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            assertThatThrownBy(() -> expenseService.filterByCategory("Entertainment"))
                    .isInstanceOf(ExpenseNotFoundException.class);
        }
    }

    @Nested
    class GetExpensesWithMonth {

        @Test
        void returns_expense_when_expense_exists() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            List<Expense> result = expenseService.filterByMonth(5);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getExpenseDate().getMonthValue()).isEqualTo(5);
        }

        @Test
        void throws_ExpenseNotFoundException_when_expense_does_not_exist() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            assertThatThrownBy(() -> expenseService.filterByMonth(12))
                    .isInstanceOf(ExpenseNotFoundException.class);
        }

        @Test
        void throws_BadRequest_when_month_is_invalid() {
            when(expenseRepository.findAll()).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> expenseService.filterByMonth(13))
                    .isInstanceOf(ExpenseNotFoundException.class);
        }
    }

    @Nested
    class GetExpensesWithMonthAndCategory {

        @Test
        void returns_expense_when_expense_exists() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            List<Expense> result = expenseService.filterByCategoryandMonth("Food", 5);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategory()).isEqualTo("Food");
            assertThat(result.get(0).getExpenseDate().getMonthValue()).isEqualTo(5);
        }

        @Test
        void throws_ExpenseNotFoundException_when_expense_does_not_exist() {
            when(expenseRepository.findAll()).thenReturn(List.of(foodExpenseMay, travelExpenseJune));

            assertThatThrownBy(() -> expenseService.filterByCategoryandMonth("Food", 12))
                    .isInstanceOf(ExpenseNotFoundException.class);
        }
    }

    @Nested
    class Create {

        @Test
        void saves_and_returns_expense_with_generated_id() {
            Expense input = new Expense(null, new BigDecimal("50.00"), "Food",
                    "Dinner", LocalDate.of(2025, 5, 20));
            Expense saved = new Expense(10L, new BigDecimal("50.00"), "Food",
                    "Dinner", LocalDate.of(2025, 5, 20));

            when(expenseRepository.save(any(Expense.class))).thenReturn(saved);

            Expense result = expenseService.addExpense(input);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getCategory()).isEqualTo("Food");
            verify(expenseRepository).save(input);
        }

        @Test
        void clears_id_field_before_saving_when_id_is_passed() {
            Expense input = new Expense(99L, new BigDecimal("50.00"), "Food",
                    "Dinner", LocalDate.of(2025, 5, 20));
            Expense saved = new Expense(10L, new BigDecimal("50.00"), "Food",
                    "Dinner", LocalDate.of(2025, 5, 20));

            when(expenseRepository.save(any(Expense.class))).thenReturn(saved);

            expenseService.addExpense(input);

            assertThat(input.getId()).isNull();
            verify(expenseRepository).save(input);
        }
    }

    @Nested
    class Update {

        @Test
        void updates_existing_expense_and_returns_updated() {
            Expense input = new Expense(null, new BigDecimal("75.00"), "Travel",
                    "Bus", LocalDate.of(2025, 6, 15));
            Expense saved = new Expense(1L, new BigDecimal("75.00"), "Travel",
                    "Bus", LocalDate.of(2025, 6, 15));

            when(expenseRepository.existsById(1L)).thenReturn(true);
            when(expenseRepository.save(any(Expense.class))).thenReturn(saved);

            Expense result = expenseService.updateExpense(1L, input);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDescription()).isEqualTo("Bus");
            assertThat(input.getId()).isEqualTo(1L);
            verify(expenseRepository).existsById(1L);
            verify(expenseRepository).save(input);
        }

        @Test
        void throws_ExpenseNotFoundException_when_id_does_not_exist() {
            Expense input = new Expense(null, new BigDecimal("75.00"), "Travel",
                    "Bus", LocalDate.of(2025, 6, 15));

            when(expenseRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> expenseService.updateExpense(999L, input))
                    .isInstanceOf(ExpenseNotFoundException.class);

            verify(expenseRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing_expense() {
            when(expenseRepository.existsById(1L)).thenReturn(true);

            expenseService.delete(1L);

            verify(expenseRepository).existsById(1L);
            verify(expenseRepository).deleteById(1L);
        }

        @Test
        void throws_ExpenseNotFoundException_when_id_does_not_exist() {
            when(expenseRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> expenseService.delete(999L))
                    .isInstanceOf(ExpenseNotFoundException.class);

            verify(expenseRepository, never()).deleteById(any());
        }
    }
}
