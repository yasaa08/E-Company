package com.mycompany.e.company.controller;

import com.mycompany.e.company.config.UserSession;
import com.mycompany.e.company.dao.PayrollDAO;
import com.mycompany.e.company.model.Payroll;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert; // Tambahan import
import javafx.scene.control.DialogPane; // Tambahan import
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class PayrollHistoryController implements Initializable {

    @FXML private TableView<Payroll> tablePayroll;
    @FXML private TableColumn<Payroll, Number> colID;
    @FXML private TableColumn<Payroll, String> colName;
    @FXML private TableColumn<Payroll, String> colBulan;
    @FXML private TableColumn<Payroll, Number> colGapok;
    @FXML private TableColumn<Payroll, Number> colPotongan;
    @FXML private TableColumn<Payroll, Number> colTotal;
    @FXML private TableColumn<Payroll, String> colStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();

        // 1. Pembuat Format Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // 2. Terapkan Rupiah ke Kolom Gaji Pokok
        colGapok.setCellFactory(column -> new TableCell<Payroll, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(formatRupiah.format(item.doubleValue())); }
            }
        });

        // 3. Terapkan Rupiah ke Kolom Potongan
        colPotongan.setCellFactory(column -> new TableCell<Payroll, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(formatRupiah.format(item.doubleValue())); }
            }
        });

        // 4. Terapkan Rupiah ke Kolom Total Bersih
        colTotal.setCellFactory(column -> new TableCell<Payroll, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(formatRupiah.format(item.doubleValue())); }
            }
        });

        // 5. Terapkan Warna dan Ikon ke Kolom Status
        colStatus.setCellFactory(column -> new TableCell<Payroll, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.equalsIgnoreCase("Lunas") ? "✔ " + item : "⏳ " + item);
                    if (item.equalsIgnoreCase("Lunas")) {
                        setStyle("-fx-text-fill: #00C853; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                    }
                }
            }
        });

        loadData();
    }

    @FXML
    private void handleExportExcel() {
        // Buka Jendela Save File
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Penggajian");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel/CSV File", "*.csv"));
        fileChooser.setInitialFileName("Laporan_Gaji_Bulan_Ini.csv");

        File file = fileChooser.showSaveDialog(tablePayroll.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Tulis Judul Kolom (Header)
                writer.println("ID,Nama Karyawan,Periode,Gaji Pokok,Potongan,Total Bersih,Status");

                // Looping data dan simpan ke file
                for (Payroll p : tablePayroll.getItems()) {
                    String baris = p.idProperty().get() + "," +
                            p.employeeNameProperty().get() + "," +
                            p.bulanProperty().get() + "," +
                            p.gajiPokokProperty().get() + "," +
                            p.potonganProperty().get() + "," +
                            p.totalGajiProperty().get() + "," +
                            p.statusProperty().get();

                    writer.println(baris);
                }

                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan berhasil diexport ke Excel!\nLokasi: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Gagal Export", "Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    private void setupTable() {
        colID.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        colBulan.setCellValueFactory(cellData -> cellData.getValue().bulanProperty());
        colGapok.setCellValueFactory(cellData -> cellData.getValue().gajiPokokProperty());
        colPotongan.setCellValueFactory(cellData -> cellData.getValue().potonganProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalGajiProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void loadData() {
        int companyId = UserSession.getCompanyId();
        tablePayroll.setItems(PayrollDAO.getPayrollByCompany(companyId));
    }

    // Fungsi tambahan untuk memunculkan notifikasi
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