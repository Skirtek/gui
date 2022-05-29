package pl.czyz.jakub.models;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class Pracownik {
    private Integer id;
    private String imie;
    private String nazwisko;
    private LocalDate dataUrodzenia;
    private DzialPracownikow dzialPracownikow;

    public Pracownik() {
    }

    public Pracownik(String imie, String nazwisko) {
        this.imie = imie;
        this.nazwisko = nazwisko;
    }

    public Pracownik(String imie, String nazwisko, LocalDate dataUrodzenia, DzialPracownikow dzialPracownikow) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.dataUrodzenia = dataUrodzenia;
        this.dzialPracownikow = dzialPracownikow;
    }

    public Pracownik(Integer id, String imie, String nazwisko, LocalDate dataUrodzenia, DzialPracownikow dzialPracownikow) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.dataUrodzenia = dataUrodzenia;
        this.dzialPracownikow = dzialPracownikow;
    }

    public Integer getId() {
        return id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public LocalDate getDataUrodzenia() {
        return dataUrodzenia;
    }

    public DzialPracownikow getDzialPracownikow() {
        return dzialPracownikow;
    }

    public static DefaultTableModel getTableModel(List<Pracownik> data) {
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
        return new Class[]{Boolean.class, Integer.class, String.class, String.class, LocalDate.class, String.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Id", "Imię", "Nazwisko", "Data urodzenia", "Nazwa działu"};
    }

    private static Object[][] getDataForTable(List<Pracownik> data) {
        Object[][] rows = new Object[data.size()][6];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getId();
            rows[i][2] = data.get(i).getImie();
            rows[i][3] = data.get(i).getNazwisko();
            rows[i][4] = data.get(i).getDataUrodzenia();
            rows[i][5] = data.get(i).getDzialPracownikow().getNazwa();
        }

        return rows;
    }

    @Override
    public String toString() {
        return getImie() + " " + getNazwisko();
    }
}
