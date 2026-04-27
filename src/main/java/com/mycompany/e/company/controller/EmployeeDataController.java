package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.EmployeeDAO;
import com.mycompany.e.company.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class EmployeeDataController implements Initializable {

    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colJabatan;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TableColumn<Employee, String> colJoinDate;
    @FXML private TableColumn<Employee, Number> colGaji;
    @FXML private TableColumn<Employee, String> colRegion;
    @FXML private TableColumn<Employee, String> colRekening;
    @FXML private TableColumn<Employee, String> colWa;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        colJabatan.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().maritalStatusProperty());
        colJoinDate.setCellValueFactory(cellData -> cellData.getValue().joinDateProperty());
        colGaji.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty());
        colRegion.setCellValueFactory(cellData -> cellData.getValue().regionProperty());
        colRekening.setCellValueFactory(cellData -> cellData.getValue().accountNumberProperty());
        colWa.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
    }

    private void loadData() {
        int currentCompanyId = UserSession.getCompanyId();
        tableEmployees.setItems(EmployeeDAO.getEmployeesByCompany(currentCompanyId));
    }

    @FXML
    private void handleTambahKaryawan() {
        bukaFormKaryawan(null); // Kirim null karena mau tambah baru
    }

    @FXML
    private void handleEditKaryawan() {
        Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Data", "Pilih karyawan yang ingin diedit dari tabel.");
            return;
        }
        bukaFormKaryawan(selected); // Kirim data karyawan yang dipilih
    }

    @FXML
    private void handleHapusKaryawan() {
        Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Data", "Pilih karyawan yang ingin dihapus.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Karyawan: " + selected.fullNameProperty().get());
        confirm.setContentText("Yakin ingin menghapus data ini? Data yang dihapus tidak bisa dikembalikan.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean sukses = EmployeeDAO.deleteEmployee(selected.employeeIdProperty().get());
            if (sukses) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil dihapus.");
                loadData(); // Refresh tabel
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus data dari database.");
            }
        }
    }

    private void bukaFormKaryawan(Employee employeeToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InputEmployee.fxml"));
            Parent root = loader.load();

            // Ambil controller dari form input
            InputEmployeeController controller = loader.getController();

            // Jika dikirim data karyawan (Edit), set data ke form
            if (employeeToEdit != null) {
                controller.setEditMode(employeeToEdit);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(employeeToEdit == null ? "Form Input Karyawan" : "Edit Data Karyawan");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData(); // Refresh tabel setelah popup ditutup

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        try {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
        alert.showAndWait();
    }
}