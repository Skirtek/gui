package pl.czyz.jakub.models;

import pl.czyz.jakub.database.BrigadeManDbManager;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class Uzytkownik extends Pracownik {
    private Integer id;
    private String login;
    private String haslo;
    private String inicial;

    public Uzytkownik(String imie, String nazwisko, LocalDate dataUrodzenia, DzialPracownikow dzialPracownikow, String login, String haslo) {
        super(imie, nazwisko, dataUrodzenia, dzialPracownikow);

        this.login = login;
        this.haslo = haslo;
        setInicial();
    }

    public Uzytkownik(Integer id, Pracownik employee, String login, String haslo) {
        super(employee.getId(), employee.getImie(), employee.getNazwisko(), employee.getDataUrodzenia(), employee.getDzialPracownikow());

        this.id = id;
        this.login = login;
        this.haslo = haslo;
        setInicial();
    }

    public Uzytkownik(Pracownik employee, String login, String haslo) {
        super(employee.getId(), employee.getImie(), employee.getNazwisko(), employee.getDataUrodzenia(), employee.getDzialPracownikow());

        this.login = login;
        this.haslo = haslo;
        setInicial();
    }

    public void setUserId(Integer userId) {
        this.id = userId;
    }

    public Integer getUserId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getHaslo() {
        return haslo;
    }

    @Override
    public void setImie(String imie) {
        super.setImie(imie);
        setInicial();
    }

    @Override
    public void setNazwisko(String nazwisko) {
        super.setNazwisko(nazwisko);
        setInicial();
    }

    public String getInicial() {
        return inicial;
    }

    private void setInicial() {
        inicial = String.format("%s%s", getFirstLetter(getImie()), getFirstLetter(getNazwisko()));
    }

    private String getFirstLetter(String input) {
        return input.substring(0, 1);
    }

    public static DefaultTableModel getDerivedTableModel(List<Uzytkownik> data) {
        return new DefaultTableModel(getDataForTable(data), getColumns()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getColumnTypes()[columnIndex];
            }
        };
    }

    private static Class[] getColumnTypes() {
        return new Class[]{Boolean.class, Integer.class, String.class, String.class};
    }

    private static String[] getColumns() {
        return new String[]{"", "Id", "Login", "Hasło"};
    }

    private static Object[][] getDataForTable(List<Uzytkownik> data) {
        Object[][] rows = new Object[data.size()][4];

        for (int i = 0; i < data.size(); i++) {
            rows[i][0] = false;
            rows[i][1] = data.get(i).getUserId();
            rows[i][2] = data.get(i).getLogin();
            rows[i][3] = data.get(i).getHaslo();
        }

        return rows;
    }

    public PoziomUprawnien getPoziomUprawnien() {
        if (getLogin().equals("admin")) {
            return PoziomUprawnien.ADMINISTRATOR;
        } else if (BrigadeManDbManager.isBrigadeMan(getUserId())) {
            return PoziomUprawnien.BRYGADZISTA;
        } else {
            return PoziomUprawnien.PRACOWNIK;
        }
    }
}
