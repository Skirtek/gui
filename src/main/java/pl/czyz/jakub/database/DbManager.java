package pl.czyz.jakub.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:gui.db";

    private static Connection connection = null;

    public static boolean createConnection() {
        if (!checkDriverAvailability()) {
            return false;
        }

        try {
            connection = DriverManager.getConnection(URL);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isConnectionAvailable() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    private static boolean checkDriverAvailability() {
        try {
            Class.forName(DRIVER);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
