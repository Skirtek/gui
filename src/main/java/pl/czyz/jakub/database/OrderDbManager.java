package pl.czyz.jakub.database;

import pl.czyz.jakub.models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDbManager {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void createOrdersTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS zlecenia " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "brygadaId INTEGER NOT NULL, " +
                "stanZlecenia INTEGER NOT NULL, " +
                "dataUtworzenia TEXT NOT NULL, " +
                "dataRealizacji TEXT NULL, " +
                "dataZakonczenia TEXT NULL, " +
                "FOREIGN KEY(brygadaId) REFERENCES brygady(id))";

        DbDataManager.createTable(query);

        query = "CREATE TABLE IF NOT EXISTS praca_zlecenie " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "numerPracy INTEGER NOT NULL, " +
                "zlecenieId INTEGER NOT NULL, " +
                "FOREIGN KEY(numerPracy) REFERENCES praca(numerPracy), " +
                "FOREIGN KEY(zlecenieId) REFERENCES zlecenia(id))";

        DbDataManager.createTable(query);
    }

    public static boolean createOrder(Zlecenie order) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "INSERT INTO zlecenia (brygadaId, stanZlecenia, dataUtworzenia, dataRealizacji, dataZakonczenia) VALUES(?, ?, ?, ?, ?)";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getBrygada().getId());
            ps.setInt(2, order.getStanZlecenia().ordinal());
            ps.setString(3, dateFormatter.format(order.getDataUtworzenia()));
            ps.setString(4, order.getDataRealizacji() == null ? null : dateFormatter.format(order.getDataRealizacji()));
            ps.setString(5, order.getDataZakoczenia() == null ? null : dateFormatter.format(order.getDataZakoczenia()));

            int rows = ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                int newId = rs.getInt(1);
                createWorkOrder(connection, order.getListaPrac(), newId);
            }

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean editOrder(Zlecenie order) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE zlecenia SET brygadaId = ?, stanZlecenia = ?, dataRealizacji = ?, dataZakonczenia = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, order.getBrygada().getId());
            ps.setInt(2, order.getStanZlecenia().ordinal());
            ps.setString(3, order.getDataRealizacji() == null ? null : dateFormatter.format(order.getDataRealizacji()));
            ps.setString(4, order.getDataZakoczenia() == null ? null : dateFormatter.format(order.getDataZakoczenia()));
            ps.setInt(5, order.getId());

            int rows = ps.executeUpdate();

            removeOrderWorks(connection, order.getId());
            createWorkOrder(connection, order.getListaPrac(), order.getId());

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean removeOrders(List<Integer> orders) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = DbDataManager.in("DELETE FROM zlecenia WHERE id IN (?)", orders.size());

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 1; i <= orders.size(); i++) {
                ps.setInt(i, orders.get(i - 1));
                removeOrderWorks(connection, orders.get(i - 1));
            }

            int rows = ps.executeUpdate();

            ps.close();

            return rows == orders.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean closeOrder(Integer orderId) {
        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "UPDATE zlecenia SET stanZlecenia = ?, dataRealizacji = ?, dataZakonczenia = ? WHERE id = ?";

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, StanZlecenia.ZAKONCZENIE.ordinal());
            ps.setString(2, dateFormatter.format(LocalDateTime.now()));
            ps.setString(3, dateFormatter.format(LocalDateTime.now()));
            ps.setInt(4, orderId);

            int rows = ps.executeUpdate();

            closeOrderWorks(connection, orderId);

            ps.close();

            return rows == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<Zlecenie> getOrders(boolean onlyUnfinished) {
        List<Zlecenie> result = new ArrayList<>();

        try {
            if (!DbManager.isConnectionAvailable()) {
                throw new Exception();
            }

            String query = "SELECT brygady.nazwa, zlecenia.* " +
                    "FROM zlecenia " +
                    "JOIN brygady ON brygady.id = zlecenia.brygadaId " +
                    (onlyUnfinished ? "WHERE zlecenia.stanZlecenia <> 3" : "");

            Connection connection = DbManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String brigadeName = rs.getString("nazwa");
                StanZlecenia state = StanZlecenia.values()[rs.getInt("stanZlecenia")];
                LocalDateTime creationDate = getLocalDateTime(rs.getString("dataUtworzenia"));
                LocalDateTime realisationDate = getLocalDateTime(rs.getString("dataRealizacji"));
                LocalDateTime endDate = getLocalDateTime(rs.getString("dataZakonczenia"));

                Zlecenie order = new Zlecenie(id,
                        new Brygada(brigadeName, null),
                        state, creationDate, realisationDate, endDate);

                List<Praca> works = getWorks(connection, order.getId());
                order.setListaPrac(works);

                result.add(order);
            }

            rs.close();
            ps.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static List<Praca> getWorks(Connection connection, Integer orderId) throws SQLException {
        List<Praca> result = new ArrayList<>();

        String query = "SELECT praca.* FROM praca_zlecenie " +
                "JOIN praca ON praca.numerPracy = praca_zlecenie.numerPracy " +
                "WHERE zlecenieId = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, orderId);

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
    }

    private static LocalDateTime getLocalDateTime(String result) {
        try {
            return LocalDateTime.parse(result, dateFormatter);
        } catch (Exception exception) {
            return null;
        }
    }

    private static void createWorkOrder(Connection connection, List<Praca> works, Integer brigadeId) throws SQLException {
        for (Praca praca : works) {
            String query = "INSERT INTO praca_zlecenie(numerPracy, zlecenieId) VALUES(?,?);";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, praca.getNumerPracy());
            ps.setInt(2, brigadeId);

            ps.executeUpdate();
        }
    }

    private static void removeOrderWorks(Connection connection, Integer orderId) throws SQLException {
        String query = "DELETE FROM praca_zlecenie WHERE zlecenieId = ?;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, orderId);

        ps.executeUpdate();
    }

    private static void closeOrderWorks(Connection connection, Integer orderId) throws SQLException {
        String query = "SELECT numerPracy FROM praca_zlecenie WHERE zlecenieId = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, orderId);

        List<Integer> worksIds = new ArrayList<>();

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            worksIds.add(rs.getInt("numerPracy"));
        }

        query = DbDataManager.in("UPDATE praca SET czyZrealizowane = 1 WHERE numerPracy IN (?)", worksIds.size());

        ps = connection.prepareStatement(query);

        for (int i = 1; i <= worksIds.size(); i++) {
            ps.setInt(i, worksIds.get(i - 1));
        }

        ps.executeUpdate();
    }
}
