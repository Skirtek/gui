package pl.czyz.jakub.models;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Brygadzista extends Uzytkownik {
    private Integer id;
    private List<Brygada> brygady;

    public Brygadzista(Integer id, String imie, String nazwisko, LocalDate dataUrodzenia, DzialPracownikow dzialPracownikow, String login, String haslo) {
        super(imie, nazwisko, dataUrodzenia, dzialPracownikow, login, haslo);

        this.id = id;
        brygady = new ArrayList<>();
    }

    public Integer getBrigadeManId() {
        return id;
    }

    public List<Brygada> getBrygady() {
        return brygady;
    }

    public void addBrygada(Brygada brygada) {
        this.brygady.add(brygada);
    }

    public static DefaultTableModel getBrigadeManTableModel(List<Brygadzista> data) {
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
        return new Class[]{Boolean.class, Integer.class, String.class, String.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Id", "Imię", "Nazwisko"};
    }

    private static Object[][] getDataForTable(List<Brygadzista> data) {
        Object[][] rows = new Object[data.size()][4];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getBrigadeManId();
            rows[i][2] = data.get(i).getImie();
            rows[i][3] = data.get(i).getNazwisko();
        }

        return rows;
    }
}
