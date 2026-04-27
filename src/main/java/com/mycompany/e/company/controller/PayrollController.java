package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.EmployeeDAO;
import com.mycompany.e.company.dao.PayrollDAO;
import com.mycompany.e.company.model.Employee;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class PayrollController {

    @FXML private ListView<Employee> listEmployees;
    @FXML private Label lblNama;
    @FXML private TextField txtGajiPokok, txtTunjangan, txtLembur, txtBPJS, txtPajak;
    @FXML private Label lblTotalGaji;
    @FXML private Label lblRekening;

    private Employee selectedEmployee;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        loadEmployees();

        // Validasi Angka Saja untuk input Tunjangan & Lembur
        UnaryOperator<TextFormatter.Change> filterAngka = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) return change;
            return null;
        };

        txtTunjangan.setTextFormatter(new TextFormatter<>(filterAngka));
        txtLembur.setTextFormatter(new TextFormatter<>(filterAngka));

        // Listener Pilih Karyawan
        listEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedEmployee = newVal;
                lblNama.setText(newVal.fullNameProperty().get());

                // Mencegah munculnya huruf "E" (Scientific Notation) dengan mencetak angka bulat murni
                txtGajiPokok.setText(String.format("%.0f", newVal.baseSalaryProperty().get()));

                lblRekening.setText(newVal.accountNumberProperty().get());

                // Reset inputan saat ganti orang
                handleReset();
            }
        });

        listEmployees.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.fullNameProperty().get() + " (" + item.positionProperty().get() + ")");
            }
        });
    }

    private void loadEmployees() {
        int companyId = UserSession.getCompanyId();
        ObservableList<Employee> data = EmployeeDAO.getEmployeesByCompany(companyId);
        listEmployees.setItems(data);
    }

    @FXML
    private void hitungTotal() {
        if (selectedEmployee == null) return;
        try {
            double gapok = parse(txtGajiPokok.getText());
            double tunjangan = parse(txtTunjangan.getText());
            double lembur = parse(txtLembur.getText());

            double bruto = gapok + tunjangan + lembur;

            // Hitung Potongan
            double bpjs = gapok * 0.03;
            double pajak = (bruto > 4500000) ? (bruto - 4500000) * 0.05 : 0;
            double netto = bruto - bpjs - pajak;

            // Mencegah notasi ilmiah pada potongan
            txtBPJS.setText(String.format("%.0f", bpjs));
            txtPajak.setText(String.format("%.0f", pajak));

            // Total gaji tampil cantik dengan format Rp
            lblTotalGaji.setText(currency.format(netto));

        } catch (Exception e) {}
    }

    // --- FITUR BARU: RESET FORM ---
    @FXML
    private void handleReset() {
        txtTunjangan.setText("0");
        txtLembur.setText("0");
        hitungTotal(); // Hitung ulang supaya total gaji kembali bersih
    }

    @FXML
    private void handleBayar() {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih karyawan terlebih dahulu!");
            return;
        }

        try {
            // Hitung total manual dari textfield agar data masuk ke DB akurat
            double gapok = parse(txtGajiPokok.getText());
            double tunjangan = parse(txtTunjangan.getText());
            double lembur = parse(txtLembur.getText());
            double bpjs = parse(txtBPJS.getText());
            double pajak = parse(txtPajak.getText());
            double total = gapok + tunjangan + lembur - bpjs - pajak;

            String noRek = selectedEmployee.accountNumberProperty().get();
            String noWa = selectedEmployee.phoneNumberProperty().get();
            String nama = selectedEmployee.fullNameProperty().get();

            // 1. Tampilkan Dialog Konfirmasi
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi Pembayaran");
            confirm.setHeaderText("Transfer ke: " + noRek);
            confirm.setContentText("Total Bayar: " + lblTotalGaji.getText() + "\n\nPastikan Anda sudah transfer. Lanjut simpan dan kirim WA?");

            if (confirm.showAndWait().get() == ButtonType.OK) {

                // 2. Simpan ke Database
                String bulanIni = LocalDate.now().getMonth().toString() + " " + LocalDate.now().getYear();
                boolean sukses = PayrollDAO.savePayroll(
                        selectedEmployee.employeeIdProperty().get(),
                        bulanIni, gapok, tunjangan, lembur, bpjs, pajak, total
                );

                if (sukses) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data gaji berhasil disimpan!");

                    // 3. Logika Otomatis WA
                    bukaWhatsApp(noWa, nama, lblTotalGaji.getText());

                    handleReset(); // Reset layar jika sudah sukses
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Database Error: Gagal menyimpan riwayat gaji.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Sistem", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    // Method untuk buka WA otomatis yang sudah dirapikan & kebal error
    private void bukaWhatsApp(String noHp, String nama, String total) {
        try {
            if (noHp == null || noHp.isEmpty() || noHp.equals("-") || noHp.equals("")) {
                showAlert(Alert.AlertType.WARNING, "WA Dilewati", "Nomor WA kosong. Data gaji disimpan, tapi notifikasi WA dibatalkan.");
                return;
            }

            // Bersihkan format agar sesuai API WhatsApp
            noHp = noHp.replaceAll("[^0-9+]", "");
            if (noHp.startsWith("0")) {
                noHp = "62" + noHp.substring(1);
            }

            String pesan = "Halo " + nama + ", ini adalah bukti gaji kamu sebesar " + total + ". Cek aplikasi E-Company kamu ya.";
            String url = "https://api.whatsapp.com/send?phone=" + noHp + "&text=" + java.net.URLEncoder.encode(pesan, "UTF-8").replace("+", "%20");

            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error WA", "Gagal membuka WhatsApp: " + e.getMessage());
        }
    }

    // Helper untuk mengubah teks kembali ke angka Double
    private double parse(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            return Double.parseDouble(text.replace(",", "").replace("Rp", "").replace(".", "").trim());
        } catch (NumberFormatException e) { return 0; }
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