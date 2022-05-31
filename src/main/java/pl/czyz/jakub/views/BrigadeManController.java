package pl.czyz.jakub.views;

import pl.czyz.jakub.database.BrigadeDbManager;
import pl.czyz.jakub.database.BrigadeManDbManager;
import pl.czyz.jakub.database.UsersDbManager;
import pl.czyz.jakub.models.Brygada;
import pl.czyz.jakub.models.Brygadzista;
import pl.czyz.jakub.models.Uzytkownik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class BrigadeManController {
    public boolean addBrigadeMan() {
        Integer userId = showCreateOrUpdateDialog(null);

        if (userId == null) {
            return false;
        }

        return BrigadeManDbManager.createBrigadeMan(userId);
    }

    public void getAssignedBrigades(JFrame frame, JTable dataTable) {
        int index = dataTable.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć brygadzistę!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) dataTable.getModel().getValueAt(index, 1);
        List<Brygada> brigades = BrigadeDbManager.getBrigades(id);

        Object message;

        if (brigades.isEmpty()) {
            message = "Brak brygad powiązanych z wybranym brygadzistą";
        } else {
            String[] options = brigades
                    .stream()
                    .map(Brygada::getNazwa)
                    .toArray(String[]::new);

            message = new JList<>(options);
        }

        JOptionPane.showMessageDialog(frame, message, "Brygady", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean updateBrigadeMan(JFrame frame, JTable table) {
        int index = table.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć brygadzistę!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        List<Brygadzista> brigadeMan = BrigadeManDbManager.getBrigadeMan();

        Brygadzista brigadeMen = brigadeMan.get(index);

        Integer userId = showCreateOrUpdateDialog(brigadeMen.getUserId());

        if (userId == null || userId.equals(brigadeMen.getUserId())) {
            return false;
        }

        return BrigadeManDbManager.updateBrigadeMan(brigadeMen.getBrigadeManId(), userId);
    }

    public boolean removeBrigadeMan(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checked = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checked.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checked.isEmpty()) {
            return false;
        }

        return BrigadeManDbManager.removeBrigadeMan(checked);
    }

    private Integer showCreateOrUpdateDialog(Integer initValue) {
        List<Uzytkownik> users = UsersDbManager.getUsers();

        int initIndex = 0;

        if (initValue != null) {
            OptionalInt indexOpt = IntStream.range(0, users.size())
                    .filter(i -> initValue.equals(users.get(i).getUserId()))
                    .findFirst();

            initIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JComboBox<Uzytkownik> usersDropdown = new JComboBox<>();
        usersDropdown.setModel(new DefaultComboBoxModel<>(users.toArray(new Uzytkownik[0])));
        usersDropdown.setSelectedIndex(initIndex);

        Object[] fields = {"Użytkownicy:", usersDropdown};

        int option = JOptionPane.showOptionDialog(
                null,
                fields,
                initValue != null ? "Edytuj brygadzistę" : "Dodaj brygadzistę",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{initValue != null ? "Edytuj" : "Dodaj", "Anuluj"},
                null);

        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        return users.get(usersDropdown.getSelectedIndex()).getUserId();
    }
}
