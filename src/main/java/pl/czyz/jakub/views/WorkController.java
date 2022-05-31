package pl.czyz.jakub.views;

import pl.czyz.jakub.database.WorkDbDataManager;
import pl.czyz.jakub.models.Praca;
import pl.czyz.jakub.models.RodzajPracy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class WorkController {
    public boolean addWork() {
        Praca work = createOrEditWork(null);

        if (work == null) {
            return false;
        }

        return WorkDbDataManager.createWork(work);
    }

    public boolean editWork(JFrame frame, JTable table) {
        int index = table.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć pracę!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        List<Praca> works = WorkDbDataManager.getWorks();

        Praca work = createOrEditWork(works.get(index));

        if (work == null) {
            return false;
        }

        return WorkDbDataManager.updateWork(work);
    }

    public boolean removeWork(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checkedWork = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checkedWork.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checkedWork.isEmpty()) {
            return false;
        }

        return WorkDbDataManager.removeWork(checkedWork);
    }

    private Praca createOrEditWork(Praca initValue) {
        int initWorkTime = initValue != null ? initValue.getCzasPracy() : 0;
        boolean initIsFinished = initValue != null && initValue.isCzyZrealizowane();
        String initDescription = initValue != null ? initValue.getOpis() : null;

        AtomicReference<Praca> result = new AtomicReference<>();

        int initWorkTypeIndex = 0;

        if (initValue != null) {
            RodzajPracy initialWorkType = initValue.getRodzajPracy();

            OptionalInt indexOpt = IntStream.range(0, RodzajPracy.values().length)
                    .filter(i -> initialWorkType.equals(RodzajPracy.values()[i]))
                    .findFirst();

            initWorkTypeIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JComboBox<String> workTypeDropdown = new JComboBox<>();
        workTypeDropdown.setModel(new DefaultComboBoxModel<>(Arrays.stream(RodzajPracy.values()).map(Enum::name).toArray(String[]::new)));
        workTypeDropdown.setSelectedIndex(initWorkTypeIndex);

        JSpinner workTime = new JSpinner();
        workTime.setModel(new SpinnerNumberModel(initWorkTime, 0, 1000, 1));

        JCheckBox isFinished = new JCheckBox("", initIsFinished);

        JTextArea description = new JTextArea(initDescription);
        description.setColumns(20);
        description.setRows(5);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setSize(description.getPreferredSize().width, description.getPreferredSize().height);

        Object[] fields = {
                "Rodzaj pracy:", workTypeDropdown,
                "Czas pracy", workTime,
                "Zrealizowane?", isFinished,
                "Opis:", new JScrollPane(description)
        };

        JButton add = new JButton(initValue != null ? "Edytuj" : "Dodaj");
        JButton cancel = new JButton("Anuluj");

        cancel.addActionListener(e -> {
            closeForm(cancel);
            result.set(null);
        });

        add.addActionListener(e -> {
            RodzajPracy rodzajPracy = RodzajPracy.values()[workTypeDropdown.getSelectedIndex()];
            int czasPracy = (Integer) workTime.getValue();
            boolean czyZrealizowane = isFinished.isSelected();
            String opis = description.getText();
            result.set(initValue != null
                    ? new Praca(initValue.getNumerPracy(), rodzajPracy, czasPracy, czyZrealizowane, opis)
                    : new Praca(rodzajPracy, czasPracy, czyZrealizowane, opis));

            closeForm(add);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                initValue != null ? "Edytuj pracę" : "Dodaj pracę",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new JButton[]{add, cancel},
                null);

        return result.get();
    }

    private void closeForm(JButton sender) {
        Window w = SwingUtilities.getWindowAncestor(sender);

        if (w != null) {
            w.setVisible(false);
        }
    }
}
