package pl.czyz.jakub.models;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class Brygada {
    private int id;
    private String nazwa;
    private Brygadzista brygadzista;
    private List<Pracownik> listaPracownikow;

    public Brygada(String nazwa, Brygadzista brygadzista) {
        this.nazwa = nazwa;
        this.brygadzista = brygadzista;

        this.listaPracownikow = new ArrayList<>();
    }

    public Brygada(int id, String nazwa, Brygadzista brygadzista) {
        this(nazwa, brygadzista);

        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Brygadzista getBrygadzista() {
        return brygadzista;
    }

    public void setBrygadzista(Brygadzista brygadzista) {
        this.brygadzista = brygadzista;
    }

    public void setListaPracownikow(List<Pracownik> pracownicy) {
        this.listaPracownikow = pracownicy;
    }

    public List<Pracownik> getListaPracownikow() {
        return listaPracownikow;
    }

    public void addPracownik(Pracownik pracownik) {
        if (pracownik instanceof Uzytkownik) {
            return;
        }

        this.listaPracownikow.add(pracownik);
    }

    public int getId() {
        return id;
    }

    public static DefaultTableModel getBrigadeTableModel(List<Brygada> data) {
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
        return new Class[]{Boolean.class, Integer.class, String.class, String.class, Integer.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Id", "Nazwa", "Brygadier", "Liczba pracowników"};
    }

    private static Object[][] getDataForTable(List<Brygada> data) {
        Object[][] rows = new Object[data.size()][5];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getId();
            rows[i][2] = data.get(i).getNazwa();
            rows[i][3] = data.get(i).getBrygadzista().toString();
            rows[i][4] = data.get(i).getListaPracownikow().size();
        }

        return rows;
    }

    @Override
    public String toString() {
        return nazwa;
    }
}
