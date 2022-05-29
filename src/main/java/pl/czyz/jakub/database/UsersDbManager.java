package pl.czyz.jakub.database;

import pl.czyz.jakub.models.Uzytkownik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsersDbManager {
    public static void createUsersTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS uzytkownicy " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "login varchar(255) NOT NULL, " +
                "haslo varchar(255) NOT NULL," +
                "pracownikId INTEGER NOT NULL, " +
                "FOREIGN KEY(pracownikId) REFERENCES pracownicy(id))";

        DbDataManager.createTable(query);
    }

    public static boolean createUser(Uzytkownik user) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO uzytkownicy (login, haslo, pracownikId) VALUES(?, ?, ?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getHaslo());
            ps.setInt(3, user.getId());

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean updateUser(Uzytkownik user) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE uzytkownicy SET login = ?, haslo = ?, pracownikId = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getHaslo());
            ps.setInt(3, user.getId());
            ps.setInt(4, user.getUserId());

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<Uzytkownik> getUsers() {
        List<Uzytkownik> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT * FROM uzytkownicy";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String login = rs.getString("login");
                String haslo = rs.getString("haslo");

                result.add(new Uzytkownik(id, login, haslo));
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static Uzytkownik getUser(String login, String password) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT * FROM uzytkownicy " +
                    "JOIN pracownicy ON uzytkownicy.pracownikId = pracownicy.id " +
                    "WHERE login = ? AND haslo = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, login);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            Uzytkownik uzytkownik = null;

            if (rs.next()) {
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                uzytkownik = new Uzytkownik(imie, nazwisko, LocalDate.now(), null, login, password);
            }

            rs.close();
            ps.close();

            return uzytkownik;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean removeUsers(List<Integer> users) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM uzytkownicy WHERE id IN (?)", users.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= users.size(); i++) {
                ps.setInt(i, users.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == users.size();
        } catch (Exception ex) {
            return false;
        }
    }
}
