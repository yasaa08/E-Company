package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.PayrollDAO;
import com.mycompany.e.company.model.Payroll;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TableCell;

public class PayrollHistoryController implements Initializable {

    @FXML private TableView<Payroll> tablePayroll;
    @FXML private TableColumn<Payroll, Number> colID;
    @FXML private TableColumn<Payroll, String> colName;
    @FXML private TableColumn<Payroll, String> colBulan;
    @FXML private TableColumn<Payroll, Number> colGapok;
    @FXML private TableColumn<Payroll, Number> colPotongan;
    @FXML private TableColumn<Payroll, Number> colTotal;
    @FXML private TableColumn<Payroll, String> colStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        colStatus.setCellFactory(column -> new TableCell<Payroll, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.equalsIgnoreCase("Lunas") ? "✔ " + item : "✘ " + item);

                    // Gunakan inline CSS atau lebih baik CSS Class
                    if (item.equalsIgnoreCase("Lunas")) {
                        setStyle("-fx-text-fill: #00C853; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                    }
                }
            }
        });
        loadData();
    }

    private void setupTable() {
        colID.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        colBulan.setCellValueFactory(cellData -> cellData.getValue().bulanProperty());
        colGapok.setCellValueFactory(cellData -> cellData.getValue().gajiPokokProperty());
        colPotongan.setCellValueFactory(cellData -> cellData.getValue().potonganProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalGajiProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void loadData() {
        int companyId = UserSession.getCompanyId();
        tablePayroll.setItems(PayrollDAO.getPayrollByCompany(companyId));
    }
}