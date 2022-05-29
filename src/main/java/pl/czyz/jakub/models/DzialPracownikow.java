package pl.czyz.jakub.models;

import pl.czyz.jakub.database.DepartmentDbManager;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class DzialPracownikow {
    private Integer id;
    private String nazwa;

    public DzialPracownikow(Integer id, String nazwa) {
        this.id = id;
        this.nazwa = nazwa;
    }

    public Integer getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    @Override
    public String toString() {
        return nazwa;
    }

    public static boolean validate(String nazwa) {
        List<DzialPracownikow> departments = DepartmentDbManager.getDepartments();
        return departments.stream().noneMatch(x -> x.getNazwa().equals(nazwa));
    }

    public static DefaultTableModel getTableModel(List<DzialPracownikow> data) {
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
        return new Class[]{Boolean.class, Integer.class, String.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Id", "Nazwa działu", };
    }

    private static Object[][] getDataForTable(List<DzialPracownikow> data) {
        Object[][] rows = new Object[data.size()][3];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getId();
            rows[i][2] = data.get(i).getNazwa();
        }

        return rows;
    }
}
