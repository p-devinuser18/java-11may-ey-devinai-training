package ai.meteoros.training.expenses.exception;

public class InvalidMonthException extends RuntimeException {

    public InvalidMonthException(int month) {
        super("Invalid month: " + month + ". Month must be between 1 and 12.");
    }
}
