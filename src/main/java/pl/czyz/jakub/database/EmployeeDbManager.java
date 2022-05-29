package pl.czyz.jakub.database;

import pl.czyz.jakub.models.DzialPracownikow;
import pl.czyz.jakub.models.Pracownik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDbManager {
    private static final DateTimeFormatter birthDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean createEmployee(Pracownik employee) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO pracownicy (imie, nazwisko, dataUrodzenia, dzialId) VALUES(?, ?, ?, ?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, employee.getImie());
            ps.setString(2, employee.getNazwisko());
            ps.setString(3, birthDateFormatter.format(employee.getDataUrodzenia()));
            ps.setInt(4, employee.getDzialPracownikow().getId());

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean updateEmployee(Pracownik employee) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE pracownicy SET imie = ?, nazwisko = ?, dataUrodzenia = ?, dzialId = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, employee.getImie());
            ps.setString(2, employee.getNazwisko());
            ps.setString(3, birthDateFormatter.format(employee.getDataUrodzenia()));
            ps.setInt(4, employee.getDzialPracownikow().getId());
            ps.setInt(5, employee.getId());

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<Pracownik> getEmployeeForDepartment(Integer id) {
        List<Pracownik> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT pracownicy.imie, pracownicy.nazwisko FROM pracownicy " +
                    "JOIN dzialyPracownikow on dzialyPracownikow.id = pracownicy.dzialId " +
                    "WHERE dzialyPracownikow.id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");

                result.add(new Pracownik(imie, nazwisko));
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static List<Pracownik> getEmployee() {
        List<Pracownik> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT pracownicy.*, dzialyPracownikow.nazwa FROM pracownicy " +
                    "JOIN dzialyPracownikow on dzialyPracownikow.id = pracownicy.dzialId";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                LocalDate dataUrodzenia = LocalDate.parse(rs.getString("dataUrodzenia"), birthDateFormatter);
                Integer idDzialu = rs.getInt("dzialId");
                String nazwaDzialu = rs.getString("nazwa");

                result.add(new Pracownik(id, imie, nazwisko, dataUrodzenia, new DzialPracownikow(idDzialu, nazwaDzialu)));
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static boolean removeEmployee(List<Integer> employee) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM pracownicy WHERE id IN (?)", employee.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= employee.size(); i++) {
                ps.setInt(i, employee.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == employee.size();
        } catch (Exception ex) {
            return false;
        }
    }

    public static void createEmployeeTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS pracownicy " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "imie varchar(255) NOT NULL, " +
                "nazwisko varchar(255) NOT NULL, " +
                "dataUrodzenia TEXT NOT NULL, " +
                "dzialId INTEGER NOT NULL, " +
                "FOREIGN KEY(dzialId) REFERENCES dzialyPracownikow(id))";

        DbDataManager.createTable(query);
    }
}
