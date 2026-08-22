package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateTotalBalanceUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CalculateTotalBalanceUseCase useCase;

    @Test
    void shouldCalculateTotalBalanceCorrectly() {
        var t1 = new Transaction("Supermercado", 15050L, Category.GROCERIES);
        var t2 = new Transaction("Combustível", 20000L, Category.AUTO);
        var t3 = new Transaction("Farmácia", 4950L, Category.PHARMA);

        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2, t3));

        var output = useCase.execute();

        assertThat(output).isNotNull();
        assertThat(output.transactionCount()).isEqualTo(3);
        assertThat(output.totalAmount()).isEqualTo(400.00);
        assertThat(output.formattedTotal()).contains("400");
        assertThat(output.message()).contains("400");
    }

    @Test
    void shouldHandleEmptyTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        var output = useCase.execute();

        assertThat(output).isNotNull();
        assertThat(output.transactionCount()).isEqualTo(0);
        assertThat(output.totalAmount()).isEqualTo(0.00);
    }
}
