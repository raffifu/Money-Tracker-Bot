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
            String sql = "INSERT INTO expense (note, amount, category, date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, expense.getNote());
                statement.setInt(2, expense.getAmount());
                statement.setString(3, expense.getCategory());
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

    @Override
    public void delete(Integer id) {
        try (Connection connection = ConnectionUtil.getDataSource().getConnection()) {
            String sql = "DELETE FROM expense WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Expense expense) {
        try (Connection connection = ConnectionUtil.getDataSource().getConnection()) {
            String sql = "UPDATE expense SET note = ?, amount = ?, category = ?, date = ? WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, expense.getNote());
                statement.setInt(2, expense.getAmount());
                statement.setString(3, expense.getCategory());
                statement.setObject(4, expense.getDate());
                statement.setLong(5, expense.getId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Expense getById(Integer id) {
        try (Connection connection = ConnectionUtil.getDataSource().getConnection()) {
            String sql = "SELECT * FROM expense WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Expense.builder()
                            .id(resultSet.getInt("id"))
                            .amount(resultSet.getInt("amount"))
                            .note(resultSet.getString("note"))
                            .category(resultSet.getString("category"))
                            .date(resultSet.getDate("date").toLocalDate())
                            .build();
                }

                return null;
            }
        } catch (SQLException e) {
            log.error("Error while inserting expense", e);
            throw new RuntimeException(e);
        }
    }
}
