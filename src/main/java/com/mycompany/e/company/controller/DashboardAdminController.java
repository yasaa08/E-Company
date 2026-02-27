package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.dao.EmployeeDAO;
import com.mycompany.e.company.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardAdminController implements Initializable {

    @FXML private BorderPane mainBorderPane;
    @FXML private VBox dashboardContent;

    // --- Tabel di Dashboard (Preview) ---
    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colJabatan;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TableColumn<Employee, Number> colGaji;
    @FXML private TableColumn<Employee, String> colRegion;

    // --- Label Statistik ---
    @FXML private Label lblCompanyName;
    @FXML private Label lblUmrValue;
    @FXML private Label lblRegionName;
    @FXML private Label lblTotalEmployees;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();

        // LOGIC BARU: Kalau tabel diklik, pindah ke halaman "Data Karyawan" yang lengkap
        tableEmployees.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 || event.getClickCount() == 2) {
                // Panggil method pindah halaman
                handleShowEmployeeData(null);
            }
        });
    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        colJabatan.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().maritalStatusProperty());
        colGaji.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty());
        colRegion.setCellValueFactory(cellData -> cellData.getValue().regionProperty());
    }

    private void loadData() {
        int currentCompanyId = UserSession.getCompanyId();
        loadDashboardStats(currentCompanyId);

        // Load data karyawan ke tabel dashboard
        tableEmployees.setItems(EmployeeDAO.getEmployeesByCompany(currentCompanyId));
    }

    private void loadDashboardStats(int companyId) {
        try {
            Connection conn = koneksi.configDB();

            // 1. Info Company & UMR
            String sqlInfo = "SELECT c.company_name, r.region_name, r.umr_value " +
                    "FROM companies c " +
                    "JOIN regions r ON c.region_id = r.region_id " +
                    "WHERE c.company_id = ?";
            PreparedStatement pst = conn.prepareStatement(sqlInfo);
            pst.setInt(1, companyId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblCompanyName.setText(rs.getString("company_name"));
                lblRegionName.setText(rs.getString("region_name"));
                double umr = rs.getDouble("umr_value");
                NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                lblUmrValue.setText(formatRp.format(umr));
            }

            // 2. Total Employees
            String sqlCount = "SELECT COUNT(*) as total FROM employees WHERE company_id = ?";
            PreparedStatement pstCount = conn.prepareStatement(sqlCount);
            pstCount.setInt(1, companyId);
            ResultSet rsCount = pstCount.executeQuery();

            if (rsCount.next()) {
                lblTotalEmployees.setText(rsCount.getInt("total") + " Orang");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- NAVIGASI SIDEBAR ---

    @FXML
    private void handleShowDashboard(ActionEvent event) {
        if (mainBorderPane != null && dashboardContent != null) {
            mainBorderPane.setCenter(dashboardContent);
            loadData(); // Refresh data dashboard
        }
    }

    @FXML
    private void handleShowEmployeeData(ActionEvent event) {
        loadPage("/EmployeeDataView.fxml");
    }

    @FXML
    private void handleShowPayroll(ActionEvent event) {
        loadPage("/PayrollView.fxml");
    }

    // Helper load page
    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText(null);
        alert.setContentText("Yakin ingin keluar?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}