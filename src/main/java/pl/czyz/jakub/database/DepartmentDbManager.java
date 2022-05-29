package pl.czyz.jakub.database;

import pl.czyz.jakub.models.DzialPracownikow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDbManager {
    public static void createDepartmentsTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS dzialyPracownikow " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "nazwa varchar(255) NOT NULL)";

        DbDataManager.createTable(query);
    }

    public static boolean createDepartment(String name) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO dzialyPracownikow (nazwa) VALUES(?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, name);
            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<DzialPracownikow> getDepartments() {
        List<DzialPracownikow> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT * FROM dzialyPracownikow";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(new DzialPracownikow(rs.getInt("id"), rs.getString("nazwa")));
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static boolean updateDepartment(Integer id, String newName) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE dzialyPracownikow SET nazwa = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, newName);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean removeDepartments(List<Integer> departments) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM dzialyPracownikow WHERE id IN (?)", departments.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= departments.size(); i++) {
                ps.setInt(i, departments.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == departments.size();
        } catch (Exception ex) {
            return false;
        }
    }
}
