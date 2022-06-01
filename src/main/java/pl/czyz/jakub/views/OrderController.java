package pl.czyz.jakub.views;

import pl.czyz.jakub.database.*;
import pl.czyz.jakub.models.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderController {
    public boolean addOrder(JFrame frame) {
        Zlecenie order = showCreateOrUpdateDialog(frame, null);

        if (order == null) {
            return false;
        }

        return OrderDbManager.createOrder(order);
    }

    public boolean editOrder(JFrame frame, JTable table) {
        int index = table.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć zlecenie!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        List<Zlecenie> orders = OrderDbManager.getOrders(false);

        Zlecenie order = showCreateOrUpdateDialog(frame, orders.get(index));

        if (order == null) {
            return false;
        }

        return OrderDbManager.editOrder(order);
    }

    public boolean getWorksForOrder(JFrame frame, JTable dataTable) {
        int index = dataTable.getSelectedRow();

        if (index < 0) {
            JOptionPane.showMessageDialog(frame, "Należy zaznaczyć zlecenie!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Integer id = (Integer) dataTable.getModel().getValueAt(index, 1);
        List<Praca> works = WorkDbDataManager.getWorksForOrder(id);

        Object message;

        if (works.isEmpty()) {
            message = "Brak prac powiązanych z wybranym zleceniem";
        } else {
            String[] options = works
                    .stream()
                    .map(x -> x.getNumerPracy() + ": " + x.getRodzajPracy().name())
                    .toArray(String[]::new);

            message = new JList<>(options);
        }

        AtomicReference<Boolean> result = new AtomicReference<>(false);

        Object[] fields = {"Prace powiązane:", message};

        JButton end = new JButton("Zakończ zlecenie");
        JButton close = new JButton("Zamknij okno");

        close.addActionListener(e -> closeForm(close));

        end.addActionListener(e -> {
            result.set(OrderDbManager.closeOrder(id));
            closeForm(end);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                "Szczegóły zlecenia",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new JButton[]{end, close},
                null);

        return result.get();
    }

    public boolean removeOrder(JTable dataTable) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();

        List<Integer> checkedOrders = new ArrayList<>();

        for (int i = 0; model.getRowCount() > i; i++) {
            boolean isChecked = (boolean) model.getValueAt(i, 0);

            if (isChecked) {
                checkedOrders.add((Integer) model.getValueAt(i, 1));
            }
        }

        if (checkedOrders.isEmpty()) {
            return false;
        }

        return OrderDbManager.removeOrders(checkedOrders);
    }

    private Zlecenie showCreateOrUpdateDialog(JFrame frame, Zlecenie initValue) {
        String initialRealisationDateText = initValue != null && initValue.getDataRealizacji() != null
                ? initValue.getDataRealizacji().toString() : null;
        String initialEndDateText = initValue != null && initValue.getDataZakoczenia() != null
                ? initValue.getDataZakoczenia().toString() : null;

        LocalDateTime initialRealisationDate = initValue != null ? initValue.getDataRealizacji() : null;
        LocalDateTime initialEndDate = initValue != null ? initValue.getDataZakoczenia() : null;

        AtomicReference<Zlecenie> result = new AtomicReference<>();
        AtomicReference<LocalDateTime> realisationDate = new AtomicReference<>(initialRealisationDate);
        AtomicReference<LocalDateTime> endDate = new AtomicReference<>(initialEndDate);

        List<Brygada> brigades = BrigadeDbManager.getBrigades(null);
        List<Praca> works = WorkDbDataManager.getWorks();

        int brigadeInitIndex = 0;

        if (initValue != null) {
            Integer id = initValue.getBrygada().getId();

            OptionalInt indexOpt = IntStream.range(0, brigades.size())
                    .filter(i -> id.equals(brigades.get(i).getId()))
                    .findFirst();

            brigadeInitIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JComboBox<Brygada> brigadeDropdown = new JComboBox<>();
        brigadeDropdown.setModel(new DefaultComboBoxModel<>(brigades.toArray(new Brygada[0])));
        brigadeDropdown.setSelectedIndex(brigadeInitIndex);

        int initStateIndex = 0;

        if (initValue != null) {
            StanZlecenia initState = initValue.getStanZlecenia();

            OptionalInt indexOpt = IntStream.range(0, StanZlecenia.values().length)
                    .filter(i -> initState.equals(StanZlecenia.values()[i]))
                    .findFirst();

            initStateIndex = indexOpt.isPresent() ? indexOpt.getAsInt() : 0;
        }

        JComboBox<String> stateDropdown = new JComboBox<>();
        stateDropdown.setModel(new DefaultComboBoxModel<>(Arrays.stream(StanZlecenia.values()).map(Enum::name).toArray(String[]::new)));
        stateDropdown.setSelectedIndex(initStateIndex);

        JList<Praca> worksList = new JList<>();

        DefaultListModel<Praca> model = new DefaultListModel<>();

        for (Praca work : works) {
            model.addElement(work);
        }

        worksList.setModel(model);

        if (initValue != null) {
            List<Integer> worksIds = initValue.getListaPrac()
                    .stream().map(Praca::getNumerPracy).collect(Collectors.toList());

            int[] indexes = IntStream.range(0, works.size())
                    .filter(i -> worksIds.contains(works.get(i).getNumerPracy()))
                    .toArray();

            for (int index : indexes) {
                worksList.getSelectionModel().addSelectionInterval(index, index);
            }
        }

        JPanel realisationDatePanel = getDateTimeButton(initialRealisationDateText, initialRealisationDate, realisationDate);
        JPanel endDatePanel = getDateTimeButton(initialEndDateText, initialEndDate, endDate);

        Object[] fields = {
                "Brygada:", brigadeDropdown,
                "Stan zlecenia:", stateDropdown,
                "Data realizacji", realisationDatePanel,
                "Data zakończenia", endDatePanel,
                "Powiązana praca", worksList
        };

        JButton add = new JButton(initValue != null ? "Edytuj" : "Dodaj");
        JButton cancel = new JButton("Anuluj");

        cancel.addActionListener(e -> {
            closeForm(cancel);
            result.set(null);
        });

        add.addActionListener(e -> {
            List<Praca> selectedWorks = worksList.getSelectedValuesList();

            if (selectedWorks.isEmpty()) {
                showValidationError(frame, "Należy wybrać co najmniej jedną pracę!");
                return;
            }

            Brygada brygada = brigades.get(brigadeDropdown.getSelectedIndex());
            StanZlecenia stanZlecenia = StanZlecenia.values()[stateDropdown.getSelectedIndex()];

            Zlecenie order = initValue != null
                    ? new Zlecenie(initValue.getId(), brygada, stanZlecenia, initValue.getDataUtworzenia(), realisationDate.get(), endDate.get())
                    : new Zlecenie(brygada, stanZlecenia, realisationDate.get(), endDate.get());

            order.setListaPrac(selectedWorks);

            result.set(order);

            closeForm(add);
        });

        JOptionPane.showOptionDialog(
                null,
                fields,
                initValue != null ? "Edytuj zlecenie" : "Dodaj zlecenie",
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

    private JPanel getDateTimeButton(String initString, LocalDateTime initDateTime, AtomicReference<LocalDateTime> dateTimeRef) {
        JButton openCalendarButton = new JButton("\uD83D\uDD52");
        JLabel label = new JLabel(initString);

        label.setBorder(BorderFactory.createEmptyBorder());

        JPanel field = new JPanel(new BorderLayout());
        Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
        field.setBorder(loweredBevelBorder);

        field.add(label, BorderLayout.LINE_START);
        field.add(openCalendarButton, BorderLayout.LINE_END);

        openCalendarButton.addActionListener(e -> {
            DateTimePicker dialog = new DateTimePicker(value -> {
                if (value != null) {
                    dateTimeRef.set(value);
                    label.setText(value.toString());
                }
            }, initDateTime);
            dialog.pack();
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        return field;
    }
}
