package pl.czyz.jakub.database;

import pl.czyz.jakub.models.Brygadzista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BrigadeManDbManager {
    public static void createBrigadeManTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS brygadzisci " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "uzytkownikId INTEGER NOT NULL, " +
                "FOREIGN KEY(uzytkownikId) REFERENCES uzytkownicy(id))";

        DbDataManager.createTable(query);
    }

    public static boolean createBrigadeMan(Integer userId) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO brygadzisci (uzytkownikId) VALUES(?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean updateBrigadeMan(Integer brigadeManId, Integer userId) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE brygadzisci SET uzytkownikId = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, userId);
            ps.setInt(2, brigadeManId);

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<Brygadzista> getBrigadeMan() {
        List<Brygadzista> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT brygadzisci.id, pracownicy.imie, pracownicy.nazwisko, uzytkownicy.id as 'userId'" +
                    "FROM brygadzisci " +
                    "JOIN uzytkownicy ON uzytkownicy.id = brygadzisci.uzytkownikId " +
                    "JOIN pracownicy ON  pracownicy.id = uzytkownicy.pracownikId";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer userId = rs.getInt("userId");
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");

                Brygadzista entity = new Brygadzista(id, imie, nazwisko, null, null, null, null);
                entity.setUserId(userId);

                result.add(entity);
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static boolean removeBrigadeMan(List<Integer> brigadeMan) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM brygadzisci WHERE id IN (?)", brigadeMan.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= brigadeMan.size(); i++) {
                ps.setInt(i, brigadeMan.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == brigadeMan.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
