package pl.czyz.jakub.views;

import pl.czyz.jakub.database.DbDataManager;
import pl.czyz.jakub.database.DbManager;
import pl.czyz.jakub.models.Uzytkownik;

import javax.swing.*;

public class UiController {
    public Uzytkownik Init(JFrame frame) {
        if (!DbManager.createConnection()) {
            return null;
        }

        if (!DbDataManager.createTables()) {
            return null;
        }

        return AuthView.authenticate(frame);
    }
}
