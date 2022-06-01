package pl.czyz.jakub;

import javafx.scene.layout.Background;
import pl.czyz.jakub.database.*;
import pl.czyz.jakub.models.*;
import pl.czyz.jakub.views.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
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
    private JScrollPane scrollPanel;
    private JButton changePasswordButton;

    private final DepartmentController departmentController;
    private final EmployeeController employeeController;
    private final UsersController usersController;
    private final WorkController workController;
    private final BrigadeManController brigadeManController;
    private final BrigadeController brigadeController;
    private final OrderController orderController;

    private Uzytkownik loggedUser;

    private DataView currentDataView;

    public App() {
        departmentController = new DepartmentController();
        employeeController = new EmployeeController();
        usersController = new UsersController();
        workController = new WorkController();
        brigadeManController = new BrigadeManController();
        brigadeController = new BrigadeController();
        orderController = new OrderController();

        currentDataView = DataView.DEPARTMENT;

        initializeButtons();
    }

    public static void main(String[] args) {
        App instance = new App();
        frame = new JFrame("GUI");
        frame.setContentPane(instance.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DbManager.closeConnection();
            }
        });

        instance.welcomeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Border border = instance.welcomeLabel.getBorder();
        Border margin = new EmptyBorder(10, 10, 10, 10);
        instance.welcomeLabel.setBorder(new CompoundBorder(border, margin));

        instance.login();
    }

    private void login() {
        Uzytkownik user = new UiController().Init(frame);

        if (user == null) {
            return;
        }

        loggedUser = user;

        welcomeLabel.setText(String.format("Witaj %s", user.getInicial()));

        PoziomUprawnien poziomUprawnien = user.getPoziomUprawnien();

        changeUI(poziomUprawnien);

        if (poziomUprawnien == PoziomUprawnien.PRACOWNIK) {
            return;
        }

        dataTable.setShowGrid(true);
        dataTable.setShowVerticalLines(true);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setAutoCreateRowSorter(true);

        loadData();
    }

    private void changeUI(PoziomUprawnien poziomUprawnien) {
        changePasswordButton.setVisible(poziomUprawnien == PoziomUprawnien.PRACOWNIK);
        departmentButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        employeeButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        userButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        brigadeManButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        brigadeButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        orderButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        worksButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        scrollPanel.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        dataTable.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        newButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        editButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        removeButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
        additionalActionButton.setVisible(poziomUprawnien != PoziomUprawnien.PRACOWNIK);
    }

    private void logout() {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        welcomeLabel.setText("");

        login();
    }

    private void loadData() {
        DefaultTableModel model = null;

        setButtonBackgroundColor(departmentButton, DataView.DEPARTMENT);
        setButtonBackgroundColor(employeeButton, DataView.EMPLOYEE);
        setButtonBackgroundColor(userButton, DataView.USERS);
        setButtonBackgroundColor(brigadeManButton, DataView.BRIGADE_MAN);
        setButtonBackgroundColor(brigadeButton, DataView.BRIGADE);
        setButtonBackgroundColor(orderButton, DataView.ORDER);
        setButtonBackgroundColor(worksButton, DataView.WORK);

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
                List<Brygadzista> brigadeMan = BrigadeManDbManager.getBrigadeMan();
                model = Brygadzista.getBrigadeManTableModel(brigadeMan);
                break;
            case BRIGADE:
                List<Brygada> brigades = BrigadeDbManager.getBrigades(null);
                model = Brygada.getBrigadeTableModel(brigades);
                break;
            case ORDER:
                List<Zlecenie> orders = OrderDbManager.getOrders();
                model = Zlecenie.getTableModel(orders);
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
            case BRIGADE_MAN:
                additionalActionButton.setVisible(true);
                additionalActionButton.setText("Przypisane brygady");
                break;
            case BRIGADE:
                additionalActionButton.setVisible(true);
                additionalActionButton.setText("Przypisani pracownicy");
                break;
            case ORDER:
                additionalActionButton.setVisible(true);
                additionalActionButton.setText("Szczegóły");
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
                boolean result = orderController.getWorksForOrder(frame, dataTable);

                if (result) {
                    loadData();
                }
                break;
            case BRIGADE_MAN:
                brigadeManController.getAssignedBrigades(frame, dataTable);
                break;
            case BRIGADE:
                brigadeController.getAssignedEmployees(frame, dataTable);
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
                result = brigadeManController.addBrigadeMan();
                break;
            case BRIGADE:
                result = brigadeController.addBrigade(frame);
                break;
            case ORDER:
                result = orderController.addOrder(frame);
                break;
            case WORK:
                result = workController.addWork();
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
                result = brigadeManController.updateBrigadeMan(frame, dataTable);
                break;
            case BRIGADE:
                result = brigadeController.editBrigade(frame, dataTable);
                break;
            case ORDER:
                result = orderController.editOrder(frame, dataTable);
                break;
            case WORK:
                result = workController.editWork(frame, dataTable);
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
                result = brigadeManController.removeBrigadeMan(dataTable);
                break;
            case BRIGADE:
                result = brigadeController.removeBrigades(dataTable);
                break;
            case ORDER:
                result = orderController.removeOrder(dataTable);
                break;
            case WORK:
                result = workController.removeWork(dataTable);
                break;
        }

        if (result) {
            loadData();
        }
    }

    private void setButtonBackgroundColor(JButton button, DataView assignedDataView) {
        Color color = currentDataView == assignedDataView ? Color.CYAN : Color.LIGHT_GRAY;
        button.setBackground(color);
    }

    private void initializeButtons() {
        changePasswordButton.addActionListener(e -> {
            usersController.changePassword(frame, loggedUser.getUserId());
        });

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
