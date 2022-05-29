package pl.czyz.jakub.views;

import javax.swing.*;
import java.awt.event.*;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class DatePicker extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox<Integer> dayBox;
    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;
    private JButton buttonCancel;
    private Consumer<LocalDate> callback;

    public DatePicker(Consumer<LocalDate> callback, LocalDate initialValue) {
        this.callback = callback;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        int year = Calendar.getInstance().get(Calendar.YEAR);

        yearBox.setModel(new DefaultComboBoxModel<>(IntStream.range(1900, year + 1).boxed().toArray(Integer[]::new)));
        monthBox.setModel(new DefaultComboBoxModel<>(getMonths()));

        yearBox.addActionListener(e -> updateDays());
        monthBox.addActionListener(e -> updateDays());

        if (initialValue != null) {
            monthBox.setSelectedIndex(initialValue.getMonthValue() - 1);
            yearBox.setSelectedItem(initialValue.getYear());
        }

        updateDays();

        if (initialValue != null) {
            dayBox.setSelectedItem(initialValue.getDayOfMonth());
        }

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void updateDays() {
        YearMonth yearMonthObject = YearMonth.of(1900 + yearBox.getSelectedIndex(), monthBox.getSelectedIndex() + 1);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        dayBox.setModel(new DefaultComboBoxModel<>(IntStream.range(1, daysInMonth + 1).boxed().toArray(Integer[]::new)));
    }

    public String[] getMonths() {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        String[] names = symbols.getMonths();
        return Arrays.copyOf(names, names.length - 1);
    }

    private void onOK() {
        callback.accept(LocalDate.of(
                1900 + yearBox.getSelectedIndex(),
                monthBox.getSelectedIndex() + 1,
                dayBox.getSelectedIndex() + 1));

        dispose();
    }

    private void onCancel() {
        callback.accept(null);
        dispose();
    }
}
