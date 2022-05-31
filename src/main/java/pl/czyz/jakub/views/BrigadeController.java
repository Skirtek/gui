package pl.czyz.jakub.views;

import pl.czyz.jakub.database.BrigadeDbManager;
import pl.czyz.jakub.database.BrigadeManDbManager;
import pl.czyz.jakub.database.EmployeeDbManager;
import pl.czyz.jakub.database.WorkDbDataManager;
import pl.czyz.jakub.models.Brygada;
import pl.czyz.jakub.models.Brygadzista;
import pl.czyz.jakub.models.Praca;
import pl.czyz.jakub.models.Pracownik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BrigadeController {
    public boolean addBrigade(JFrame frame) {
        Brygada brigade = showCreateOrUpdateDialog(frame, null);

        if (brigade == null) {
            return false;
        }

        return BrigadeDbManager.createBrigade(brigade);
    }

    public boolean editBrigade(JFrame frame, JTable table) {
        int index = table.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć brygadę!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        List<Brygada> brigades = BrigadeDbManager.getBrigades(null);

        Brygada brigade = showCreateOrUpdateDialog(frame, brigades.get(index));

        if (brigade == null) {
            return false;
        }

        return BrigadeDbManager.updateBrigade(brigade);
    }

    public boolean removeBrigades(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checkedBrigade = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checkedBrigade.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checkedBrigade.isEmpty()) {
            return false;
        }

        return BrigadeDbManager.removeBrigades(checkedBrigade);
    }

    public void getAssignedEmployees(JFrame frame, JTable dataTable) {
        int index = dataTable.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć brygadę!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) dataTable.getModel().getValueAt(index, 1);
        List<Pracownik> works = EmployeeDbManager.getEmployeeOfBrigade(id);

        Object message;

        if (works.isEmpty()) {
            message = "Brak pracowników powiązanych z wybranym zleceniem";
        } else {
            String[] options = works
                    .stream()
                    .map(x -> x.getImie() + " " + x.getNazwisko())
                    .toArray(String[]::new);

            message = new JList<>(options);
        }

        JOptionPane.showMessageDialog(frame, message, "Pracownicy", JOptionPane.INFORMATION_MESSAGE);
    }

    private Brygada showCreateOrUpdateDialog(JFrame frame, Brygada initValue) {
        String initName = initValue != null ? initValue.getNazwa() : null;

        JTextField name = new JTextField(initName);

        List<Brygadzista> brigadeMen = BrigadeManDbManager.getBrigadeMan();
        List<Pracownik> employees = EmployeeDbManager.getEmployeeForBrigade();

        AtomicReference<Brygada> result = new AtomicReference<>();

        int initIndex = 0;

        if (initValue != null) {
            Integer id = initValue.getBrygadzista().getBrigadeManId();

            OptionalInt indexOpt = IntStream.range(0, brigadeMen.size())
                    .filter(i -> id.equals(brigadeMen.get(i).getBrigadeManId()))
                    .findFirst();

            initIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JComboBox<Brygadzista> brigadeMenDropdown = new JComboBox<>();
        brigadeMenDropdown.setModel(new DefaultComboBoxModel<>(brigadeMen.toArray(new Brygadzista[0])));
        brigadeMenDropdown.setSelectedIndex(initIndex);

        JList<Pracownik> employeeList = new JList<>();

        DefaultListModel<Pracownik> model = new DefaultListModel<>();

        for (Pracownik employee : employees) {
            model.addElement(employee);
        }

        employeeList.setModel(model);

        if (initValue != null) {
            List<Integer> employeesIds = initValue.getListaPracownikow()
                    .stream().map(Pracownik::getId).collect(Collectors.toList());

            int[] indexes = IntStream.range(0, employees.size())
                    .filter(i -> employeesIds.contains(employees.get(i).getId()))
                    .toArray();

            for (int index : indexes) {
                employeeList.getSelectionModel().addSelectionInterval(index, index);
            }
        }

        Object[] fields = {"Nazwa:", name, "Brygadzista:", brigadeMenDropdown, "Pracownicy", employeeList};

        JButton add = new JButton(initValue != null ? "Edytuj" : "Dodaj");
        JButton cancel = new JButton("Anuluj");

        cancel.addActionListener(e -> {
            closeForm(cancel);
            result.set(null);
        });

        add.addActionListener(e -> {
            String nameText = name.getText();

            if (nameText == null || nameText.trim().isEmpty()) {
                showValidationError(frame, "Należy podać nazwę!");
                return;
            }

            List<Pracownik> selectedEmployees = employeeList.getSelectedValuesList();

            if (selectedEmployees.isEmpty()) {
                showValidationError(frame, "Należy wybrać co najmniej jednego pracownika!");
                return;
            }

            Brygadzista selectedBrigadeMan = brigadeMen.get(brigadeMenDropdown.getSelectedIndex());

            Brygada brigade = initValue != null
                    ? new Brygada(initValue.getId(), nameText, selectedBrigadeMan)
                    : new Brygada(nameText, selectedBrigadeMan);

            brigade.setListaPracownikow(selectedEmployees);

            result.set(brigade);

            closeForm(add);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                initValue != null ? "Edytuj brygadę" : "Dodaj brygadę",
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
