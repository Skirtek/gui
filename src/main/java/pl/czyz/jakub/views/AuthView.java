package pl.czyz.jakub.views;

import pl.czyz.jakub.database.BrigadeManDbManager;
import pl.czyz.jakub.database.UsersDbManager;
import pl.czyz.jakub.models.PoziomUprawnien;
import pl.czyz.jakub.models.Uzytkownik;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class AuthView {
    public static Uzytkownik authenticate(JFrame frame) {
        JTextField login = new JTextField();
        JTextField password = new JPasswordField();

        Object[] fields = {"Login:", login, "Hasło:", password};

        int option = JOptionPane.showOptionDialog(null,
                fields,
                "Zaloguj się",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"Zaloguj się", "Zamknij"},
                null);

        if (option != JOptionPane.OK_OPTION) {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            return null;
        }

        Uzytkownik user = UsersDbManager.getUser(login.getText(), password.getText());

        if (user == null) {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            return null;
        }

        return user;
    }
}
