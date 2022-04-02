package id.my.btw.repository;

import id.my.btw.entity.Expense;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExpenseRepositoryImplTest {

    private final EasyRandom easyRandom = new EasyRandom();
    private final ExpenseRepository expenseRepository = new ExpenseRepositoryImpl();

    @Test
    void testInsert() {
        Expense expense = Expense.builder()
                .note(easyRandom.nextObject(String.class))
                .amount(easyRandom.nextObject(Integer.class))
                .category(easyRandom.nextObject(String.class))
                .date(easyRandom.nextObject(LocalDate.class))
                .build();

        expenseRepository.insert(expense);

        assertNotNull(expense.getId());
    }
}