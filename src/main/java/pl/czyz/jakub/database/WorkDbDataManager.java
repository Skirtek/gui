package pl.czyz.jakub.database;

import pl.czyz.jakub.models.Praca;
import pl.czyz.jakub.models.RodzajPracy;
import pl.czyz.jakub.models.Uzytkownik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WorkDbDataManager {
    public static void createWorkTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS praca " +
                "(numerPracy INTEGER NOT NULL, " +
                "rodzajPracy INTEGER NOT NULL," +
                "czasPracy INTEGER NOT NULL, " +
                "czyZrealizowane BOOLEAN NOT NULL, " +
                "opis TEXT NULL)";

        DbDataManager.createTable(query);
    }

    public static boolean createWork(Praca work) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO praca (numerPracy, rodzajPracy, czasPracy, czyZrealizowane, opis) VALUES(?, ?, ?, ?, ?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, work.getNumerPracy());
            ps.setInt(2, work.getRodzajPracy().ordinal());
            ps.setInt(3, work.getCzasPracy());
            ps.setBoolean(4, work.isCzyZrealizowane());
            ps.setString(5, work.getOpis());

            int rows = ps.executeUpdate();

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            return false;
        }
    }

    public static List<Praca> getWorks() {
        List<Praca> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT * FROM praca";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer numerPracy = rs.getInt("numerPracy");
                RodzajPracy rodzajPracy = RodzajPracy.values()[rs.getInt("rodzajPracy")];
                int czasPracy = rs.getInt("czasPracy");
                boolean czyZrealizowane = rs.getBoolean("czyZrealizowane");
                String opis = rs.getString("opis");

                result.add(new Praca(numerPracy, rodzajPracy, czasPracy, czyZrealizowane, opis));
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

}
