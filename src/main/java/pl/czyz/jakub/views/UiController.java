package pl.czyz.jakub.views;

import pl.czyz.jakub.database.DbDataManager;
import pl.czyz.jakub.database.DbManager;
import pl.czyz.jakub.models.Uzytkownik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UiController {
    public Uzytkownik Init(JFrame frame) {
        if (!DbManager.createConnection()) {
            // todo
            return null;
        }

        if (!DbDataManager.createTables()) {
            // todo
            return null;
        }

        Uzytkownik user = AuthView.authenticate(frame);

        if (user == null) {
            // todo
            return null;
        }

        return user;
    }
}
