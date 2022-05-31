package pl.czyz.jakub.models;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.util.List;

public class Zlecenie {
    private Integer id;
    private List<Praca> listaPrac;
    private Brygada brygada;
    private StanZlecenia stanZlecenia;
    private LocalDateTime dataUtworzenia;
    private LocalDateTime dataRealizacji;
    private LocalDateTime dataZakoczenia;

    public Zlecenie(Integer id, Brygada brygada, StanZlecenia stanZlecenia, LocalDateTime dataUtworzenia, LocalDateTime dataRealizacji, LocalDateTime dataZakoczenia) {
        this.id = id;
        this.brygada = brygada;
        this.stanZlecenia = stanZlecenia;
        this.dataUtworzenia = dataUtworzenia;
        this.dataRealizacji = dataRealizacji;
        this.dataZakoczenia = dataZakoczenia;
    }

    public Zlecenie(Brygada brygada, StanZlecenia stanZlecenia, LocalDateTime dataRealizacji, LocalDateTime dataZakoczenia) {
        this.brygada = brygada;
        this.stanZlecenia = stanZlecenia;
        this.dataUtworzenia = LocalDateTime.now();
        this.dataRealizacji = dataRealizacji;
        this.dataZakoczenia = dataZakoczenia;
    }

    public List<Praca> getListaPrac() {
        return listaPrac;
    }

    public void setListaPrac(List<Praca> listaPrac) {
        this.listaPrac = listaPrac;
    }

    public Brygada getBrygada() {
        return brygada;
    }

    public void setBrygada(Brygada brygada) {
        this.brygada = brygada;
    }

    public StanZlecenia getStanZlecenia() {
        return stanZlecenia;
    }

    public void setStanZlecenia(StanZlecenia stanZlecenia) {
        this.stanZlecenia = stanZlecenia;
    }

    public LocalDateTime getDataUtworzenia() {
        return dataUtworzenia;
    }

    public LocalDateTime getDataRealizacji() {
        return dataRealizacji;
    }

    public void setDataRealizacji(LocalDateTime dataRealizacji) {
        this.dataRealizacji = dataRealizacji;
    }

    public LocalDateTime getDataZakoczenia() {
        return dataZakoczenia;
    }

    public void setDataZakoczenia(LocalDateTime dataZakoczenia) {
        this.dataZakoczenia = dataZakoczenia;
    }

    public Integer getId() {
        return id;
    }

    public static DefaultTableModel getTableModel(List<Zlecenie> data) {
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
        return new Class[]{Boolean.class, Integer.class, String.class, String.class, String.class, String.class, String.class, Integer.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Id", "Nazwa brygady", "Stan zlecenia", "Data utworzenia", "Data realizacji", "Data zakończenia", "Liczba prac"};
    }

    private static Object[][] getDataForTable(List<Zlecenie> data) {
        Object[][] rows = new Object[data.size()][8];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getId();
            rows[i][2] = data.get(i).getBrygada().getNazwa();
            rows[i][3] = data.get(i).getStanZlecenia().name();
            rows[i][4] = data.get(i).getDataUtworzenia().toString();
            rows[i][5] = data.get(i).getDataRealizacji() == null ? null : data.get(i).getDataRealizacji().toString();
            rows[i][6] = data.get(i).getDataZakoczenia() == null ? null : data.get(i).getDataZakoczenia().toString();
            rows[i][7] = data.get(i).getListaPrac().size();
        }

        return rows;
    }
}
