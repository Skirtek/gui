package pl.czyz.jakub.views;

import pl.czyz.jakub.database.WorkDbDataManager;
import pl.czyz.jakub.models.DzialPracownikow;
import pl.czyz.jakub.models.Praca;
import pl.czyz.jakub.models.Pracownik;
import pl.czyz.jakub.models.RodzajPracy;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class WorkController {
    public boolean addUser(JFrame frame) {
        Praca work = createOrEditWork(frame, null);

        if (work == null) {
            return false;
        }

        return WorkDbDataManager.createWork(work);
    }

    private Praca createOrEditWork(JFrame frame, Praca initValue) {
        AtomicReference<Praca> result = new AtomicReference<>();

        JComboBox<String> workTypeDropdown = new JComboBox<>();
        workTypeDropdown.setModel(new DefaultComboBoxModel<>(Arrays.stream(RodzajPracy.values()).map(Enum::name).toArray(String[]::new)));

        JSpinner workTime = new JSpinner();
        workTime.setModel(new SpinnerNumberModel(0, 0, 1000, 1));

        JCheckBox isFinished = new JCheckBox();

        JTextArea description = new JTextArea();
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
            result.set(new Praca(rodzajPracy, czasPracy, czyZrealizowane, opis));

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
