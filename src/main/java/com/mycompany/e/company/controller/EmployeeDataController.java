package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.EmployeeDAO;
import com.mycompany.e.company.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeDataController implements Initializable {

    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colJabatan;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TableColumn<Employee, Number> colAnak;
    @FXML private TableColumn<Employee, String> colJoinDate;
    @FXML private TableColumn<Employee, Number> colGaji;
    @FXML private TableColumn<Employee, String> colRegion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        colJabatan.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().maritalStatusProperty());
        colAnak.setCellValueFactory(cellData -> cellData.getValue().childCountProperty());
        colJoinDate.setCellValueFactory(cellData -> cellData.getValue().joinDateProperty());
        colGaji.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty());
        colRegion.setCellValueFactory(cellData -> cellData.getValue().regionProperty());
    }

    private void loadData() {
        int currentCompanyId = UserSession.getCompanyId();
        tableEmployees.setItems(EmployeeDAO.getEmployeesByCompany(currentCompanyId));
    }

    @FXML
    private void handleTambahKaryawan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InputEmployee.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Biar window belakang gabisa diklik
            stage.setTitle("Form Input Karyawan");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Tunggu sampai popup ditutup

            loadData(); // REFRESH TABEL setelah input selesai

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}