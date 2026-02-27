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

    private Employee selectedEmployee;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        loadEmployees();

        // Validasi Angka Saja
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
                txtGajiPokok.setText(String.format("%.0f", newVal.baseSalaryProperty().get()));

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

            txtBPJS.setText(String.format("%.0f", bpjs));
            txtPajak.setText(String.format("%.0f", pajak));
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
            double gapok = parse(txtGajiPokok.getText());
            double tunjangan = parse(txtTunjangan.getText());
            double lembur = parse(txtLembur.getText());
            double bpjs = parse(txtBPJS.getText());
            double pajak = parse(txtPajak.getText());
            double total = gapok + tunjangan + lembur - bpjs - pajak;

            String bulanIni = LocalDate.now().getMonth().toString() + " " + LocalDate.now().getYear();

            boolean sukses = PayrollDAO.savePayroll(
                    selectedEmployee.employeeIdProperty().get(),
                    bulanIni, gapok, tunjangan, lembur, bpjs, pajak, total
            );

            if (sukses) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Gaji berhasil dibayarkan!");
                handleReset(); // Reset setelah bayar
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Database error.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Format data salah.");
        }
    }

    private double parse(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            return Double.parseDouble(text.replace(",", "").replace("Rp", "").replace(".", "").trim());
        } catch (NumberFormatException e) { return 0; }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}