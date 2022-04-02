package id.my.btw.repository;

import id.my.btw.entity.Expense;

public interface ExpenseRepository {
    void insert(Expense expense);

    void delete(Integer id);

    void update(Expense expense);

    Expense getById(Integer id);
}
