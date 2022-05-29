package pl.czyz.jakub;

import pl.czyz.jakub.database.*;
import pl.czyz.jakub.models.*;
import pl.czyz.jakub.views.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class App {
    private static JFrame frame;

    private JPanel panelMain;
    private JButton newButton;
    private JButton removeButton;
    private JButton editButton;
    private JButton additionalActionButton;
    private JButton logoutButton;
    private JButton worksButton;
    private JButton orderButton;
    private JButton brigadeButton;
    private JButton departmentButton;
    private JButton brigadeManButton;
    private JButton userButton;
    private JButton employeeButton;
    private JTable dataTable;
    private JLabel welcomeLabel;

    private final DepartmentController departmentController;
    private final EmployeeController employeeController;
    private final UsersController usersController;
    private final WorkController workController;

    private DataView currentDataView;

    public App() {
        departmentController = new DepartmentController();
        employeeController = new EmployeeController();
        usersController = new UsersController();
        workController = new WorkController();

        currentDataView = DataView.DEPARTMENT;

        initializeButtons();
    }

    public static void main(String[] args) {
        App instance = new App();
        frame = new JFrame("GUI");
        frame.setContentPane(instance.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DbManager.closeConnection();
            }
        });

        instance.welcomeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        instance.login();
    }

    private void login() {
        Uzytkownik user = new UiController().Init(frame);

        if (user == null) {
            return;
        }

        welcomeLabel.setText(String.format("Witaj %s", user.getInicial()));

        dataTable.setShowGrid(true);
        dataTable.setShowVerticalLines(true);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setAutoCreateRowSorter(true);

        loadData();
    }

    private void logout() {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        welcomeLabel.setText("");

        login();
    }

    private void loadData() {
        DefaultTableModel model = null;

        handleAdditionalActionButton();

        switch (currentDataView) {
            case DEPARTMENT:
                List<DzialPracownikow> departments = DepartmentDbManager.getDepartments();
                model = DzialPracownikow.getTableModel(departments);
                break;
            case EMPLOYEE:
                List<Pracownik> employees = EmployeeDbManager.getEmployee();
                model = Pracownik.getTableModel(employees);
                break;
            case USERS:
                List<Uzytkownik> users = UsersDbManager.getUsers();
                model = Uzytkownik.getDerivedTableModel(users);
                break;
            case BRIGADE_MAN:
                break;
            case BRIGADE:
                break;
            case ORDER:
                break;
            case WORK:
                List<Praca> works = WorkDbDataManager.getWorks();
                model = Praca.getTableModel(works);
                break;
        }

        dataTable.setModel(model);
    }

    private void handleAdditionalActionButton() {
        switch (currentDataView) {
            case DEPARTMENT:
                additionalActionButton.setVisible(true);
                additionalActionButton.setText("Pracownicy działu");
                break;
            case ORDER:
                additionalActionButton.setVisible(true);
                additionalActionButton.setText("Powiązana praca");
                break;
            default:
                additionalActionButton.setVisible(false);
                break;
        }
    }

    private void additionalAction() {
        switch (currentDataView) {
            case DEPARTMENT:
                departmentController.getEmployeesForDepartment(frame, dataTable);
                break;
            case ORDER:
                break;
            default:
                break;
        }
    }

    private void addData() {
        boolean result = false;

        switch (currentDataView) {
            case DEPARTMENT:
                result = departmentController.addDepartment(frame);
                break;
            case EMPLOYEE:
                result = employeeController.addEmployee(frame);
                break;
            case USERS:
                result = usersController.addUser(frame);
                break;
            case BRIGADE_MAN:
                break;
            case BRIGADE:
                break;
            case ORDER:
                break;
            case WORK:
                result = workController.addUser(frame);
                break;
        }

        if (result) {
            loadData();
        }
    }

    private void editData() {
        boolean result = false;

        switch (currentDataView) {
            case DEPARTMENT:
                result = departmentController.editDepartment(frame, dataTable);
                break;
            case EMPLOYEE:
                result = employeeController.editEmployee(frame, dataTable);
                break;
            case USERS:
                result = usersController.editUser(frame, dataTable);
                break;
            case BRIGADE_MAN:
                break;
            case BRIGADE:
                break;
            case ORDER:
                break;
            case WORK:
                break;
        }

        if (result) {
            loadData();
        }
    }

    private void removeData() {
        boolean result = false;

        switch (currentDataView) {
            case DEPARTMENT:
                result = departmentController.removeDepartment(dataTable);
                break;
            case EMPLOYEE:
                result = employeeController.removeEmployee(dataTable);
                break;
            case USERS:
                result = usersController.removeUsers(dataTable);
                break;
            case BRIGADE_MAN:
                break;
            case BRIGADE:
                break;
            case ORDER:
                break;
            case WORK:
                break;
        }

        if (result) {
            loadData();
        }
    }

    private void initializeButtons() {
        newButton.addActionListener(e -> addData());

        editButton.addActionListener(e -> editData());

        removeButton.addActionListener(e -> removeData());

        additionalActionButton.addActionListener(e -> additionalAction());

        departmentButton.addActionListener(e -> {
            currentDataView = DataView.DEPARTMENT;
            loadData();
        });

        employeeButton.addActionListener(e -> {
            currentDataView = DataView.EMPLOYEE;
            loadData();
        });

        userButton.addActionListener(e -> {
            currentDataView = DataView.USERS;
            loadData();
        });

        brigadeManButton.addActionListener(e -> {
            currentDataView = DataView.BRIGADE_MAN;
            loadData();
        });

        brigadeButton.addActionListener(e -> {
            currentDataView = DataView.BRIGADE;
            loadData();
        });

        orderButton.addActionListener(e -> {
            currentDataView = DataView.ORDER;
            loadData();
        });

        worksButton.addActionListener(e -> {
            currentDataView = DataView.WORK;
            loadData();
        });

        logoutButton.addActionListener(e -> logout());
    }
}
