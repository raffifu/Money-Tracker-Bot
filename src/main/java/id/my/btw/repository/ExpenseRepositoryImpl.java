package id.my.btw.repository;

import id.my.btw.entity.Expense;
import id.my.btw.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class ExpenseRepositoryImpl implements ExpenseRepository {
    @Override
    public void insert(Expense expense) {
        try (Connection connection = ConnectionUtil.getDataSource().getConnection()) {
            String sql = "INSERT INTO expense (name, amount, description, date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, expense.getName());
                statement.setInt(2, expense.getAmount());
                statement.setString(3, expense.getDescription());
                statement.setObject(4, expense.getDate());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        expense.setId(resultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense", e);
            throw new RuntimeException(e);
        }
    }
}