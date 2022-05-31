package pl.czyz.jakub.database;

import pl.czyz.jakub.models.Praca;
import pl.czyz.jakub.models.RodzajPracy;

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

    public static boolean updateWork(Praca work) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE praca SET rodzajPracy = ?, czasPracy = ?, czyZrealizowane = ?, opis = ? WHERE numerPracy = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, work.getRodzajPracy().ordinal());
            ps.setInt(2, work.getCzasPracy());
            ps.setBoolean(3, work.isCzyZrealizowane());
            ps.setString(4, work.getOpis());
            ps.setInt(5, work.getNumerPracy());

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

    public static List<Praca> getWorksForOrder(Integer orderId) {
        List<Praca> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT praca.* FROM praca " +
                    "JOIN praca_zlecenie ON praca_zlecenie.numerPracy = praca.numerPracy " +
                    "WHERE praca_zlecenie.zlecenieId = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer numerPracy = rs.getInt("numerPracy");
                RodzajPracy rodzajPracy = RodzajPracy.values()[rs.getInt("rodzajPracy")];

                result.add(new Praca(numerPracy, rodzajPracy, 0, false, null));
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static boolean removeWork(List<Integer> works) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM praca WHERE numerPracy IN (?)", works.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= works.size(); i++) {
                ps.setInt(i, works.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == works.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
