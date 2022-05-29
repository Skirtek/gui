package pl.czyz.jakub.database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collections;

public class DbDataManager {
    public static boolean createTables() {
        try {
            DepartmentDbManager.createDepartmentsTable();
            EmployeeDbManager.createEmployeeTable();
            UsersDbManager.createUsersTable();
            WorkDbDataManager.createWorkTable();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void createTable(String query) throws Exception {
        if (!DbManager.isConnectionAvailable()) {
            throw new Exception();
        }

        Connection connection = DbManager.getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    public static String in(String sql, int params) {
        final StringBuilder sb = new StringBuilder(String.join(", ", Collections.nCopies(params, "?")));

        if (sb.length() > 1) {
            sql = sql.replace("(?)", "(" + sb + ")");
        }

        return sql;
    }
}
