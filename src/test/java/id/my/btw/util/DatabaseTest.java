package id.my.btw.util;

import id.btw.util.ConnectionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    @Test
    void testConnection() {
        try (Connection connection = ConnectionUtil.getDataSource().getConnection()) {
            System.out.println("Database connected");
        } catch (SQLException e) {
            Assertions.fail(e);
        }
    }
}
