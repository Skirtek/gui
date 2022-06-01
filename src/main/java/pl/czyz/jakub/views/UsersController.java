package pl.czyz.jakub.views;

import pl.czyz.jakub.database.EmployeeDbManager;
import pl.czyz.jakub.database.UsersDbManager;
import pl.czyz.jakub.models.Pracownik;
import pl.czyz.jakub.models.Uzytkownik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class UsersController {
    public boolean addUser(JFrame frame) {
        Uzytkownik user = createOrEditUser(frame, null);

        if (user == null) {
            return false;
        }

        return UsersDbManager.createUser(user);
    }

    public boolean changePassword(JFrame frame, Integer userId) {
        JTextField password = new JPasswordField();
        JTextField repeatedPassword = new JPasswordField();

        AtomicReference<Boolean> result = new AtomicReference<>(false);

        Object[] fields = {"Nowe hasło:", password, "Powtórz nowe hasło:", repeatedPassword};

        JButton change = new JButton("Zmień");
        JButton cancel = new JButton("Anuluj");

        cancel.addActionListener(e -> {
            result.set(false);
            closeForm(cancel);
        });

        change.addActionListener(e -> {
            if (password.getText().trim().isEmpty()) {
                showValidationError(frame, "Hasło nie może być puste");
                return;
            }

            if (!password.getText().equals(repeatedPassword.getText())) {
                showValidationError(frame, "Hasła się nie zgadzają");
                return;
            }

            result.set(UsersDbManager.changePassword(userId, password.getText()));
            closeForm(change);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                "Ustaw nowe hasło",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new JButton[]{change, cancel},
                null);

        return result.get();
    }

    public boolean editUser(JFrame frame, JTable table) {
        int index = table.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć użytkownika!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        List<Uzytkownik> users = UsersDbManager.getUsers();

        Uzytkownik user = createOrEditUser(frame, users.get(index));

        if (user == null) {
            return false;
        }

        return UsersDbManager.updateUser(user);
    }

    public boolean removeUsers(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checkedUsers = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checkedUsers.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checkedUsers.isEmpty()) {
            return false;
        }

        return UsersDbManager.removeUsers(checkedUsers);
    }

    private Uzytkownik createOrEditUser(JFrame frame, Uzytkownik initValue) {
        String initLogin = initValue != null ? initValue.getLogin() : null;
        String initPassword = initValue != null ? initValue.getHaslo() : null;

        List<Pracownik> employees = EmployeeDbManager.getEmployee();

        JTextField login = new JTextField(initLogin);
        JTextField password = new JPasswordField(initPassword);

        int initEmployeeIndex = 0;

        if (initValue != null) {
            Integer id = initValue.getId();

            OptionalInt indexOpt = IntStream.range(0, employees.size())
                    .filter(i -> id.equals(employees.get(i).getId()))
                    .findFirst();

            initEmployeeIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JComboBox<Pracownik> employeesDropdown = new JComboBox<>();
        employeesDropdown.setModel(new DefaultComboBoxModel<>(employees.toArray(new Pracownik[0])));
        employeesDropdown.setSelectedIndex(initEmployeeIndex);

        AtomicReference<Uzytkownik> result = new AtomicReference<>();

        Object[] fields = {"Login:", login, "Hasło:", password, "Pracownik:", employeesDropdown};

        JButton add = new JButton(initValue != null ? "Edytuj" : "Dodaj");
        JButton cancel = new JButton("Anuluj");

        cancel.addActionListener(e -> {
            closeForm(cancel);
            result.set(null);
        });

        add.addActionListener(e -> {
            String loginText = login.getText();
            String passwordText = password.getText();

            if (loginText == null || loginText.trim().isEmpty()) {
                showValidationError(frame, "Należy podać login!");
                return;
            }

            if (passwordText == null || passwordText.trim().isEmpty()) {
                showValidationError(frame, "Należy podać hasło!");
                return;
            }

            Pracownik employee = employees.get(employeesDropdown.getSelectedIndex());

            result.set(new Uzytkownik(employee, loginText, passwordText));

            closeForm(add);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                initValue != null ? "Edytuj użytkownika" : "Dodaj użytkownika",
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
