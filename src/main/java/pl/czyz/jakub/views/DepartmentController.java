package pl.czyz.jakub.views;

import pl.czyz.jakub.database.DepartmentDbManager;
import pl.czyz.jakub.database.EmployeeDbManager;
import pl.czyz.jakub.models.DzialPracownikow;
import pl.czyz.jakub.models.Pracownik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class DepartmentController {
    public boolean addDepartment(JFrame frame) {
        String result = showInput(frame, "Dodawanie działu", null);

        if (result == null) {
            return false;
        }

        boolean createSucceeded = DepartmentDbManager.createDepartment(result);

        if (!createSucceeded) {
            JOptionPane.showMessageDialog(frame, "Nie udało się utworzyć działu!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean editDepartment(JFrame frame, JTable dataTable) {
        int index = dataTable.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć dział!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String name = (String) dataTable.getModel().getValueAt(index, 2);
        String result = showInput(frame, "Edycja działu", name);

        if (result == null) {
            return false;
        }

        Integer id = (Integer) dataTable.getModel().getValueAt(index, 1);
        boolean updateSucceeded = DepartmentDbManager.updateDepartment(id, result);

        if (!updateSucceeded) {
            JOptionPane.showMessageDialog(frame, "Nie udało się zaktualizować działu!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean removeDepartment(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checkedDepartments = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checkedDepartments.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checkedDepartments.isEmpty()) {
            return false;
        }

        return DepartmentDbManager.removeDepartments(checkedDepartments);
    }

    public void getEmployeesForDepartment(JFrame frame, JTable dataTable) {
        int index = dataTable.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć dział!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) dataTable.getModel().getValueAt(index, 1);
        List<Pracownik> employees = EmployeeDbManager.getEmployeeForDepartment(id);

        Object message;

        if (employees.isEmpty()) {
            message = "Brak pracowników w wybranym dziale";
        } else {
            String[] options = employees
                    .stream()
                    .map(x -> x.getImie() + " " + x.getNazwisko())
                    .toArray(String[]::new);

            message = new JList<>(options);
        }

        JOptionPane.showMessageDialog(frame, message, "Pracownicy działu", JOptionPane.INFORMATION_MESSAGE);
    }

    private String showInput(JFrame frame, String title, String defaultValue) {
        String result = (String) JOptionPane.showInputDialog(
                frame,
                "Wpisz nazwę działu",
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultValue
        );

        if (result == null) {
            return null;
        }

        result = result.trim();

        if (result.length() == 0) {
            JOptionPane.showMessageDialog(frame, "Nazwa działu nie może być pusta!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (defaultValue != null && defaultValue.equals(result)) {
            return null;
        }

        if (!DzialPracownikow.validate(result)) {
            JOptionPane.showMessageDialog(frame, "Dział o podanej nazwie już istnieje!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return result;
    }
}
