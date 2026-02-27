package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.config.koneksi; // Wajib import ini
import com.mycompany.e.company.dao.EmployeeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class InputEmployeeController {

    @FXML private TextField txtNama;
    @FXML private TextField txtJabatan;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TextField txtAnak;
    @FXML private TextField txtGaji;
    @FXML private DatePicker dpJoinDate;

    @FXML
    public void initialize() {
        // 1. Isi Pilihan Status
        cmbStatus.getItems().addAll("TK/0", "K/0", "K/1", "K/2", "K/3");

        // 2. Default Tanggal Hari Ini
        dpJoinDate.setValue(LocalDate.now());

        // 3. Filter Angka untuk Gaji & Anak
        UnaryOperator<TextFormatter.Change> filterAngka = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) return change;
            return null;
        };
        txtGaji.setTextFormatter(new TextFormatter<>(filterAngka));
        txtAnak.setTextFormatter(new TextFormatter<>(filterAngka));
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (txtNama.getText().isEmpty() || txtGaji.getText().isEmpty()) {
                showAlert("Peringatan", "Nama dan Gaji wajib diisi!");
                return;
            }

            String nama = txtNama.getText();
            String jabatan = txtJabatan.getText();
            String status = cmbStatus.getValue();
            if (status == null) status = "TK/0";

            double gaji = Double.parseDouble(txtGaji.getText());
            int anak = 0;
            if (!txtAnak.getText().isEmpty()) {
                anak = Integer.parseInt(txtAnak.getText());
            }

            // --- LOGIKA OTOMATIS REGION ---
            int companyId = UserSession.getCompanyId();
            int regionId = getCompanyRegion(companyId); // Ambil region asli perusahaan

            boolean success = EmployeeDAO.addEmployee(nama, jabatan, status, gaji, regionId, companyId, dpJoinDate.getValue());

            if (success) {
                showAlert("Sukses", "Data Karyawan Berhasil Disimpan!");
                closeWindow(event);
            } else {
                showAlert("Gagal", "Gagal menyimpan ke database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    // Method Helper buat cari Region ID
    private int getCompanyRegion(int companyId) {
        int regionId = 1; // Default aman
        try {
            Connection conn = koneksi.configDB();
            String sql = "SELECT region_id FROM companies WHERE company_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, companyId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                regionId = rs.getInt("region_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regionId;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}