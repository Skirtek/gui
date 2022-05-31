package pl.czyz.jakub.database;

import pl.czyz.jakub.models.Brygada;
import pl.czyz.jakub.models.Brygadzista;
import pl.czyz.jakub.models.Pracownik;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrigadeDbManager {
    public static void createBrigadeTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS brygady " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "nazwa TEXT NOT NULL, " +
                "brygadzistaId INTEGER NOT NULL, " +
                "FOREIGN KEY(brygadzistaId) REFERENCES brygadzisci(id))";

        DbDataManager.createTable(query);

        query = "CREATE TABLE IF NOT EXISTS brygady_pracownicy " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "pracownikId INTEGER NOT NULL, " +
                "brygadaId INTEGER NOT NULL, " +
                "FOREIGN KEY(pracownikId) REFERENCES pracownicy(id), " +
                "FOREIGN KEY(brygadaId) REFERENCES brygady(id) ON DELETE CASCADE)";

        DbDataManager.createTable(query);
    }

    public static boolean createBrigade(Brygada brigade) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO brygady (nazwa, brygadzistaId) VALUES(?, ?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, brigade.getNazwa());
            ps.setInt(2, brigade.getBrygadzista().getBrigadeManId());

            int rows = ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                int newId = rs.getInt(1);
                createBrigadeEmployees(connection, brigade.getListaPracownikow(), newId);
            }

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    private static void createBrigadeEmployees(Connection connection, List<Pracownik> employees, Integer brigadeId) throws SQLException {
        for (Pracownik pracownik : employees) {
            String query = "INSERT INTO brygady_pracownicy(pracownikId, brygadaId) VALUES(?,?);";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, pracownik.getId());
            ps.setInt(2, brigadeId);

            ps.executeUpdate();
        }
    }

    private static void removeBrigadeEmployees(Connection connection, Integer brigadeId) throws SQLException {
        String query = "DELETE FROM brygady_pracownicy WHERE brygadaId = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, brigadeId);

        ps.executeUpdate();
    }

    public static boolean updateBrigade(Brygada brigade) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE brygady SET nazwa = ?, brygadzistaId = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, brigade.getNazwa());
            ps.setInt(2, brigade.getBrygadzista().getBrigadeManId());
            ps.setInt(3, brigade.getId());

            int rows = ps.executeUpdate();

            removeBrigadeEmployees(connection, brigade.getId());

            createBrigadeEmployees(connection, brigade.getListaPracownikow(), brigade.getId());

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<Brygada> getBrigades(Integer brigadeManId) {
        List<Brygada> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT brygady.nazwa, brygady.id as 'idBrygady', brygadzisci.id, pracownicy.imie, pracownicy.nazwisko " +
                    "FROM brygady " +
                    "JOIN brygadzisci ON brygady.brygadzistaId = brygadzisci.id " +
                    "JOIN uzytkownicy ON uzytkownicy.id = brygadzisci.uzytkownikId " +
                    "JOIN pracownicy ON  pracownicy.id = uzytkownicy.pracownikId" +
                    (brigadeManId != null ? " WHERE brygadzisci.id = ?" : "");

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            if (brigadeManId != null) {
                ps.setInt(1, brigadeManId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int brigadeId = rs.getInt("idBrygady");

                Brygadzista brigadist = new Brygadzista(
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        null, null, null, null);

                Brygada brigade = new Brygada(brigadeId, rs.getString("nazwa"), brigadist);

                List<Pracownik> employees = getEmployees(connection, brigadeId);
                brigade.setListaPracownikow(employees);

                result.add(brigade);
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean removeBrigades(List<Integer> brigade) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM brygady WHERE id IN (?)", brigade.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= brigade.size(); i++) {
                ps.setInt(i, brigade.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == brigade.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static List<Pracownik> getEmployees(Connection connection, Integer brigadeId) throws SQLException {
        List<Pracownik> result = new ArrayList<>();

        String query = "SELECT pracownicy.id, pracownicy.imie, pracownicy.nazwisko FROM brygady_pracownicy " +
                "JOIN pracownicy ON pracownicy.id = brygady_pracownicy.pracownikId " +
                "WHERE brygadaId = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, brigadeId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Integer pracownikId = rs.getInt("id");
            String imie = rs.getString("imie");
            String nazwisko = rs.getString("nazwisko");

            result.add(new Pracownik(pracownikId, imie, nazwisko, null, null));
        }

        rs.close();
        ps.close();

        return result;
    }
}
