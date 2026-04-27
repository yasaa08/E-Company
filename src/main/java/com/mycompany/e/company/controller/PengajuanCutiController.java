package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.LeaveDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class PengajuanCutiController {

    // cmbKaryawan sudah dihapus!
    @FXML private ComboBox<String> cmbTipeCuti;
    @FXML private DatePicker dpMulai;
    @FXML private DatePicker dpSelesai;
    @FXML private TextArea txtAlasan;

    @FXML
    public void initialize() {
        // Cukup isi pilihan cuti dan tanggal, tidak perlu lagi meload nama
        cmbTipeCuti.getItems().addAll("Cuti Tahunan", "Cuti Sakit", "Izin Acara Keluarga", "Cuti Melahirkan", "Lainnya");
        dpMulai.setValue(LocalDate.now());
    }

    @FXML
    private void handleAjukan(ActionEvent event) {
        // AMBIL ID OTOMATIS DARI SESSION LOGIN (Tanpa perlu milih-milih nama)
        int empId = UserSession.getEmployeeId();

        String tipe = cmbTipeCuti.getValue();
        LocalDate mulai = dpMulai.getValue();
        LocalDate selesai = dpSelesai.getValue();
        String alasan = txtAlasan.getText();

        if (empId == 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Data sesi Karyawan tidak ditemukan! Coba relogin.");
            return;
        }

        if (tipe == null || mulai == null || selesai == null || alasan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Mohon lengkapi seluruh form pengajuan!");
            return;
        }

        if (selesai.isBefore(mulai)) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Tanggal", "Tanggal selesai tidak boleh sebelum tanggal mulai!");
            return;
        }

        // Simpan langsung ke database
        String hasil = LeaveDAO.ajukanCuti(empId, tipe, mulai, selesai, alasan);

        if (hasil.equals("SUKSES")) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Pengajuan berhasil dikirim! Silakan tunggu persetujuan Admin.");

            // Reset Form
            cmbTipeCuti.setValue(null);
            txtAlasan.setText("");
            dpMulai.setValue(LocalDate.now());
            dpSelesai.setValue(null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal dari Database", "Penyebab: " + hasil);
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