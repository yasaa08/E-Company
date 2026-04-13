package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.config.koneksi;
import com.mycompany.e.company.dao.EmployeeDAO;
import com.mycompany.e.company.model.Employee;
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
    @FXML private TextField txtGaji;
    @FXML private DatePicker dpJoinDate;
    @FXML private TextField txtRekening;
    @FXML private TextField txtWa;

    // Variabel penanda apakah sedang edit atau tambah baru
    private Integer idKaryawanEdit = null;

    @FXML
    public void initialize() {
        cmbStatus.getItems().addAll("TK/0", "K/0", "K/1", "K/2", "K/3");
        dpJoinDate.setValue(LocalDate.now());

        UnaryOperator<TextFormatter.Change> filterAngka = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) return change;
            return null;
        };
        txtGaji.setTextFormatter(new TextFormatter<>(filterAngka));
    }

    // Fungsi baru untuk memasukkan data yang mau diedit ke form
    public void setEditMode(Employee emp) {
        idKaryawanEdit = emp.employeeIdProperty().get(); // Tandai ID yang sedang diedit

        txtNama.setText(emp.fullNameProperty().get());
        txtJabatan.setText(emp.positionProperty().get());
        cmbStatus.setValue(emp.maritalStatusProperty().get());
        txtGaji.setText(String.format("%.0f", emp.baseSalaryProperty().get()));
        txtRekening.setText(emp.accountNumberProperty().get());
        txtWa.setText(emp.phoneNumberProperty().get());

        // Cek dan set tanggal jika ada
        String tgl = emp.joinDateProperty().get();
        if (tgl != null && !tgl.equals("-") && !tgl.isEmpty()) {
            dpJoinDate.setValue(LocalDate.parse(tgl));
        }
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
            String status = cmbStatus.getValue() != null ? cmbStatus.getValue() : "TK/0";
            double gaji = Double.parseDouble(txtGaji.getText());
            String noRek = txtRekening.getText() != null ? txtRekening.getText() : "";
            String noHp = txtWa.getText() != null ? txtWa.getText() : "";

            int companyId = UserSession.getCompanyId();
            int regionId = getCompanyRegion(companyId);

            boolean success = false;

            // CEK: Apakah ini tambah baru atau edit lama?
            if (idKaryawanEdit == null) {
                // Proses Tambah Baru
                success = EmployeeDAO.addEmployee(nama, jabatan, status, gaji, regionId, companyId, dpJoinDate.getValue(), noRek, noHp);
            } else {
                // Proses Edit/Update (Pakai ID Karyawan yang disimpan di setEditMode)
                success = EmployeeDAO.updateEmployee(idKaryawanEdit, nama, jabatan, status, gaji, regionId, dpJoinDate.getValue(), noRek, noHp);
            }

            if (success) {
                showAlert("Sukses", idKaryawanEdit == null ? "Karyawan Baru Berhasil Disimpan!" : "Data Karyawan Berhasil Diupdate!");
                closeWindow(event);
            } else {
                showAlert("Gagal", "Gagal menyimpan ke database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    private int getCompanyRegion(int companyId) {
        int regionId = 1;
        try {
            Connection conn = koneksi.configDB();
            String sql = "SELECT region_id FROM companies WHERE company_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, companyId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) regionId = rs.getInt("region_id");
        } catch (Exception e) {}
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