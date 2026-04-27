package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.config.koneksi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView; // INI IMPORT YANG HILANG TADI
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class DashboardEmployeeController {

    @FXML private BorderPane mainBorderPane;
    @FXML private VBox contentArea;
    @FXML private ListView<String> listKalenderLibur;

    // Label untuk statistik
    @FXML private Label lblUsername, lblJabatan, lblLokasi, lblStatusGaji, lblTotalCuti;

    @FXML
    public void initialize() {
        if (lblUsername != null) {
            lblUsername.setText(UserSession.getUsername());
        }

        // Pindahkan pengisian kalender ke sini agar langsung muncul saat aplikasi dibuka
        if (listKalenderLibur != null) {
            listKalenderLibur.getItems().clear();
            listKalenderLibur.getItems().addAll(
                    "🔴 1 Mei - Hari Buruh Internasional",
                    "🔴 14 Mei - Kenaikan Isa Al Masih",
                    "🔴 24 Mei - Hari Raya Waisak",
                    "🔴 1 Juni - Hari Lahir Pancasila",
                    "🔴 19 Juni - Tahun Baru Islam",
                    "🔴 17 Agustus - Hari Kemerdekaan RI",
                    "🔴 25 Desember - Hari Raya Natal",
                    "🟢 (Dan cuti bersama lainnya sesuai SKB 3 Menteri)"
            );
        }

        loadEmployeeStats();
    }

    private void loadEmployeeStats() {
        int empId = UserSession.getEmployeeId();
        String bulanIni = LocalDate.now().getMonth().toString() + " " + LocalDate.now().getYear();

        try (Connection conn = koneksi.configDB()) {
            // 1. Ambil Jabatan & Lokasi
            String sqlInfo = "SELECT e.position, r.region_name FROM employees e JOIN regions r ON e.region_id = r.region_id WHERE e.employee_id = ?";
            PreparedStatement pstInfo = conn.prepareStatement(sqlInfo);
            pstInfo.setInt(1, empId);
            ResultSet rsInfo = pstInfo.executeQuery();
            if (rsInfo.next()) {
                if (lblJabatan != null) lblJabatan.setText(rsInfo.getString("position"));
                if (lblLokasi != null) lblLokasi.setText(rsInfo.getString("region_name"));
            }

            // 2. Cek Status Gaji Bulan Ini
            String sqlGaji = "SELECT status FROM payrolls WHERE employee_id = ? AND bulan_tahun = ?";
            PreparedStatement pstGaji = conn.prepareStatement(sqlGaji);
            pstGaji.setInt(1, empId);
            pstGaji.setString(2, bulanIni);
            ResultSet rsGaji = pstGaji.executeQuery();
            if (lblStatusGaji != null) lblStatusGaji.setText(rsGaji.next() ? "✔ Sudah Dibayar" : "⏳ Belum Dibayar");

            // 3. Hitung Total Cuti yang Disetujui
            String sqlCuti = "SELECT COUNT(*) as total FROM leaves WHERE employee_id = ? AND status = 'Approved'";
            PreparedStatement pstCuti = conn.prepareStatement(sqlCuti);
            pstCuti.setInt(1, empId);
            ResultSet rsCuti = pstCuti.executeQuery();
            if (rsCuti.next() && lblTotalCuti != null) {
                lblTotalCuti.setText(rsCuti.getInt("total") + " Kali");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =======================================================
    // --- FUNGSI NAVIGASI TOMBOL ---
    // =======================================================

    @FXML
    private void handleShowDashboard(ActionEvent event) {
        mainBorderPane.setCenter(contentArea);
    }

    @FXML
    private void handleShowPengajuanCuti(ActionEvent event) {
        loadPage("/PengajuanCutiView.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Yakin ingin keluar?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}