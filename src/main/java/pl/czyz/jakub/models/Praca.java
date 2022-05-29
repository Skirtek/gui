package pl.czyz.jakub.models;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Praca {
    private int numerPracy;
    private RodzajPracy rodzajPracy;
    private int czasPracy;
    private boolean czyZrealizowane;
    private String opis;

    public Praca(Integer numerPracy, RodzajPracy rodzajPracy, int czasPracy, boolean czyZrealizowane, String opis) {
        this.numerPracy = numerPracy;
        this.rodzajPracy = rodzajPracy;
        this.czasPracy = czasPracy;
        this.czyZrealizowane = czyZrealizowane;
        this.opis = opis;
    }

    public Praca(RodzajPracy rodzajPracy, int czasPracy, boolean czyZrealizowane, String opis) {
        this.numerPracy = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        this.rodzajPracy = rodzajPracy;
        this.czasPracy = czasPracy;
        this.czyZrealizowane = czyZrealizowane;
        this.opis = opis;
    }

    public int getNumerPracy() {
        return numerPracy;
    }

    public RodzajPracy getRodzajPracy() {
        return rodzajPracy;
    }

    public int getCzasPracy() {
        return czasPracy;
    }

    public boolean isCzyZrealizowane() {
        return czyZrealizowane;
    }

    public String getOpis() {
        return opis;
    }

    public static DefaultTableModel getTableModel(List<Praca> data) {
        return new DefaultTableModel(getDataForTable(data), getColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getColumnTypes()[columnIndex];
            }
        };
    }

    private static Class[] getColumnTypes() {
        return new Class[]{Boolean.class, Integer.class, String.class, Integer.class, String.class, String.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Numer pracy", "Rodzaj pracy", "Czas pracy", "Zrealizowane", "Opis"};
    }

    private static Object[][] getDataForTable(List<Praca> data) {
        Object[][] rows = new Object[data.size()][6];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getNumerPracy();
            rows[i][2] = data.get(i).getRodzajPracy().name();
            rows[i][3] = data.get(i).getCzasPracy();
            rows[i][4] = data.get(i).isCzyZrealizowane() ? "Tak" : "Nie";
            rows[i][5] = data.get(i).getOpis();
        }

        return rows;
    }
}
