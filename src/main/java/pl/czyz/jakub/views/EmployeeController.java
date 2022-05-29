package pl.czyz.jakub.views;

import pl.czyz.jakub.database.DepartmentDbManager;
import pl.czyz.jakub.database.EmployeeDbManager;
import pl.czyz.jakub.models.DzialPracownikow;
import pl.czyz.jakub.models.Pracownik;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class EmployeeController {
    public boolean addEmployee(JFrame frame) {
        Pracownik employee = showCreateOrUpdateDialog(frame, null);

        if (employee == null) {
            return false;
        }

        return EmployeeDbManager.createEmployee(employee);
    }

    public boolean editEmployee(JFrame frame, JTable table) {
        int index = table.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć pracownika!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        List<Pracownik> employees = EmployeeDbManager.getEmployee();

        Pracownik employee = showCreateOrUpdateDialog(frame, employees.get(index));

        if (employee == null) {
            return false;
        }

        return EmployeeDbManager.updateEmployee(employee);
    }

    public boolean removeEmployee(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checkedEmployee = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checkedEmployee.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checkedEmployee.isEmpty()) {
            return false;
        }

        return EmployeeDbManager.removeEmployee(checkedEmployee);
    }

    private Pracownik showCreateOrUpdateDialog(JFrame frame, Pracownik initValue) {
        String initName = initValue != null ? initValue.getImie() : null;
        String initSurname = initValue != null ? initValue.getNazwisko() : null;
        LocalDate initBirthDate = initValue != null ? initValue.getDataUrodzenia() : null;
        String initBirthDateString = initBirthDate != null ? initBirthDate.toString() : null;

        AtomicReference<Pracownik> result = new AtomicReference<>();
        AtomicReference<LocalDate> date = new AtomicReference<>(initBirthDate);

        List<DzialPracownikow> departments = DepartmentDbManager.getDepartments();

        int initDepartmentIndex = 0;

        if (initValue != null) {
            Integer id = initValue.getDzialPracownikow().getId();

            OptionalInt indexOpt = IntStream.range(0, departments.size())
                    .filter(i -> id.equals(departments.get(i).getId()))
                    .findFirst();

            initDepartmentIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JTextField name = new JTextField(initName);
        JTextField surname = new JTextField(initSurname);

        JButton openCalendarButton = new JButton("\uD83D\uDCC5");
        JLabel birthDate = new JLabel(initBirthDateString);

        birthDate.setBorder(BorderFactory.createEmptyBorder());

        JPanel field = new JPanel(new BorderLayout());
        Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
        field.setBorder(loweredBevelBorder);

        field.add(birthDate, BorderLayout.LINE_START);
        field.add(openCalendarButton, BorderLayout.LINE_END);

        openCalendarButton.addActionListener(e -> {
            DatePicker dialog = new DatePicker(value -> {
                if (value != null) {
                    date.set(value);
                    birthDate.setText(value.toString());
                }
            }, initBirthDate);
            dialog.pack();
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        JComboBox<DzialPracownikow> departmentsDropdown = new JComboBox<>();
        departmentsDropdown.setModel(new DefaultComboBoxModel<>(departments.toArray(new DzialPracownikow[0])));
        departmentsDropdown.setSelectedIndex(initDepartmentIndex);

        Object[] fields = {"Imię:", name, "Nazwisko:", surname, "Data urodzenia:", field, "Dział:", departmentsDropdown};

        JButton add = new JButton(initValue != null ? "Edytuj" : "Dodaj");
        JButton cancel = new JButton("Anuluj");

        cancel.addActionListener(e -> {
            closeForm(cancel);
            result.set(null);
        });

        add.addActionListener(e -> {
            String nameText = name.getText();
            String surnameText = surname.getText();

            if (nameText == null || nameText.trim().isEmpty()) {
                showValidationError(frame, "Należy podać imię!");
                return;
            }

            if (surnameText == null || surnameText.trim().isEmpty()) {
                showValidationError(frame, "Należy podać nazwisko!");
                return;
            }

            if (date.get() == null) {
                showValidationError(frame, "Należy podać datę urodzenia!");
                return;
            }

            DzialPracownikow department = departments.get(departmentsDropdown.getSelectedIndex());

            result.set(initValue != null
                    ? new Pracownik(initValue.getId(), nameText, surnameText, date.get(), department)
                    : new Pracownik(nameText, surnameText, date.get(), department));

            closeForm(add);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                initValue != null ? "Edytuj pracownika" : "Dodaj pracownika",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new JButton[]{add, cancel},
                null);

        return result.get();
    }

    private void showValidationError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Błąd", JOptionPane.WARNING_MESSAGE);
    }

    private void closeForm(JButton sender) {
        Window w = SwingUtilities.getWindowAncestor(sender);

        if (w != null) {
            w.setVisible(false);
        }
    }
}
