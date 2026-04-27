package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.koneksi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class RegisterController {

    @FXML private TextField txtCompanyName;
    @FXML private TextArea txtCompanyAddress;
    @FXML private ComboBox<String> cmbRegion;
    @FXML private TextField txtAdminUser;
    @FXML private PasswordField txtAdminPass;

    @FXML
    public void initialize() {
        // 1. Bersihkan combobox agar tidak ada sisa data lama
        cmbRegion.getItems().clear();

        // 2. Ambil data langsung dari Database Aiven
        try (Connection conn = koneksi.configDB()) {
            Statement stmt = conn.createStatement();

            // Query untuk mengambil semua kota dan diurutkan sesuai abjad (A-Z)
            ResultSet rs = stmt.executeQuery("SELECT region_id, region_name FROM regions ORDER BY region_name ASC");

            while (rs.next()) {
                int id = rs.getInt("region_id");
                String nama = rs.getString("region_name");

                // Masukkan ke combobox dengan format: "ID - Nama Kota"
                cmbRegion.getItems().add(id + " - " + nama);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat daftar kota: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String namaPT = txtCompanyName.getText();
        String alamatPT = txtCompanyAddress.getText();
        String regionRaw = cmbRegion.getValue();
        String userAdmin = txtAdminUser.getText();
        String passAdmin = txtAdminPass.getText();

        if (namaPT.isEmpty() || userAdmin.isEmpty() || regionRaw == null) {
            showAlert("Error", "Mohon lengkapi semua data!");
            return;
        }

        try {
            Connection conn = koneksi.configDB();

            // Ambil ID Region (Memecah tulisan "1 - DKI Jakarta" mengambil angka 1)
            int regionId = Integer.parseInt(regionRaw.split(" - ")[0]);

            // 1. Simpan Company (Nama, Alamat, Region ID)
            String sqlPT = "INSERT INTO companies (company_name, company_address, region_id) VALUES (?, ?, ?)";
            PreparedStatement pstPT = conn.prepareStatement(sqlPT, Statement.RETURN_GENERATED_KEYS);
            pstPT.setString(1, namaPT);
            pstPT.setString(2, alamatPT);
            pstPT.setInt(3, regionId);
            pstPT.executeUpdate();

            ResultSet rsPT = pstPT.getGeneratedKeys();
            int newCompanyID = 0;
            if (rsPT.next()) {
                newCompanyID = rsPT.getInt(1);
            }

            // 2. Buat Akun Admin
            String sqlUser = "INSERT INTO users (username, password, role, company_id) VALUES (?, ?, 'admin', ?)";
            PreparedStatement pstUser = conn.prepareStatement(sqlUser);
            pstUser.setString(1, userAdmin);
            pstUser.setString(2, passAdmin);
            pstUser.setInt(3, newCompanyID);
            pstUser.executeUpdate();

            showAlert("Sukses", "Registrasi Berhasil!");
            handleBackToLogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal Daftar: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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