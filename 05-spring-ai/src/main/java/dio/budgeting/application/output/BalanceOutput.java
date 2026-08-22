package dio.budgeting.application.output;

public record BalanceOutput(int transactionCount, double totalAmount, String formattedTotal, String message) {
}
