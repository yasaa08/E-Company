package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.LeaveDAO;
import com.mycompany.e.company.model.Leave;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class LeaveController implements Initializable {

    @FXML private TableView<Leave> tableLeaves;
    @FXML private TableColumn<Leave, String> colName;
    @FXML private TableColumn<Leave, String> colType;
    @FXML private TableColumn<Leave, String> colStart;
    @FXML private TableColumn<Leave, String> colEnd;
    @FXML private TableColumn<Leave, String> colReason;
    @FXML private TableColumn<Leave, String> colStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().leaveTypeProperty());
        colStart.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        colEnd.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        colReason.setCellValueFactory(cellData -> cellData.getValue().reasonProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void loadData() {
        int companyId = UserSession.getCompanyId();
        tableLeaves.setItems(LeaveDAO.getLeavesByCompany(companyId));
    }

    @FXML
    private void handleApprove() {
        processLeave("Approved");
    }

    @FXML
    private void handleReject() {
        processLeave("Rejected");
    }

    private void processLeave(String newStatus) {
        Leave selected = tableLeaves.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Data", "Pilih pengajuan cuti dari tabel terlebih dahulu!");
            return;
        }

        if (!selected.statusProperty().get().equals("Pending")) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Pengajuan ini sudah diproses (" + selected.statusProperty().get() + ").");
            return;
        }

        boolean success = LeaveDAO.updateLeaveStatus(selected.idProperty().get(), newStatus);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Status cuti berhasil diubah menjadi " + newStatus);
            loadData(); // Refresh tabel
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan pada database.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}