package dio.budgeting.application;

import dio.budgeting.application.output.BalanceOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class CalculateTotalBalanceUseCase {
    private final TransactionRepository transactionRepository;

    public CalculateTotalBalanceUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "calculate-total-balance", description = "Calcula o saldo total acumulado de todas as transações financeiras cadastradas")
    public BalanceOutput execute() {
        List<Transaction> transactions = transactionRepository.findAll();

        long totalAmountRaw = transactions.stream()
                .mapToLong(Transaction::getAmount)
                .sum();

        BigDecimal total = BigDecimal.valueOf(totalAmountRaw)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        String formattedTotal = currencyFormat.format(total);

        String message = String.format("O total de gastos acumulado é de %s em um total de %d transação(ões).",
                formattedTotal, transactions.size());

        return new BalanceOutput(transactions.size(), total.doubleValue(), formattedTotal, message);
    }
}
